package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.ActivityState;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.data.trace.TraceManager;

/**
 * Listens to the state changes of the Activities of the Application. Gathers and sends the data to the
 * {@link DataManager}.
 * <p>
 * The value of {@link Activity#hashCode()} is used for ID for each Activity, to differentiate between them.
 *
 * @see
 * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle">https://developer.android.com/guide/components/activities/activity-lifecycle</a>
 */
public class ActivityStateDataListener extends BaseDataListener implements TraceActivityLifecycleSink {

    /**
     * A Map that identifies a single Activity and attaches the required data to it as an {@link ActivityData}.
     */
    @NonNull
    final Map<Integer, ActivityData> activityMap;
    /**
     * The {@link TraceActivityLifecycleTracker} for observing the lifecycle state changes.
     */
    @NonNull
    private final TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    /**
     * The {@link TraceManager} for getting Span IDs.
     */
    @NonNull
    private final TraceManager traceManager;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public ActivityStateDataListener(@NonNull final Context context) {
        this.dataManager = DataManager.getInstance(context);
        this.traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(context);
        this.activityMap = new HashMap<>();
        this.traceManager = ApplicationTraceManager.getInstance(context);
    }

    @Override
    public void startCollecting() {
        activityMap.clear();
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

    @Override
    public void onActivityCreated(@NonNull final Activity activity, @Nullable final Bundle savedInstanceState) {
        if (!isActive()) {
            return;
        }

        updateActivityMap(activity.hashCode(), activity.getClass().getSimpleName(), ActivityState.CREATED);
    }

    @Override
    public void onActivityStarted(@NonNull final Activity activity) {
        if (!isActive()) {
            return;
        }

        updateActivityMap(activity.hashCode(), activity.getClass().getSimpleName(), ActivityState.STARTED);
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

        final int id = activity.hashCode();
        updateActivityMap(id, null, ActivityState.STOPPED);
        flushData(id);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull final Activity activity, @NonNull final Bundle outState) {
        // nop
    }

    @Override
    public void onActivityDestroyed(@NonNull final Activity activity) {
        // nop
    }

    /**
     * Updates the {@link #activityMap} with the given values.
     *
     * @param activityId        the ID of the Activity to update. Inserts if doesn't exists.
     * @param name              the name of the Activity, or {@code null} if it does not required to change.
     * @param activityStateType the {@link ActivityState} to add.
     */
    private void updateActivityMap(final int activityId,
                                   @Nullable final String name,
                                   @NonNull final ActivityState activityStateType) {
        ActivityData activityData = activityMap.get(activityId);
        if (activityData == null) {
            activityData = new ActivityData(traceManager.createSpanId(true));
        }

        if (name != null) {
            activityData.setName(name);
        }

        activityData.putState(activityStateType, TraceClock.getCurrentTimeMillis());
        activityMap.put(activityId, activityData);
    }

    /**
     * When an Activity reaches a state, that a {@link Span} is finished, we should flush the {@link ActivityData} of
     * the given Activity from the {@link #activityMap} and forward it for further processing.
     *
     * @param id the ID of the given Activity.
     */
    private void flushData(final int id) {
        final Data data = new Data(this);
        data.setContent(activityMap.get(id));
        activityMap.remove(id);
        onDataCollected(data);
    }
}
