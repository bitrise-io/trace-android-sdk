package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.data.dto.ApplicationStartData;
import io.bitrise.trace.data.dto.ApplicationStartType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link DataListener} implementation for listening to the Application start times. Should be
 * coupled with a {@link ApplicationForegroundStateDataListener} and must run before it.
 */
public class ApplicationStartUpDataListener extends BaseDataListener
    implements TraceActivityLifecycleSink {

  /**
   * The {@link TraceActivityLifecycleTracker} to register for receiving the lifecycle event
   * callbacks.
   */
  @NonNull
  private final TraceActivityLifecycleTracker traceActivityLifecycleTracker;

  /**
   * The {@link ApplicationForegroundStateDataListener} to determine if it is an Application
   * launch or not.
   */
  @VisibleForTesting
    @NonNull
    final ApplicationForegroundStateDataListener applicationForegroundStateDataListener;
  /**
   * Counts the number of onCreate callbacks.
   */
  @VisibleForTesting
    @NonNull
    final AtomicInteger onCreateCounter;
  /**
   * Counts the number of onResume callbacks.
   */
  @VisibleForTesting
    @NonNull
    final AtomicInteger onResumeCounter;

  /**
   * The start time of the Application launch.
   */
  @VisibleForTesting
    Long start;
    /**
     * The end time of the Application launch.
     */
    @VisibleForTesting
    Long end;
    /**
     * Indicates that the given lifecycle events are an Application start or not. If the* Application comes from the background, it should be an Application start, otherwise it is
   * just a new Activity launch.
   */
  private boolean isApplicationStart;

  /**
   * Constructor for class.
   *
   * @param context                                the Android Context.
   * @param applicationForegroundStateDataListener the active
   *                                               {@link ApplicationForegroundStateDataListener}
   *                                               for getting the correct Application states.
   */
  public ApplicationStartUpDataListener(
      @NonNull final Context context,
      @NonNull
      final ApplicationForegroundStateDataListener applicationForegroundStateDataListener) {
    this.traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(context);
    this.dataManager = DataManager.getInstance(context);
    this.applicationForegroundStateDataListener = applicationForegroundStateDataListener;
    this.onCreateCounter = new AtomicInteger(0);
    this.onResumeCounter = new AtomicInteger(0);
  }

  @Override
  public void onActivityCreated(@NonNull final Activity activity,
                                @Nullable final Bundle savedInstanceState) {
    if (!isActive()) {
      return;
    }

    onCreateCounter.getAndIncrement();
    start = TraceClock.getCurrentTimeMillis();
  }

  @Override
  public void onActivityStarted(@NonNull final Activity activity) {
    if (!isActive()) {
      return;
    }
    isApplicationStart =
        applicationForegroundStateDataListener.hasApplicationJustComeFromBackground();

    if (start == null) {
      start = TraceClock.getCurrentTimeMillis();
    }
  }

  @Override
  public void onActivityResumed(@NonNull final Activity activity) {
    if (!isActive()) {
      return;
    }

    onResumeCounter.getAndIncrement();
    if (isApplicationStart) {
      end = TraceClock.getCurrentTimeMillis();
      final Data data = new Data(this);
      data.setContent(getApplicationStartData());
      onDataCollected(data);
    }
  }

  /**
   * Creates an {@link ApplicationStartData} from the available information. The duration of
   * the Application launch should be the time elapsed between the creation and the resume of the
   * first Activity.
   *
   * <p>The current {@link ApplicationStartType}s are determined in the following way:
   * <ul>
   *     <li>The Application should enter from the background</li>
   *     <li>{@link ApplicationStartType#COLD}, when the onCreate and onResume method was
   *     exactly once, because
   *     this means only one Activity was created so far and is shown for the first time. </li>
   *     <li>{@link ApplicationStartType#WARM} otherwise</li>
   * </ul>
   *
   * @return the ApplicationStartData.
   * @see
   * <a href="https://developer.android.com/topic/performance/vitals/launch-time">https://developer.android.com/topic/performance/vitals/launch-time</a>
   */
  @NonNull
    private synchronized ApplicationStartData getApplicationStartData() {
        final long duration = end - start;
        if (onCreateCounter.get() == 1 && onResumeCounter.get() == 1) {
            TraceLog.d("ApplicationStartUpDataListener COLD");
            return new ApplicationStartData(duration, ApplicationStartType.COLD);
        } else {
            TraceLog.d("ApplicationStartUpDataListener WARM");
            return new ApplicationStartData(duration, ApplicationStartType.WARM);
        }
    }

  @Override
  public void onActivityPaused(@NonNull final Activity activity) {
    if (!isActive()) {
      return;
    }

    start = null;
  }

  @Override
  public void onActivityStopped(@NonNull final Activity activity) {
    // nop
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

  @Override
  public void startCollecting() {
    traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(this);
    setActive(true);
  }

  @Override
  public void stopCollecting() {
    traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(this);
    setActive(false);
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }
}
