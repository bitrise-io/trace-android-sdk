package io.bitrise.trace.network;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;
import okhttp3.Headers;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MetricSender}.
 */
public class MetricSenderTest {

    private static final DataManager mockDataManager = mock(DataManager.class);
    private static final DataStorage mockDataStorage = mock(DataStorage.class);
    private static final MetricSender metricSender = new MetricSender();

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
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT, "3",
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms,process.cpu.pct,system.cpu.pct"));

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
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT, "1",
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms"));

        assertEquals(2, MetricSender.countHeaderComparisonDifference(request, response));
    }

    @Test
    public void headerComparison_headersDoNotMatch_backendAcceptedMore() {
        final List<Metric> metricList = new ArrayList<>();
        metricList.add(MetricTestProvider.getApplicationStartUpMetric());
        final MetricRequest request = new MetricRequest(
                DataTestUtils.getSampleResource("sessionId"), metricList);
        final Response<Void> response = Response.success(null, Headers.of(
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT, "3",
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms,process.cpu.pct,system.cpu.pct"));

        assertEquals(2, MetricSender.countHeaderComparisonDifference(request, response));
    }

    @Test
    public void headerComparison_headersNotInt() {
        final List<Metric> metricList = new ArrayList<>();
        metricList.add(MetricTestProvider.getApplicationStartUpMetric());
        final MetricRequest request = new MetricRequest(
                DataTestUtils.getSampleResource("sessionId"), metricList);
        final Response<Void> response = Response.success(null, Headers.of(
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT, "cats",
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS, "app.startup.latency.ms"));

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
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT, "0",
                NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS, ""));

        assertEquals(0, MetricSender.countHeaderComparisonDifference(request, response));
    }
}