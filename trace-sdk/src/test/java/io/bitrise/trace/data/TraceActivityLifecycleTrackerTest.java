package io.bitrise.trace.data;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.bitrise.trace.data.collector.TraceActivityLifecycleSink;
import io.bitrise.trace.data.collector.view.ApplicationForegroundStateDataListener;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the {@link TraceActivityLifecycleTracker}.
 */
public class TraceActivityLifecycleTrackerTest {

    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    private static Application mockApplication = mock(Application.class);
    private Activity mockActivity = mock(Activity.class);
    private ApplicationForegroundStateDataListener mockApplicationForegroundStateDataListener;

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(mockApplication);
    }

    /**
     * Sets up the initial state for each test case.
     */
    @Before
    public void setUp() {
        TraceActivityLifecycleTracker.reset();
        mockApplicationForegroundStateDataListener = mock(ApplicationForegroundStateDataListener.class);
    }

    /**
     * Asserts that the {@link TraceActivityLifecycleTracker} is a singleton.
     */
    @Test
    public void isSingleton_ShouldBeTrue() {
        final TraceActivityLifecycleTracker expectedValue = TraceActivityLifecycleTracker.getInstance(mockApplication);
        final TraceActivityLifecycleTracker actualValue =
                TraceActivityLifecycleTracker.getInstance(mockApplication);
        assertThat(actualValue, is(expectedValue));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} is called.
     */
    @Test
    public void onActivityCreated_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, never()).onActivityCreated(any(Activity.class),
                any(Bundle.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} method of it should be called when
     * the {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} is called.
     */
    @Test
    public void onActivityCreated_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityCreated(any(Activity.class),
                any(Bundle.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityCreated(Activity, Bundle)} is called.
     */
    @Test
    public void onActivityCreated_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, never()).onActivityCreated(any(Activity.class),
                any(Bundle.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)}
     * is called.
     */
    @Test
    public void onActivityStarted_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityStarted(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)} method of it should be called when
     * the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)} is called.
     */
    @Test
    public void onActivityStarted_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityStarted(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityStarted(Activity)}
     * is called.
     */
    @Test
    public void onActivityStarted_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityStarted(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)} is called.
     */
    @Test
    public void onActivityResumed_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityResumed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityResumed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)} method of it should be called when the
     * {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)} is called.
     */
    @Test
    public void onActivityResumed_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityResumed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityResumed(Activity)}
     * is called.
     */
    @Test
    public void onActivityResumed_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityResumed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)} is called.
     */
    @Test
    public void onActivityPaused_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityPaused(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityPaused(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)} method of it should be called when the
     * {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)} is called.
     */
    @Test
    public void onActivityPaused_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityPaused(mockActivity);
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityPaused(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityPaused(Activity)}
     * is called.
     */
    @Test
    public void onActivityPaused_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityPaused(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityPaused(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)} is called.
     */
    @Test
    public void onActivityStopped_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityStopped(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)} method of it should be called when the
     * {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)} is called.
     */
    @Test
    public void onActivityStopped_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityStopped(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityStopped(Activity)}
     * is called.
     */
    @Test
    public void onActivityStopped_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityStopped(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)} method of it should not be called
     * when the {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)} is called.
     */
    @Test
    public void onActivityDestroyed_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivityDestroyed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityDestroyed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)} method of it should be called when the
     * {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)} is called.
     */
    @Test
    public void onActivityDestroyed_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityDestroyed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivityDestroyed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)}
     * method of it should not be called when the {@link TraceActivityLifecycleTracker#onActivityDestroyed(Activity)}
     * is called.
     */
    @Test
    public void onActivityDestroyed_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivityDestroyed(mockActivity);
        verify(mockApplicationForegroundStateDataListener, never()).onActivityDestroyed(any(Activity.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is not registered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} method of it should not be
     * called when the {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} is called.
     */
    @Test
    public void onActivitySaveInstanceState_ShouldBeNeverCalledIfNotRegistered() {
        traceActivityLifecycleTracker.onActivitySaveInstanceState(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, never()).onActivitySaveInstanceState(any(Activity.class),
                any(Bundle.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is registered in the the {@link TraceActivityLifecycleTracker}
     * the {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} method of it should be
     * called when the {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} is called.
     */
    @Test
    public void onActivitySaveInstanceState_ShouldBeCalledIfRegistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivitySaveInstanceState(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, times(1)).onActivitySaveInstanceState(any(Activity.class),
                any(Bundle.class));
    }

    /**
     * If the given {@link TraceActivityLifecycleSink} is unregistered in the the
     * {@link TraceActivityLifecycleTracker} the
     * {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} method of it should not be
     * called  when the {@link TraceActivityLifecycleTracker#onActivitySaveInstanceState(Activity, Bundle)} is called.
     */
    @Test
    public void onActivitySaveInstanceState_ShouldBeNeverCalledIfUnregistered() {
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.unregisterTraceActivityLifecycleSink(mockApplicationForegroundStateDataListener);
        traceActivityLifecycleTracker.onActivitySaveInstanceState(mockActivity, new Bundle());
        verify(mockApplicationForegroundStateDataListener, never()).onActivitySaveInstanceState(any(Activity.class),
                any(Bundle.class));
    }
}
