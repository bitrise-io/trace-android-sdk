package io.bitrise.trace.network;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.bitrise.trace.internal.TestUtils;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link MetricRequest}.
 */
public class MetricRequestTest {

    /**
     * Create a MetricRequest with as close to real data as possible and ensure it matches
     * the expected json request.
     * @throws IOException if there is an issue with expected json file.
     */
    @Test
    public void MetricRequestConstructor() throws IOException {

        // given some metrics
        final List<Metric> metricList = new ArrayList<>();
        metricList.add(MetricTestProvider.getApplicationStartUpMetric());
        metricList.add(MetricTestProvider.getApplicationCpuMetric());
        metricList.add(MetricTestProvider.getSystemCpuMetric());
        metricList.add(MetricTestProvider.getApplicationMemoryUsage());
        metricList.add(MetricTestProvider.getSystemMemoryUsage());

        // when I get a metric request using the NetworkClient gson
        final MetricRequest metricRequest = new MetricRequest(
                DataTestUtils.getSampleResource("session-id"), metricList);
        final String requestJson = NetworkClient.getGson().toJson(metricRequest);

        // it should match the expected json
        final String filePath = "src/test/resources/io/bitrise/trace/network/metric_request.json";
        final String expectedJson = TestUtils.getJsonContentRemovingWhitespace(filePath);

        assertEquals(expectedJson, requestJson);
    }

}
