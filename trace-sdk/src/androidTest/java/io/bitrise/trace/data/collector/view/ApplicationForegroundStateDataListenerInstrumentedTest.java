package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.session.ApplicationSessionManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;

/**
 * Instrumented tests for the {@link ApplicationForegroundStateDataListener}.
 */
public class ApplicationForegroundStateDataListenerInstrumentedTest {

    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    private static ApplicationForegroundStateDataListener applicationForegroundStateDataListener;
    private static Application mockApplication;
    private Activity mockActivity1 = mock(Activity.class);
    private Activity mockActivity2 = mock(Activity.class);

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        ApplicationSessionManager.getInstance().startSession();
        mockApplication = mock(Application.class);
        traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(mockApplication);
        BaseDataCollectorInstrumentedTest.useTestDataStorage(mockApplication);
    }

    /**
     * Tears down the required objects after all the tests run.
     */
    @AfterClass
    public static void tearDownClass() {
        ApplicationSessionManager.getInstance().stopSession();
    }

    /**
     * Sets up the initial state for each test case.
     */
    @Before
    public void setUp() {
        TraceActivityLifecycleTracker.reset();
        applicationForegroundStateDataListener = new ApplicationForegroundStateDataListener(mockApplication);
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(applicationForegroundStateDataListener);
        applicationForegroundStateDataListener.startCollecting();
    }

    /**
     * When the collection for {@link ApplicationForegroundStateDataListener} is started, it should be active.
     */
    @Test
    public void isActive_ShouldBeTrueWhenStarted() {
        assertThat(applicationForegroundStateDataListener.isActive(), is(true));
    }

    /**
     * When the collection for {@link ApplicationForegroundStateDataListener} is stopped, it should not be active.
     */
    @Test
    public void isActive_ShouldBeFalseWhenStopped() {
        applicationForegroundStateDataListener.stopCollecting();
        assertThat(applicationForegroundStateDataListener.isActive(), is(false));
    }

    /**
     * Before activity is started, app should be in background.
     */
    @Test
    public void isAppIsInForeground_ShouldBeFalseWithNoActivity() {
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(false));
    }

    /**
     * After activity is started, app should be in foreground.
     */
    @Test
    public void isAppIsInForeground_ShouldBeTrueWithActivityStarted() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(true));
    }

    /**
     * After activity is stopped, app should be in background.
     */
    @Test
    public void isAppIsInForeground_ShouldBeFalseWithActivityStopped() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(false));
    }

    /**
     * After activity is started again, app should be in foreground.
     */
    @Test
    public void isAppIsInForeground_ShouldBeTrueWithActivityRestart() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(true));
    }

    /**
     * If at least there is one activity that is not stopped, app should be in foreground.
     */
    @Test
    public void isAppIsInForeground_ShouldBeTrueWithAtLeastOneActivityStarted() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity2);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(true));
    }

    /**
     * If the data collection is stopped with {@link ApplicationForegroundStateDataListener#stopCollecting()},
     * starting an
     * Activity should not make it return {@code true} for checking if it is in the foreground.
     */
    @Test
    public void isAppIsInForeground_ShouldNotContainActivityWhenCollectingStopped() {
        applicationForegroundStateDataListener.stopCollecting();
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, new Bundle());
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        assertThat(applicationForegroundStateDataListener.hasApplicationJustComeFromBackground(), is(false));
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], applicationForegroundStateDataListener.getPermissions());
    }
}