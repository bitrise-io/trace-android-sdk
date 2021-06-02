package io.bitrise.trace.data.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.memory.MemoryDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.scheduler.ExecutorScheduler;
import io.bitrise.trace.scheduler.ServiceScheduler;
import io.bitrise.trace.session.ApplicationSessionManager;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link DataManager}.
 */
public class DataManagerTest {

  final DataManager mockDataManager = Mockito.mock(DataManager.class);
  final ConfigurationManager mockConfigurationManager = Mockito.mock(ConfigurationManager.class);
  final Context mockContext = Mockito.mock(Context.class);

  @AfterClass
  public static void resetAfterAllTests() {
    DataManager.reset();
  }

  private DataManager createRealDataManager() {
    DataManager.reset();
    final DataManager dataManager = DataManager.getInstance(mockContext);
    dataManager.configurationManager = mockConfigurationManager;

    ApplicationSessionManager.getInstance().startSession();
    dataManager.traceManager.startTrace();

    return dataManager;
  }

  @Test
  public void getInstance() {
    DataManager.setTestInstance(mockDataManager);
    assertEquals(mockDataManager, DataManager.getInstance(mockContext));
  }

  @Test
  public void isInitialised_true() {
    DataManager.setTestInstance(mockDataManager);
    assertTrue(DataManager.isInitialised());
  }

  @Test
  public void isInitialised_false() {
    DataManager.reset();
    assertFalse(DataManager.isInitialised());
  }

  @Test
  public void reset_everythingInitialised() {
    final ExecutorScheduler mockExecutorScheduler = Mockito.mock(ExecutorScheduler.class);
    final ServiceScheduler mockMetricServiceScheduler = Mockito.mock(ServiceScheduler.class);
    final ServiceScheduler mockTraceServiceScheduler = Mockito.mock(ServiceScheduler.class);

    DataManager.setTestInstance(mockDataManager);
    mockDataManager.executorScheduler = mockExecutorScheduler;
    mockDataManager.metricServiceScheduler = mockMetricServiceScheduler;
    mockDataManager.traceServiceScheduler = mockTraceServiceScheduler;

    DataManager.reset();

    verify(mockDataManager, times(1)).stopCollection();
    verify(mockExecutorScheduler, times(1)).cancelAll();
    verify(mockMetricServiceScheduler, times(1)).cancelAll();
    verify(mockTraceServiceScheduler, times(1)).cancelAll();

    assertNull(mockDataManager.executorScheduler);
    assertNull(mockDataManager.metricServiceScheduler);
    assertNull(mockDataManager.traceServiceScheduler);
    assertFalse(DataManager.isInitialised());
  }

  @Test
  public void reset_nothingInitialised() {
    DataManager.reset();
    assertFalse(DataManager.isInitialised());
    DataManager.reset();
    assertFalse(DataManager.isInitialised());
  }

  @Test
  public void setDataStorage() {
    final DataManager mockDataManager = Mockito.mock(DataManager.class, Mockito.CALLS_REAL_METHODS);
    final TraceDataStorage mockDataStorage = Mockito.mock(TraceDataStorage.class);
    mockDataManager.setDataStorage(mockDataStorage);
    assertEquals(mockDataStorage, mockDataManager.dataStorage);
  }

  // region collections

  @Test
  public void startCollection_hasCollectorsAndListenersAlready() {
    final DataManager dataManager = createRealDataManager();

    dataManager.activeDataCollectors.add(Mockito.mock(MemoryDataCollector.class));
    dataManager.activeDataListeners.add(Mockito.mock(ApplicationStartUpDataListener.class));

    dataManager.startCollection(mockContext);

    assertEquals(1, dataManager.activeDataCollectors.size());
    assertEquals(1, dataManager.activeDataListeners.size());
  }

  @Test
  public void startCollecting_noCurrentCollectorsOrListeners() {
    final DataCollector mockCollector = Mockito.mock(MemoryDataCollector.class);
    final Set<DataCollector> collectors = new HashSet<>();
    collectors.add(mockCollector);

    final DataListener dataListener = Mockito.mock(ApplicationStartUpDataListener.class);
    final LinkedHashSet<DataListener> listeners = new LinkedHashSet<>();
    listeners.add(dataListener);

    when(mockConfigurationManager.getDataCollectors(any())).thenReturn(collectors);
    when(mockConfigurationManager.getDataListeners(any())).thenReturn(listeners);

    final DataManager dataManager = createRealDataManager();
    dataManager.startCollection(mockContext);

    assertEquals(collectors, dataManager.getActiveDataCollectors());
    assertEquals(listeners, dataManager.getActiveDataListeners());
  }

  @Test
  public void stopCollecting() {
    final DataManager dataManager = createRealDataManager();

    dataManager.activeDataCollectors.add(Mockito.mock(MemoryDataCollector.class));
    dataManager.activeDataListeners.add(Mockito.mock(ApplicationStartUpDataListener.class));

    dataManager.stopCollection();

    assertEquals(0, dataManager.getActiveDataCollectors().size());
    assertEquals(0, dataManager.getActiveDataListeners().size());
  }

  //endregion

  // region handleReceivedData

  @Test
  public void handleReceivedData_trace() {
    final DataManager dataManager = createRealDataManager();
    assertNotNull(dataManager.traceManager.getActiveTrace());
    assertEquals(0, dataManager.traceManager.getActiveTrace().getSpanList().size());

    dataManager.handleReceivedData(createNetworkSpanData());

    assertEquals(1, dataManager.traceManager.getActiveTrace().getSpanList().size());
  }

  private Data createNetworkSpanData() {
    final Data data = new Data(OkHttpDataListener.class);
    final NetworkData networkData = new NetworkData("spanId", "parentSpanId");
    networkData.setUrl("url");
    networkData.setMethod("GET");
    networkData.setStatusCode(202);
    networkData.setStart(1000L);
    networkData.setEnd(2000L);
    data.setContent(networkData);
    return data;
  }

  @Test
  public void handleReceivedData_nullContent() {
    final DataManager dataManager = createRealDataManager();
    dataManager.handleReceivedData(createNullContentData());

    assertNotNull(dataManager.traceManager.getActiveTrace());
    assertEquals(0, dataManager.traceManager.getActiveTrace().getSpanList().size());
  }

  private Data createNullContentData() {
    final Data data = new Data(OkHttpDataListener.class);
    data.setContent(null);
    return data;
  }

  //endregion
}
