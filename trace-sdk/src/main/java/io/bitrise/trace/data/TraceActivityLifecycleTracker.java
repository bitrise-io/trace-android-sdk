package io.bitrise.trace.data;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.TraceSdk;
import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.LinkedHashSet;
import javax.inject.Singleton;

/**
 * Class used by the {@link TraceSdk} that uses Android's ActivityLifecycleCallbacks to monitor
 * Activity lifecycle changes. Can register {@link TraceActivityLifecycleSink}s to forward these
 * events.
 */
@Singleton
public class TraceActivityLifecycleTracker implements Application.ActivityLifecycleCallbacks {

  @NonNull
  private static final LinkedHashSet<TraceActivityLifecycleSink>
      registeredTraceActivityLifecycleSinks = new LinkedHashSet<>();
  /**
   * A lock object for preventing concurrency issues for the
   * {@link #traceActivityLifecycleTracker}. E.g: when
   * {@link #reset()} and {@link #getInstance(Context)} is called parallel.
   */
  @NonNull
  private static final Object traceActivityLifecycleTrackerLock = new Object();
  /**
   * A lock object for preventing concurrency issues for the
   * {@link #registeredTraceActivityLifecycleSinks}. E.g: when
   * {@link #reset()} ()} and {@link #onActivityCreated(Activity, Bundle)} )} is called parallel.
   */
  @NonNull
  private static final Object registeredTraceActivityLifecycleSinkLock = new Object();
  @Nullable
  @VisibleForTesting
  static volatile TraceActivityLifecycleTracker traceActivityLifecycleTracker;
  @NonNull
  private final Context applicationContext;

  private TraceActivityLifecycleTracker(@NonNull final Context applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Gets an instance of the class.
   *
   * @param applicationContext the Android Application Context.
   * @return an instance of the class.
   */
  @NonNull
  public static synchronized TraceActivityLifecycleTracker getInstance(
      @NonNull final Context applicationContext) {
    synchronized (traceActivityLifecycleTrackerLock) {
      if (traceActivityLifecycleTracker == null) {
        traceActivityLifecycleTracker = new TraceActivityLifecycleTracker(applicationContext);
        TraceLog.d(LogMessageConstants.ACTIVITY_LIFECYCLE_TRACKER_INITIALISED);
      }
      return traceActivityLifecycleTracker;
    }
  }

  /**
   * Resets the state of the TraceActivityLifecycleTracker.
   */
  public static synchronized void reset() {
    synchronized (traceActivityLifecycleTrackerLock) {
      if (traceActivityLifecycleTracker == null) {
        return;
      }

      traceActivityLifecycleTracker.unregister();
      synchronized (registeredTraceActivityLifecycleSinkLock) {
        registeredTraceActivityLifecycleSinks.clear();
      }
      traceActivityLifecycleTracker = null;
    }
  }

  /**
   * Unregisters the TraceActivityLifecycleTracker from the ActivityLifecycle callbacks.
   */
  private void unregister() {
    if (applicationContext instanceof Application) {
      ((Application) applicationContext)
          .unregisterActivityLifecycleCallbacks(traceActivityLifecycleTracker);
    }
  }

  /**
   * Registers a {@link TraceActivityLifecycleSink} for the Activity lifecycle events. The
   * registered sinks will receive the events in the order they were registered.
   *
   * @param traceActivityLifecycleSink the given TraceActivityLifecycleSink.
   */
  public void registerTraceActivityLifecycleSink(
      @NonNull final TraceActivityLifecycleSink traceActivityLifecycleSink) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      registeredTraceActivityLifecycleSinks.add(traceActivityLifecycleSink);
    }
  }


  /**
   * Unregisters a {@link TraceActivityLifecycleSink} for the Activity lifecycle events.
   *
   * @param traceActivityLifecycleSink the given TraceActivityLifecycleSink.
   */
  public void unregisterTraceActivityLifecycleSink(
      @NonNull final TraceActivityLifecycleSink traceActivityLifecycleSink) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      registeredTraceActivityLifecycleSinks.remove(traceActivityLifecycleSink);
    }
  }

  @Override
  public void onActivityStarted(@NonNull final Activity activity) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityStarted(activity);
      }
    }
  }

  @Override
  public void onActivityStopped(@NonNull final Activity activity) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityStopped(activity);
      }
    }
  }

  @Override
  public void onActivityCreated(@NonNull final Activity activity, @Nullable final Bundle bundle) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityCreated(activity, bundle);
      }
    }
  }

  @Override
  public void onActivityResumed(@NonNull final Activity activity) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityResumed(activity);
      }
    }
  }

  @Override
  public void onActivityPaused(@NonNull final Activity activity) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityPaused(activity);
      }
    }
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull final Activity activity,
                                          @NonNull final Bundle bundle) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivitySaveInstanceState(activity, bundle);
      }
    }
  }

  @Override
  public void onActivityDestroyed(@NonNull final Activity activity) {
    synchronized (registeredTraceActivityLifecycleSinkLock) {
      for (@NonNull final TraceActivityLifecycleSink registeredTraceActivityLifecycleSink :
          registeredTraceActivityLifecycleSinks) {
        registeredTraceActivityLifecycleSink.onActivityDestroyed(activity);
      }
    }
  }
}
