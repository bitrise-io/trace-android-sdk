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
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.ActivityState;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.session.ApplicationSessionManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Instrumented tests for the {@link ActivityStateDataListener}.
 */
public class ActivityStateDataListenerInstrumentedTest {

    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    private static ActivityStateDataListener activityStateDataListener;
    private static Application mockApplication;
    private Activity mockActivity = mock(Activity.class);

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
        activityStateDataListener = new ActivityStateDataListener(mockApplication);
        activityStateDataListener.startCollecting();
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(activityStateDataListener);
    }

    /**
     * When the collection for {@link ActivityStateDataListener} is started, it should be active.
     */
    @Test
    public void isActive_ShouldBeTrueWhenStarted() {
        assertThat(activityStateDataListener.isActive(), is(true));
    }

    /**
     * When the collection for {@link ActivityStateDataListener} is stopped, it should not be active.
     */
    @Test
    public void isActive_ShouldBeFalseWhenStopped() {
        activityStateDataListener.stopCollecting();
        assertThat(activityStateDataListener.isActive(), is(false));
    }

    /**
     * Before any activity is started, {@link ActivityStateDataListener#activityMap} should be empty.
     */
    @Test
    public void activityMap_ShouldBeEmptyWithNoActivity() {
        assertThat(activityStateDataListener.activityMap.isEmpty(), is(true));
    }

    /**
     * After an activity is started, {@link ActivityStateDataListener#activityMap} should contain it's ID.
     */
    @Test
    public void activityMap_ShouldContainStartedActivity() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        assertThat(activityStateDataListener.activityMap.containsKey(mockActivity.hashCode()), is(true));
    }

    /**
     * If the data collection is stopped with {@link ActivityStateDataListener#stopCollecting()}, it should not put
     * any value to {@link ActivityStateDataListener#activityMap}.
     */
    @Test
    public void activityMap_ShouldNotContainActivityWhenCollectingStopped() {
        activityStateDataListener.stopCollecting();
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        assertThat(activityStateDataListener.activityMap.isEmpty(), is(true));
    }

    /**
     * After activity is started, the given Activity's entry in the {@link ActivityStateDataListener#activityMap}
     * should contain the {@link ActivityState#STARTED} value in it's {@link ActivityData}.
     */
    @Test
    public void activityMap_ActivityDataShouldContainStartedState() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        final ActivityData activityData = activityStateDataListener.activityMap.get(mockActivity.hashCode());
        assertThat(activityData.getStateMap().containsKey(ActivityState.STARTED), is(true));
    }

    /**
     * After activity is created, the given Activity's entry in the {@link ActivityStateDataListener#activityMap}
     * should contain the {@link ActivityState#CREATED} value in it's {@link ActivityData}.
     */
    @Test
    public void activityMap_ActivityDataShouldContainCreatedState() {
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        final ActivityData activityData = activityStateDataListener.activityMap.get(mockActivity.hashCode());
        assertThat(activityData.getStateMap().containsKey(ActivityState.CREATED), is(true));
    }

    /**
     * After activity is stopped, the given Activity's entry in the {@link ActivityStateDataListener#activityMap}
     * should not contain the {@link ActivityState#STOPPED} value in it's {@link ActivityData}, as it should be flushed.
     */
    @Test
    public void activityMap_ActivityDataShouldNotContainStoppedState() {
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        final ActivityData activityData = activityStateDataListener.activityMap.get(mockActivity.hashCode());
        assertThat(activityData.getStateMap().containsKey(ActivityState.STOPPED), is(false));
    }

    /**
     * After activity is stopped, the {@link DataManager#handleReceivedData(Data)} method should be called.
     */
    @Test
    public void onActivityStopped_ShouldCallHandleReceivedData() {
        final DataManager mockDataManager = mock(DataManager.class);
        activityStateDataListener.setDataManager(mockDataManager);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
    }

    /**
     * After activity is stopped, the {@link DataManager#handleReceivedData(Data)} method should not be called if
     * Data collection is stopped with {@link ActivityStateDataListener#stopCollecting()}.
     */
    @Test
    public void onActivityStopped_ShouldNotCallHandleReceivedDataWhenCollectingStopped() {
        activityStateDataListener.stopCollecting();
        final DataManager mockDataManager = mock(DataManager.class);
        activityStateDataListener.setDataManager(mockDataManager);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockDataManager, never()).handleReceivedData(any(Data.class));
    }

    /**
     * If {@link ActivityStateDataListener#stopCollecting()} is called, it should not affect the current state of the
     * {@link ActivityStateDataListener#activityMap}.
     */
    @Test
    public void stopCollecting_ShouldNotAffectActivityMap() {
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        activityStateDataListener.stopCollecting();
        assertThat(activityStateDataListener.activityMap.size(), is(1));
    }

    /**
     * If {@link ActivityStateDataListener#startCollecting()} is called, it should empty the
     * {@link ActivityStateDataListener#activityMap}. This is required, because during the time the collecting is
     * stopped multiple Activity lifecycle event can occur, which could lead to invalid data.
     */
    @Test
    public void startCollecting_ShouldEmptyActivityMap() {
        traceActivityLifecycleTracker.onActivityCreated(mockActivity, new Bundle());
        activityStateDataListener.stopCollecting();
        activityStateDataListener.startCollecting();
        assertThat(activityStateDataListener.activityMap.size(), is(0));
    }
}