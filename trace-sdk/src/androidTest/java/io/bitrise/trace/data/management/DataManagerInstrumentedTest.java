package io.bitrise.trace.data.management;

import android.content.Context;
import android.os.AsyncTask;

import androidx.test.annotation.UiThreadTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.collector.DummyDataCollector;
import io.bitrise.trace.data.collector.DummyDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TestDataStorage;
import io.bitrise.trace.scheduler.ServiceScheduler;
import io.bitrise.trace.session.ApplicationSessionManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contains the instrumented tests for {@link DataManager} class.
 */
public class DataManagerInstrumentedTest {

    private static DataManager dataManager;
    private static ConfigurationManager mockConfigurationManager;
    private static final DataManager mockDataManager = mock(DataManager.class, Mockito.CALLS_REAL_METHODS);
    private static Context context;

    /**
     * Sets up required members for the test class. Creates a {@link DataManager} that manages
     * some {@link DummyDataCollector}s and {@link DummyDataListener}s.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        context = InstrumentationRegistry.getInstrumentation()
                                         .getTargetContext()
                                         .getApplicationContext();
        mockConfigurationManager = mock(ConfigurationManager.class);
        final Set<DataCollector> dummyDataCollectors =
                new HashSet<>(Arrays.asList(
                        new DummyDataCollector("dummyCollector1"),
                        new DummyDataCollector("dummyCollector2")));
        when(mockConfigurationManager.getDataCollectors(context)).thenReturn(dummyDataCollectors);
        final LinkedHashSet<DataListener> dummyDataListeners = new LinkedHashSet<>(
                Arrays.asList(
                        new DummyDataListener("dummyListener1"),
                        new DummyDataListener("dummyListener2"),
                        new DummyDataListener("dummyListener3")));
        when(mockConfigurationManager.getDataListeners(context)).thenReturn(dummyDataListeners);
    }

    /**
     * Sets up the initial state for each test case.
     */
    @Before
    public void setUp() {
        dataManager = DataManager.getInstance(context);
        dataManager.configurationManager = mockConfigurationManager;
        final ServiceScheduler mockMetricServiceScheduler = mock(ServiceScheduler.class);
        final ServiceScheduler mockTraceServiceScheduler = mock(ServiceScheduler.class);
        dataManager.metricServiceScheduler = mockMetricServiceScheduler;
        dataManager.traceServiceScheduler = mockTraceServiceScheduler;
        mockDataManager.metricServiceScheduler = mockMetricServiceScheduler;
        mockDataManager.traceServiceScheduler = mockTraceServiceScheduler;
    }

    /**
     * When the sending is stopped with {@link DataManager#stopSending(android.content.Context)} the
     * {@link ServiceScheduler#cancelAll()} should be called.
     */
    @Test
    public void stopSending_shouldCallCancelAll() {
        dataManager.stopSending(context);

        verify(dataManager.metricServiceScheduler, times(1)).cancelAll();
        verify(dataManager.traceServiceScheduler, times(1)).cancelAll();
    }

    /**
     * Test when the Data collection in started in the DataManager, the number of active
     * DataCollectors should be 2 based on {@link #setUp()}.
     */
    @Test
    public void startCollection_shouldActiveCollectorsBeNotEmptyAfterStart() {
        dataManager.startCollection(context);

        final int expectedValue = 2;
        final int actualValue = dataManager.getActiveDataCollectors().size();

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Test when the Data collection in stopped in the DataManager, the number of active
     * DataCollectors should be zero.
     */
    @Test
    public void stopCollection_shouldHaveZeroActiveCollectors_AfterStop() {
        dataManager.stopCollection();

        final int expectedValue = 0;
        final int actualValue = dataManager.getActiveDataCollectors().size();

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Test when the Data collection in started in the DataManager, the number of active
     * DataListeners should be 3 based on {@link #setUp()}.
     */
    @Test
    public void startCollection_shouldActiveListenersBeNotEmptyAfterStart() {
        dataManager.startCollection(context);

        final int expectedValue = 3;
        final int actualValue = dataManager.getActiveDataListeners().size();

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Test when the Data collection in stopped in the DataManager, the number of active
     * DataListeners should be zero.
     */
    @Test
    public void stopCollection_shouldHaveZeroActiveListeners_AfterStop() {
        dataManager.stopCollection();

        final int expectedValue = 0;
        final int actualValue = dataManager.getActiveDataListeners().size();

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * When the sending is started the {@link DataManager#stopSending(Context)} should be called.
     */
    @Test
    public void startSending_shouldCallStop() {
        mockDataManager.startSending(context);
        verify(mockDataManager, times(1)).stopSending(context);
    }

    /**
     * When the sending is started the {@link ServiceScheduler#schedule()} should be called.
     */
    @Test
    public void startSending_shouldCallSchedule() {
        dataManager.startSending(context);

        verify(dataManager.metricServiceScheduler, times(1)).schedule();
        verify(dataManager.traceServiceScheduler, times(1)).schedule();
    }

    /**
     * Checks that the {@link DataManager#getInstance(Context)} returns a Singleton value.
     */
    @Test
    public void getInstance_assertIsSingleton() {
        final DataManager expectedValue = DataManager.getInstance(context);
        final DataManager actualValue = DataManager.getInstance(context);
        assertThat(actualValue, sameInstance(expectedValue));
    }

    /**
     * Checks that a call to {@link DataManager#isInitialised()} should return {@code true} after a call made to
     * {@link DataManager#getInstance(Context)}.
     */
    @Test
    public void getInstance_shouldInitialise() {
        DataManager.getInstance(context);
        final boolean actualValue = DataManager.isInitialised();
        assertThat(actualValue, is(true));
    }

    /**
     * Checks that a call to {@link DataManager#isInitialised()} should return {@code false} after a call made to
     * {@link DataManager#reset()}.
     */
    @Test
    public void reset_shouldNotBeInitialised() {
        DataManager.getInstance(context);
        DataManager.reset();
        final boolean actualValue = DataManager.isInitialised();
        assertThat(actualValue, is(false));
    }

    /**
     * When the {@link DataManager#handleReceivedData(Data)} is called with a valid {@link Data} that will be
     * converted to a {@link io.opencensus.proto.metrics.v1.Metric} it should not throw an exception and be saved
     * to the database correctly.
     *
     * Note: We use the @UiThreadTest annotation to ensure this test runs synchronously on it's own.
     * however, as we also use Room for our db, we must do that off the main thread, hence the AsyncTask.
     */
    @Test
    @UiThreadTest
    public void handleReceivedData_shouldHandleMetricWithoutException() {
        AsyncTask.execute(() -> {
            DataManager.getInstance(context);
            ApplicationSessionManager.getInstance().startSession();
            final DataStorage traceDataStorage = TestDataStorage.getInstance(context);
            dataManager.setDataStorage(traceDataStorage);

            final Data data = new Data(DataSourceType.SYSTEM_USED_MEMORY);
            final long dummyValue = 500L;
            data.setContent(dummyValue);
            dataManager.handleReceivedData(data);

            assertThat(traceDataStorage.getAllMetrics().size(), is(equalTo(1)));
        });
    }
}
