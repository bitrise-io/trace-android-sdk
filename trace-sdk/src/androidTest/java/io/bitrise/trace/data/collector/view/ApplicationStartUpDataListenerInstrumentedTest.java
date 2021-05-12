package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.dto.ApplicationStartData;
import io.bitrise.trace.data.dto.ApplicationStartType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.collector.DataSourceType;
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
 * Instrumented tests for the {@link ApplicationStartUpDataListener}.
 */
public class ApplicationStartUpDataListenerInstrumentedTest {

    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    private static ApplicationStartUpDataListener applicationStartUpDataListener;
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
        applicationStartUpDataListener = new ApplicationStartUpDataListener(mockApplication,
                applicationForegroundStateDataListener);
        applicationStartUpDataListener.startCollecting();
        applicationForegroundStateDataListener.startCollecting();
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(applicationStartUpDataListener);
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(applicationForegroundStateDataListener);
    }

    /**
     * When the first Activity is launched, it should be a cold start.
     */
    @Test
    public void handleReceivedData_ShouldBeColdStart() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        final ArgumentCaptor<Data> dataCaptor = ArgumentCaptor.forClass(Data.class);
        verify(mockDataManager).handleReceivedData(dataCaptor.capture());

        final ApplicationStartType actualValue =
                ((ApplicationStartData) dataCaptor.getValue().getContent()).getApplicationStartType();
        assertThat(actualValue, is(ApplicationStartType.COLD));
    }

    /**
     * When the first Activity is launched, then put in the background, then re-opened and the onCreate is called, it
     * should be a warm start.
     */
    @Test
    public void handleReceivedData_ShouldBeWarmStart() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);

        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);

        traceActivityLifecycleTracker.onActivityPaused(mockActivity1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);
        applicationForegroundStateDataListener.onActivityStopped(mockActivity1);

        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        final ArgumentCaptor<Data> dataCaptor = ArgumentCaptor.forClass(Data.class);
        verify(mockDataManager, times(2)).handleReceivedData(dataCaptor.capture());

        final ApplicationStartType actualValue =
                ((ApplicationStartData) dataCaptor.getAllValues().get(1).getContent()).getApplicationStartType();
        assertThat(actualValue, is(ApplicationStartType.WARM));
    }

    /**
     * When an Activity is already running and a new Activity is launched, it should not be a startup event.
     */
    @Test
    public void handleReceivedData_ShouldNotBeCalledWhenNewActivityCreated() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        // Create first Activity.
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        // Creating the first Activity was an Application launch, so handleReceivedData should be called exactly once.
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
        // Launch second Activity.
        applicationForegroundStateDataListener.onActivityStarted(mockActivity2);
        applicationForegroundStateDataListener.onActivityPaused(mockActivity1);
        traceActivityLifecycleTracker.onActivityPaused(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity2, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity2);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity2);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);

        // Launching the second Activity was not an Application launch, so the number of times handleReceivedData was
        // called should be still 1.
        final ArgumentCaptor<Data> dataCaptor = ArgumentCaptor.forClass(Data.class);
        verify(mockDataManager, times(1)).handleReceivedData(dataCaptor.capture());

        final DataSourceType actualValue =
                ((DataSourceType) dataCaptor.getAllValues().get(0).getDataSourceType());
        assertThat(actualValue, is(DataSourceType.APP_START));
    }

    /**
     * When an Activity is already running and a new Activity is launched, then we navigate back it should not be a
     * startup event.
     */
    @Test
    public void handleReceivedData_ShouldNotBeCalledWhenNavigatingBack() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        // Create first Activity.
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        // Creating the first Activity was an Application launch, so handleReceivedData should be called exactly once.
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
        // Launch second Activity.
        applicationForegroundStateDataListener.onActivityPaused(mockActivity1);
        applicationForegroundStateDataListener.onActivityStarted(mockActivity2);
        traceActivityLifecycleTracker.onActivityPaused(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity2, null);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity2);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity2);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity1);
        // Launching the second Activity was not an Application launch, so the number of times handleReceivedData was
        // called should be still 1.
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
        // Navigate back.
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        applicationForegroundStateDataListener.onActivityStopped(mockActivity2);
        traceActivityLifecycleTracker.onActivityPaused(mockActivity2);
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity2);

        // After navigating back, the Activity Start lifecycle event should not count as an Application launch, so
        // the number of times handleReceivedData was called should be still 1.
        final ArgumentCaptor<Data> dataCaptor = ArgumentCaptor.forClass(Data.class);
        verify(mockDataManager, times(1)).handleReceivedData(dataCaptor.capture());

        final DataSourceType actualValue =
                ((DataSourceType) dataCaptor.getAllValues().get(0).getDataSourceType());
        assertThat(actualValue, is(DataSourceType.APP_START));
    }

    /**
     * If the data collection is stopped with {@link ApplicationStartUpDataListener#stopCollecting()}, it should not
     * call {@link DataManager#handleReceivedData(Data)}.
     */
    @Test
    public void handleReceivedData_ShouldNotContainActivityWhenCollectingStopped() {
        applicationStartUpDataListener.stopCollecting();
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, new Bundle());
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        verify(mockDataManager, never()).handleReceivedData(any(Data.class));
    }

    /**
     * No {@link Data} should be received before the onResume lifecycle event is called.
     */
    @Test
    public void handleReceivedData_ShouldNotBeCalledBeforeOnResume() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, new Bundle());
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        verify(mockDataManager, never()).handleReceivedData(any(Data.class));
    }

    /**
     * {@link Data} should be received after the onResume lifecycle event is called.
     */
    @Test
    public void handleReceivedData_ShouldBeCalledAfterOnResume() {
        final DataManager mockDataManager = mock(DataManager.class);
        applicationForegroundStateDataListener.onActivityStarted(mockActivity1);
        applicationStartUpDataListener.setDataManager(mockDataManager);
        traceActivityLifecycleTracker.onActivityCreated(mockActivity1, new Bundle());
        traceActivityLifecycleTracker.onActivityStarted(mockActivity1);
        traceActivityLifecycleTracker.onActivityResumed(mockActivity1);
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], applicationStartUpDataListener.getPermissions());
    }
}