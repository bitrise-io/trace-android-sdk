package io.bitrise.trace.data.management;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.os.AsyncTask;
import androidx.test.annotation.UiThreadTest;
import androidx.test.platform.app.InstrumentationRegistry;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.collector.DummyDataCollector;
import io.bitrise.trace.data.collector.DummyDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TestDataStorage;
import io.bitrise.trace.scheduler.ServiceScheduler;
import io.bitrise.trace.session.ApplicationSessionManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Contains the instrumented tests for {@link DataManager} class.
 */
public class DataManagerInstrumentedTest {

  private static final DataManager mockDataManager =
      mock(DataManager.class, Mockito.CALLS_REAL_METHODS);
  private static DataManager dataManager;
  private static ConfigurationManager mockConfigurationManager;
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
   * When the sending is stopped with {@link DataManager#stopSending()} the
   * {@link ServiceScheduler#cancelAll()} should be called.
   */
  @Test
  public void stopSending_shouldCallCancelAll() {
    dataManager.stopSending();

    verify(dataManager.metricServiceScheduler, times(1)).cancelAll();
    verify(dataManager.traceServiceScheduler, times(1)).cancelAll();
  }

  /**
   * When the sending is started the {@link DataManager#stopSending()} should be called.
   */
  @Test
  public void startSending_shouldCallStop() {
    mockDataManager.startSending(context);
    verify(mockDataManager, times(1)).stopSending();
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
   * When the {@link DataManager#handleReceivedData(Data)} is called with a valid {@link Data}
   * that will be converted to a {@link io.opencensus.proto.metrics.v1.Metric} it should not
   * throw an exception and be saved to the database correctly.
   *
   * <p>Note: We use the @UiThreadTest annotation to ensure this test runs synchronously on it's
   * own. However, as we also use Room for our db, we must do that off the main thread, hence the
   * AsyncTask.
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
