package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.app.FragmentManager.FragmentLifecycleCallbacks;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import io.bitrise.trace.TraceSdk;
import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FragmentData;
import io.bitrise.trace.data.dto.FragmentDataStateEntry;
import io.bitrise.trace.data.dto.FragmentState;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.data.trace.TraceManager;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.TraceException;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 * Class used by the {@link TraceSdk} that uses Android's FragmentLifecycleCallbacks to monitor
 * Fragment lifecycle changes. This class is also a {@link TraceActivityLifecycleSink}, as it
 * needs to be aware of Activity lifecycle changes, to register the FragmentLifecycleCallbacks on
 * them. This also needs the running {@link ActivityStateDataListener} to extract the required
 * {@link ActivityData} from it.
 */
public class FragmentStateDataListener extends FragmentManager.FragmentLifecycleCallbacks implements
    TraceActivityLifecycleSink, DataListener {

  /**
   * The map that should store every Fragment lifecycle event linked to Activities. Should
   * flush the corresponding data, when an Activity is shut down.
   */
  @VisibleForTesting
  @NonNull
  Map<Integer, Map<Integer, FragmentData>> activityFragmentMap;
  /**
   * The {@link TraceActivityLifecycleTracker} to register itself to it's lifecycle event callbacks.
   */
  @NonNull
  private final TraceActivityLifecycleTracker traceActivityLifecycleTracker;
  /**
   * The {@link ActivityStateDataListener} for the class. Required to get the Span IDs of the
   * hosting Activities.
   */
  @NonNull
  private final ActivityStateDataListener activityStateDataListener;
  /**
   * The {@link TraceManager} for this class, for getting the Span IDs.
   */
  @NonNull
  private final TraceManager traceManager;
  /**
   * The hashcode for the activity currently active.
   */
  @VisibleForTesting
  protected int activeActivityHashCode;
  /**
   * The {@link DataManager} to handle the collected {@link Data}.
   */
  @NonNull
  DataManager dataManager;
  /**
   * Indicates that this {@link DataListener} is active or not.
   */
  private boolean isActive;
  /**
   * Lifecycle callbacks instance for code, that use
   * {@link android.app.FragmentManager.FragmentLifecycleCallbacks}, which is deprecated, and
   * {@link androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks} is recommended.
   * Although it is deprecated, we still provide it, so users still using it will be able to use
   * this too.
   */
  @Singleton
  @Nullable
  private volatile android.app.FragmentManager.FragmentLifecycleCallbacks
      deprecatedFragmentCallbackTracker;

  /**
   * Constructor for class.
   *
   * @param context                   the Android Context.
   * @param activityStateDataListener the {@link ActivityStateDataListener}.
   */
  public FragmentStateDataListener(@NonNull final Context context,
                                   @NonNull
                                   final ActivityStateDataListener activityStateDataListener) {
    this.dataManager = DataManager.getInstance(context);
    this.traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(context);
    this.activityFragmentMap = new HashMap<>();
    this.activityStateDataListener = activityStateDataListener;
    this.traceManager = ApplicationTraceManager.getInstance(context);
  }

  @Override
  public boolean isActive() {
    return isActive;
  }

  @Override
  public void onActivityCreated(@NonNull final Activity activity,
                                @Nullable final Bundle savedInstanceState) {
    if (!isActive()) {
      return;
    }

    //noinspection ConstantConditions
    if (activity instanceof FragmentActivity) {
      final FragmentActivity fragmentActivity = (FragmentActivity) activity;
      fragmentActivity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(this, true);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      activity.getFragmentManager().registerFragmentLifecycleCallbacks(
          getDeprecatedFragmentCallbackTracker(), true);
    } else {
      TraceLog.w(
          new TraceException.FragmentLifecycleCallbackException(activity.getClass().getName()),
          LogMessageConstants.COULD_NOT_REGISTER_FRAGMENT_LIFECYCLE_CALLBACK);
    }
  }

  @Override
  public void onActivityStarted(@NonNull final Activity activity) {
    activeActivityHashCode = activity.hashCode();
  }

  @Override
  public void onActivityResumed(@NonNull final Activity activity) {
    // nop
  }

  @Override
  public void onActivityPaused(@NonNull final Activity activity) {
    // nop
  }

  @Override
  public void onActivityStopped(@NonNull final Activity activity) {
    if (!isActive()) {
      return;
    }

    //noinspection ConstantConditions
    if (activity instanceof FragmentActivity) {
      final FragmentActivity fragmentActivity = (FragmentActivity) activity;
      fragmentActivity.getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(this);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      activity.getFragmentManager()
              .unregisterFragmentLifecycleCallbacks(getDeprecatedFragmentCallbackTracker());
    }
    flushData(activity.hashCode());
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull final Activity activity,
                                          @NonNull final Bundle outState) {
    // nop
  }

  @Override
  public void onActivityDestroyed(@NonNull final Activity activity) {
    // nop
  }

  /**
   * Records a fragment VIEW_CREATED or PAUSED to the relevant fragment hash code,
   * and activity hash code.
   *
   * @param fragmentHashCode the current fragment hash code.
   * @param fragmentData     the fragment data that should be recorded.
   */
  @VisibleForTesting
  protected void recordFragmentData(@NonNull final Integer fragmentHashCode,
                                    @NonNull final FragmentData fragmentData) {

    // ensure that any incoming record has a single state change.
    if (fragmentData.getStates().size() != 1) {
      return;
    }

    Map<Integer, FragmentData> fragmentRecords = activityFragmentMap.get(activeActivityHashCode);
    if (fragmentRecords == null) {
      fragmentRecords = new HashMap<>();
    }

    //get the record for the current fragment
    FragmentData fragmentRecord = fragmentRecords.get(fragmentHashCode);
    if (fragmentRecord == null) {
      // we don't have a record - we must create one
      fragmentRecord = fragmentData;
    } else {
      // we already had a record - we should append the state to this one
      fragmentRecord.addState(fragmentData.getStates().get(0));
    }

    //replace the fragment record
    fragmentRecords.put(fragmentHashCode.hashCode(), fragmentRecord);

    // replace the activity records
    activityFragmentMap.put(activeActivityHashCode, fragmentRecords);
  }

  @Override
  public void onFragmentViewCreated(@NonNull final FragmentManager fragmentManager,
                                    @NonNull final Fragment fragment,
                                    @NonNull final View view,
                                    @Nullable final Bundle savedInstanceState) {
    super.onFragmentViewCreated(fragmentManager, fragment, view, savedInstanceState);
    if (!isActive()) {
      return;
    }

    recordFragmentData(
        fragment.hashCode(),
        createFragmentData(fragment, FragmentState.VIEW_CREATED));
  }

  @Override
  public void onFragmentPaused(@NonNull final FragmentManager fragmentManager,
                               @NonNull final Fragment fragment) {
    super.onFragmentPaused(fragmentManager, fragment);
    if (!isActive()) {
      return;
    }

    recordFragmentData(
        fragment.hashCode(),
        createFragmentData(fragment, FragmentState.PAUSED));
  }

  /**
   * Gets the {@link android.app.FragmentManager.FragmentLifecycleCallbacks} for receiving and
   * handing Fragment lifecycle events.
   *
   * @return the FragmentLifecycleCallbacks.
   */
  @VisibleForTesting
  @RequiresApi(api = Build.VERSION_CODES.O)
  @NonNull
  synchronized FragmentLifecycleCallbacks getDeprecatedFragmentCallbackTracker() {
    if (deprecatedFragmentCallbackTracker == null) {
      deprecatedFragmentCallbackTracker =
          new android.app.FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(
                @NonNull final android.app.FragmentManager fragmentManager,
                @NonNull final android.app.Fragment fragment,
                @NonNull final View view,
                @NonNull final Bundle savedInstanceState) {
              super.onFragmentViewCreated(fragmentManager, fragment, view, savedInstanceState);
              if (!isActive()) {
                return;
              }

              recordFragmentData(
                  fragment.hashCode(),
                  createFragmentData(fragment, FragmentState.VIEW_CREATED));
            }

            @Override
            public void onFragmentPaused(@NonNull final android.app.FragmentManager fragmentManager,
                                         @NonNull final android.app.Fragment fragment) {
              super.onFragmentPaused(fragmentManager, fragment);
              if (!isActive()) {
                return;
              }

              recordFragmentData(
                  fragment.hashCode(),
                  createFragmentData(fragment, FragmentState.PAUSED));
            }
          };
    }
    return deprecatedFragmentCallbackTracker;
  }

  /**
   * Gets the parent Span ID for the given Fragment.
   *
   * @param fragment the given Fragment.
   * @return the Span ID of the parent, or {@code null}, when it does not have a parent.
   */
  @Nullable
  private String getParentSpanId(@NonNull final Fragment fragment) {
    if (fragment.getParentFragment() == null) {
      final FragmentActivity fragmentActivity = fragment.getActivity();
      if (fragmentActivity == null) {
        return null;
      }
      return getParentSpanIdFromActivityId(fragmentActivity.hashCode());
    }
    return getParentSpanIdFromFragmentId(fragment.getParentFragment().hashCode());
  }

  /**
   * Gets the parent Span ID for the given Fragment.
   *
   * @param fragment the given Fragment.
   * @return the Span ID of the parent, or {@code null}, when it does not have a parent.
   */
  @Nullable
  private String getParentSpanId(@NonNull final android.app.Fragment fragment) {
    if (fragment.getParentFragment() == null) {
      final Activity activity = fragment.getActivity();
      if (activity == null) {
        return null;
      }
      return getParentSpanIdFromActivityId(activity.hashCode());
    }
    return getParentSpanIdFromFragmentId(fragment.getParentFragment().hashCode());
  }

  /**
   * Gets the parent Span ID with the ID of the hosting Activity.
   *
   * @param activityId the ID of the hosting Activity.
   * @return the Span ID of the hosting Activity, or {@code null}, when it cannot be determined.
   */
  @VisibleForTesting
  @Nullable
  String getParentSpanIdFromActivityId(final int activityId) {
    if (activityStateDataListener.activityMap.containsKey(activityId)) {
      return activityStateDataListener.activityMap.get(activityId).getSpanId();
    }
    return null;
  }

  /**
   * Finds the span id for a child fragment.
   *
   * @param fragmentHashCode the hashcode of the parent Fragment.
   * @return the Span ID of the parent Fragment, or {@code null}, when it cannot be determined.
   */
  @VisibleForTesting
  @Nullable
  String getParentSpanIdFromFragmentId(final int fragmentHashCode) {
    final Map<Integer, FragmentData> fragmentRecords =
        activityFragmentMap.get(activeActivityHashCode);
    if (fragmentRecords != null) {
      final FragmentData fragmentData = fragmentRecords.get(fragmentHashCode);
      if (fragmentData != null) {
        return fragmentData.getSpanId();
      }
    }
    return null;
  }

  /**
   * Creates a {@link FragmentData} instance from the given input.
   *
   * @param fragment      the given Fragment.
   * @param fragmentState the given {@link FragmentState}
   * @return the created FragmentData.
   */
  @VisibleForTesting
  @NonNull
  FragmentData createFragmentData(@NonNull final Fragment fragment,
                                          @NonNull final FragmentState fragmentState) {
    final FragmentData fragmentData = new FragmentData(traceManager.createSpanId(false));
    fragmentData.setName(fragment.getClass().getSimpleName());
    fragmentData.addState(fragmentState, TraceClock.getCurrentTimeMillis());
    fragmentData.setParentSpanId(getParentSpanId(fragment));
    return fragmentData;
  }

  /**
   * Creates a {@link FragmentData} instance from the given input.
   *
   * @param fragment      the given Fragment.
   * @param fragmentState the given {@link FragmentState}
   * @return the created FragmentData.
   */
  @VisibleForTesting
  @NonNull
  FragmentData createFragmentData(@NonNull final android.app.Fragment fragment,
                                          @NonNull final FragmentState fragmentState) {
    final FragmentData fragmentData = new FragmentData(traceManager.createSpanId(false));
    fragmentData.setName(fragment.getClass().getSimpleName());
    fragmentData.addState(fragmentState, TraceClock.getCurrentTimeMillis());
    fragmentData.setParentSpanId(getParentSpanId(fragment));
    return fragmentData;
  }

  /**
   * When an Activity is stopped, we should flush the {@link FragmentData} of the given
   * Activity hashCode from the {@link #activityFragmentMap} and forward it for further processing.
   *
   * @param activityHashCode the hashcode for the activity that has just stopped.
   */
  private void flushData(@NonNull final Integer activityHashCode) {
    final Data data = new Data(this);

    Map<Integer, FragmentData> records = activityFragmentMap.get(activityHashCode);

    if (records != null) {
      data.setContent(records);
      onDataCollected(data);
    }

    activityFragmentMap.remove(activityHashCode);
  }

  @Override
  public void startCollecting() {
    activityFragmentMap.clear();
    traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(this);
    isActive = true;
  }

  @Override
  public void stopCollecting() {
    traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(this);
    isActive = false;
    endAnyFragmentsOpenAndFlush();
  }

  private void endAnyFragmentsOpenAndFlush() {

    // loop through any activities we had open
    for (Map.Entry<Integer, Map<Integer, FragmentData>> activityEntry :
        activityFragmentMap.entrySet()) {
      final Map<Integer, FragmentData> fragmentMap = activityEntry.getValue();

      // loop through any fragments in those activities
      for (Map.Entry<Integer, FragmentData> fragmentEntry : fragmentMap.entrySet()) {
        final FragmentData fragmentData = fragmentEntry.getValue();

        // append the stopped state to all the entries
        fragmentData.addState(
            new FragmentDataStateEntry(FragmentState.STOPPED, TraceClock.getCurrentTimeMillis()));

        // send the data up
        final Data data = new Data(this);
        data.setContent(fragmentData);
        onDataCollected(data);

      }
    }
  }

  @Override
  public void onDataCollected(@NonNull final Data data) {
    dataManager.handleReceivedData(data);
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }
}
