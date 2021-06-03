package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.job.JobParameters;
import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.TraceTestProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link TraceSender}.
 */
public class TraceSenderTest {

  private static final DataManager mockDataManager = mock(DataManager.class);
  private static final TraceSender traceSender = new TraceSender();
  private static final DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
  final JobParameters mockJobParameters = Mockito.mock(JobParameters.class);

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
    traceSender.setDataStorage(mockDataStorage);
    traceSender.setDataManager(mockDataManager);
  }

  @Test
  public void findEmptyTraces_emptyList() {
    final List<Trace> inputTraces = new ArrayList<>();
    final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);
    assertEquals(0, outputTraces.size());
  }

  @Test
  public void findEmptyTraces_nullList() {
    final List<Trace> outputTraces = traceSender.findEmptyTraces(null);
    assertEquals(0, outputTraces.size());
  }

  @Test
  public void findEmptyTraces_twoValidTraces() {
    final List<Trace> inputTraces = new ArrayList<>();
    inputTraces.add(TraceTestProvider.getSampleTrace());
    inputTraces.add(TraceTestProvider.getOtherTrace());

    final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

    assertEquals(0, outputTraces.size());
  }

  @Test
  public void findEmptyTraces_twoEmptyTraces() {
    final List<Trace> inputTraces = new ArrayList<>();
    inputTraces.add(TraceTestProvider.getEmptyTrace());
    inputTraces.add(TraceTestProvider.getEmptyTrace());

    final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

    assertEquals(2, outputTraces.size());
  }

  @Test
  public void findEmptyTraces_twoValidTraces_andTwoEmptyTraces() {
    final List<Trace> inputTraces = new ArrayList<>();
    inputTraces.add(TraceTestProvider.getSampleTrace());
    inputTraces.add(TraceTestProvider.getOtherTrace());
    inputTraces.add(TraceTestProvider.getEmptyTrace());
    inputTraces.add(TraceTestProvider.getEmptyTrace());

    final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

    assertEquals(2, outputTraces.size());
  }

  @Test
  public void needReschedule_shouldBeFalse() {
    when(mockDataStorage.getAllMetrics()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertFalse(traceSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasTraces() {
    when(mockDataStorage.getAllTraces())
        .thenReturn(Collections.singletonList(TraceTestProvider.getEmptyTrace()));
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertTrue(traceSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasActiveCollector() {
    when(mockDataStorage.getAllTraces()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(
        Collections.singleton(new DeviceOsVersionDataCollector()));
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertTrue(traceSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasActiveListener() {
    when(mockDataStorage.getAllTraces()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(
        Collections.singleton(mock(OkHttpDataListener.class)));

    assertTrue(traceSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasAll() {
    when(mockDataStorage.getAllTraces())
        .thenReturn(Collections.singletonList(TraceTestProvider.getEmptyTrace()));
    when(mockDataManager.getActiveDataCollectors()).thenReturn(
        Collections.singleton(new DeviceOsVersionDataCollector()));
    when(mockDataManager.getActiveDataListeners()).thenReturn(
        Collections.singleton(mock(OkHttpDataListener.class)));

    assertTrue(traceSender.isRescheduleNeeded());
  }

  @Test
  public void getTraceList_isNull() {
    traceSender.setTraceList(null);
    assertEquals(new ArrayList<Trace>(), traceSender.getTraceList());
  }

  @Test
  public void getTraceList_hasItems() {
    final List<Trace> traces = new ArrayList<>();
    traces.add(TraceTestProvider.getSampleTrace());
    traceSender.setTraceList(traces);
    assertEquals(traces, traceSender.getTraceList());
  }

  @Test
  public void getNetworkRequest_noItems() {
    when(mockDataStorage.getFirstTraceGroup())
        .thenReturn(new ArrayList<>());

    assertNull(traceSender.getNetworkRequest());
  }

  @Test
  public void onStartJob() {
    final TraceSender mockTraceSender = Mockito.mock(TraceSender.class,
        Mockito.CALLS_REAL_METHODS);
    boolean serviceShouldContinue = mockTraceSender.onStartJob(mockJobParameters);

    assertTrue(serviceShouldContinue);
    verify(mockTraceSender, times(1)).send(mockJobParameters);
  }

  @Test
  public void send_hasStopped() throws ExecutionException, InterruptedException {
    traceSender.setStopped(true);
    final Future<DataSender.Result> settableFuture = traceSender.send(mockJobParameters);

    assertEquals(DataSender.Result.FAILURE, settableFuture.get());
  }

  @Test
  public void send_invalidNetworkRequest() throws ExecutionException, InterruptedException {
    traceSender.setStopped(false);
    traceSender.setTraceList(null);
    final Future<DataSender.Result> settableFuture = traceSender.send(mockJobParameters);

    assertEquals(DataSender.Result.FAILURE, settableFuture.get());
  }
}
