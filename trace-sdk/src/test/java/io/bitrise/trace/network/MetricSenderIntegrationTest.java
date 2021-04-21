package io.bitrise.trace.network;

import androidx.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests that send a metric to the backend and expect confirmation by headers.
 */
public class MetricSenderIntegrationTest {

    private static final String TRACE_TOKEN = "70a14e32-97b1-4b6d-b4bc-ff592c780325";

    @Before
    public void setUp() {
        ConfigurationManager.getDebugInstance(TRACE_TOKEN);
    }

    @After
    public void tearDown() {
        ConfigurationManager.reset();
    }

    /**
     * Method that sends an individual metric to the back end and confirms the metric count and metric
     * label were accepted.
     * @param metric the {@link Metric} to send.
     * @throws IOException if there are any issues executing the network request.
     */
    private void sendMetricList(@NonNull final Metric metric) throws IOException {
        final List<Metric> metricList = new ArrayList<>();
        metricList.add(metric);
        final MetricRequest request = new MetricRequest(DataTestUtils.getSampleResource("sessionId"), metricList);
        final Response<Void> response = NetworkClient.getCommunicator().sendMetrics(request).execute();
        assertEquals("1", response.headers().get("accepted-metrics-count"));
        assertEquals(metricList.get(0).getMetricDescriptor().getName(), response.headers().get("accepted-metrics-labels"));
    }

    @Test
    public void sendMetric_applicationStartUp() throws IOException {
        sendMetricList(MetricTestProvider.getApplicationStartUpMetric());
    }

    @Test
    public void sendMetric_applicationCpu() throws IOException{
        sendMetricList(MetricTestProvider.getApplicationCpuMetric());
    }

    @Test
    public void sendMetric_applicationMemoryUsage() throws IOException{
        sendMetricList(MetricTestProvider.getApplicationMemoryUsage());
    }

    @Test
    public void sendMetric_systemCpu() throws IOException{
        sendMetricList(MetricTestProvider.getSystemCpuMetric());
    }

    @Test
    public void sendMetric_systemMemoryUsage() throws IOException{
        sendMetricList(MetricTestProvider.getSystemMemoryUsage());
    }
}
