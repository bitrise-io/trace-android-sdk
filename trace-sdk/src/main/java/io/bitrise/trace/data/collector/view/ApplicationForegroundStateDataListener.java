package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.data.trace.TraceManager;

/**
 * {@link DataListener} implementation for listening to the Application state changes.
 */
public class ApplicationForegroundStateDataListener extends BaseDataListener implements TraceActivityLifecycleSink {

    private final AtomicInteger activityCounter = new AtomicInteger(0);
    @NonNull
    private final TraceManager traceManager;

    @NonNull
    private final TraceActivityLifecycleTracker traceActivityLifecycleTracker;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public ApplicationForegroundStateDataListener(@NonNull final Context context) {
        this.traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(context);
        this.traceManager = ApplicationTraceManager.getInstance(context);
    }

    /**
     * Returns whether the application has returned from the background
     * @return true if the application has either just been created or returned from another app
     */
    public synchronized boolean hasApplicationJustComeFromBackground() {
        return activityCounter.get() == 1;
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

    @Override
    public void onActivityCreated(@NonNull final Activity activity, @Nullable final Bundle savedInstanceState) {
        // nop
    }

    @Override
    public void onActivityStarted(@NonNull final Activity activity) {
        if (!isActive()) {
            return;
        }

        if (activityCounter.getAndIncrement() == 0) {
            traceManager.startTrace();
        }
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

        if (activityCounter.decrementAndGet() == 0) {
            traceManager.startTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull final Activity activity, @NonNull final Bundle outState) {
        // nop
    }

    @Override
    public void onActivityDestroyed(@NonNull final Activity activity) {
        // nop
    }
}
