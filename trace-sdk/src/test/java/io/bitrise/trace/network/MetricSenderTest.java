package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import com.google.common.util.concurrent.SettableFuture;
import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import okhttp3.Headers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Response;

/**
 * Unit tests for {@link MetricSender}.
 */
public class MetricSenderTest {

  private static final DataManager mockDataManager = mock(DataManager.class);
  private static final DataStorage mockDataStorage = mock(DataStorage.class);
  private static final MetricSender metricSender = new MetricSender();
  final MetricSender mockMetricSender = Mockito.mock(MetricSender.class);
  final JobParameters mockJobParameters = Mockito.mock(JobParameters.class);

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
    metricSender.setDataStorage(mockDataStorage);
    metricSender.setDataManager(mockDataManager);
  }

  @Test
  public void needReschedule_shouldBeFalse() {
    when(mockDataStorage.getAllMetrics()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertFalse(metricSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasMetrics() {
    when(mockDataStorage.getAllMetrics()).thenReturn(
        Collections.singletonList(new MetricEntity(MetricTestProvider.getEmptyMetric())));
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertTrue(metricSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasActiveCollector() {
    when(mockDataStorage.getAllMetrics()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(
        Collections.singleton(new DeviceOsVersionDataCollector()));
    when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

    assertTrue(metricSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasActiveListener() {
    when(mockDataStorage.getAllMetrics()).thenReturn(Collections.emptyList());
    when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
    when(mockDataManager.getActiveDataListeners()).thenReturn(
        Collections.singleton(mock(OkHttpDataListener.class)));

    assertTrue(metricSender.isRescheduleNeeded());
  }

  @Test
  public void needReschedule_shouldBeTrue_hasAll() {
    when(mockDataStorage.getAllMetrics()).thenReturn(
        Collections.singletonList(new MetricEntity(MetricTestProvider.getEmptyMetric())));
    when(mockDataManager.getActiveDataCollectors()).thenReturn(
        Collections.singleton(new DeviceOsVersionDataCollector()));
    when(mockDataManager.getActiveDataListeners()).thenReturn(
        Collections.singleton(mock(OkHttpDataListener.class)));

    assertTrue(metricSender.isRescheduleNeeded());
  }

  @Test
  public void headerComparison_headersMatch() {
    final List<Metric> metricList = new ArrayList<>();
    metricList.add(MetricTestProvider.getApplicationStartUpMetric());
    metricList.add(MetricTestProvider.getApplicationCpuMetric());
    metricList.add(MetricTestProvider.getSystemCpuMetric());
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of(
        MetricSender.METRIC_HEADER_ACCEPTED_COUNT, "3",
        MetricSender.METRIC_HEADER_ACCEPTED_LABELS,
        "app.startup.latency.ms,process.cpu.pct,system.cpu.pct"));

    assertEquals(0, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void headerComparison_headersDoNotMatch_backendAcceptedLess() {
    final List<Metric> metricList = new ArrayList<>();
    metricList.add(MetricTestProvider.getApplicationStartUpMetric());
    metricList.add(MetricTestProvider.getApplicationCpuMetric());
    metricList.add(MetricTestProvider.getSystemCpuMetric());
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of(
        MetricSender.METRIC_HEADER_ACCEPTED_COUNT, "1",
        MetricSender.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms"));

    assertEquals(2, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void headerComparison_headersDoNotMatch_backendAcceptedMore() {
    final List<Metric> metricList = new ArrayList<>();
    metricList.add(MetricTestProvider.getApplicationStartUpMetric());
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of(
        MetricSender.METRIC_HEADER_ACCEPTED_COUNT, "3",
        MetricSender.METRIC_HEADER_ACCEPTED_LABELS,
        "app.startup.latency.ms,process.cpu.pct,system.cpu.pct"));

    assertEquals(2, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void headerComparison_headersNotInt() {
    final List<Metric> metricList = new ArrayList<>();
    metricList.add(MetricTestProvider.getApplicationStartUpMetric());
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of(
        MetricSender.METRIC_HEADER_ACCEPTED_COUNT, "cats",
        MetricSender.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms"));

    assertEquals(0, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void headerComparison_headersMissing() {
    final List<Metric> metricList = new ArrayList<>();
    metricList.add(MetricTestProvider.getApplicationStartUpMetric());
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of());

    assertEquals(0, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void headerComparison_noMetricsSent() {
    final List<Metric> metricList = new ArrayList<>();
    final MetricRequest request = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), metricList);
    final Response<Void> response = Response.success(null, Headers.of(
        MetricSender.METRIC_HEADER_ACCEPTED_COUNT, "0",
        MetricSender.METRIC_HEADER_ACCEPTED_LABELS, ""));

    assertEquals(0, MetricSender.countHeaderComparisonDifference(request, response));
  }

  @Test
  public void send_hasStopped() throws ExecutionException, InterruptedException {
    mockMetricSender.setStopped(true);

    final Future<DataSender.Result> settableFuture = metricSender.send(mockJobParameters);

    assertEquals(DataSender.Result.FAILURE, settableFuture.get());
  }

  @Test
  public void send_invalidNetworkRequest() throws ExecutionException, InterruptedException {
    mockMetricSender.setStopped(false);
    when(mockMetricSender.getNetworkRequest()).thenReturn(null);

    final Future<DataSender.Result> settableFuture = metricSender.send(mockJobParameters);

    assertEquals(DataSender.Result.FAILURE, settableFuture.get());
  }

  @Test
  public void onStartJob() {
    final MetricSender mockMetricSender = Mockito.mock(MetricSender.class,
        Mockito.CALLS_REAL_METHODS);
    boolean serviceShouldContinue = mockMetricSender.onStartJob(mockJobParameters);

    assertTrue(serviceShouldContinue);
    verify(mockMetricSender, times(1)).send(mockJobParameters);
  }

  @Test
  public void getNetworkRequest_noItems() {
    when(mockDataStorage.getFirstMetricGroup())
        .thenReturn(new ArrayList<>());

    assertNull(metricSender.getNetworkRequest());
  }

  @Test
  public void getMetricEntityList_hasNoItems() {
    metricSender.setMetricEntityList(null);
    assertEquals(new ArrayList<MetricEntity>(), metricSender.getMetricEntityList());
  }

  @Test
  public void getMetricEntityList_hasItems() {
    final List<MetricEntity> metricList = new ArrayList<>();
    metricList.add(new MetricEntity(MetricTestProvider.getSystemMemoryUsage()));
    metricSender.setMetricEntityList(metricList);

    assertEquals(metricList, metricSender.getMetricEntityList());
  }
}