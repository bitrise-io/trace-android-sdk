package io.bitrise.trace.data.collector;

import android.app.Application;
import io.bitrise.trace.data.TraceActivityLifecycleTracker;

/**
 * Interface for Objects that want to receive Activity lifecycle events through the
 * {@link TraceActivityLifecycleTracker}s.
 */
public interface TraceActivityLifecycleSink extends Application.ActivityLifecycleCallbacks {
}
