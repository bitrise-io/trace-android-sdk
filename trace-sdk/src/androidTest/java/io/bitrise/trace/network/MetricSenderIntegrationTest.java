package io.bitrise.trace.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.InstrumentedTestRequirements;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.cpu.ApplicationCpuUsageDataCollector;
import io.bitrise.trace.data.collector.cpu.SystemCpuUsageDataCollector;
import io.bitrise.trace.data.collector.memory.ApplicationUsedMemoryDataCollector;
import io.bitrise.trace.data.collector.memory.SystemMemoryDataCollector;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.dto.ApplicationStartData;
import io.bitrise.trace.data.dto.ApplicationStartType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.cpu.ApplicationCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.cpu.SystemCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.ApplicationUsedMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.SystemMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.view.ApplicationStartUpDataFormatter;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.opencensus.proto.metrics.v1.Metric;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests that send a metric to the backend and expect confirmation by headers.
 *
 * Note: to run these tests locally, you need to add a traceToken property to local.properties
 * with a valid trace token. Please see the readme for more information.
 */
@LargeTest
public class MetricSenderIntegrationTest {

    @BeforeClass
    public static void setUp() {
        ConfigurationManager.getDebugInstance(BuildConfig.TRACE_TOKEN, new HashMap<>());
        ApplicationSessionManager.getInstance().startSession();
    }

    @AfterClass
    public static void tearDown() {
        ConfigurationManager.reset();
        ApplicationSessionManager.getInstance().stopSession();
    }

    /**
     * Method that sends an individual metric to the back end and confirms the metric count and metric
     * label were accepted.
     *
     * @param formattedData the output from calling a formatter.format()
     * @throws IOException if there are any issues executing the network request.
     */
    private void sendMetricList(@NonNull final FormattedData[] formattedData) throws IOException {
        final List<Metric> metricList = new ArrayList<>();
        metricList.add(formattedData[0].getMetricEntity().getMetric());
        final MetricRequest request = new MetricRequest(DataTestUtils
                .getSampleResource("sessionId"), metricList);
        final Response<Void> response = NetworkClient.getCommunicator().sendMetrics(request).execute();
        assertEquals("1",
                response.headers().get(NetworkCommunicator.METRIC_HEADER_ACCEPTED_COUNT));
        assertEquals(metricList.get(0).getMetricDescriptor().getName(),
                response.headers().get(NetworkCommunicator.METRIC_HEADER_ACCEPTED_LABELS));
    }

    @Test
    public void sendMetric_applicationStartUp() throws IOException {
        final Data data = new Data(ApplicationStartUpDataListener.class);
        data.setContent(new ApplicationStartData(100L, ApplicationStartType.COLD));
        final ApplicationStartUpDataFormatter formatter = new ApplicationStartUpDataFormatter();
        sendMetricList(formatter.formatData(data));
    }

    @Test
    public void sendMetric_applicationCpu() throws IOException{
        final ApplicationCpuUsageDataCollector collector = new ApplicationCpuUsageDataCollector();
        final ApplicationCpuDataFormatter formatter = new ApplicationCpuDataFormatter();
        sendMetricList(formatter.formatData(collector.collectData()));
    }

    @Test
    public void sendMetric_applicationMemoryUsage() throws IOException{
        final Context context = InstrumentationRegistry.getInstrumentation().getContext();
        final ApplicationUsedMemoryDataCollector collector = new ApplicationUsedMemoryDataCollector(context);
        final ApplicationUsedMemoryDataFormatter formatter = new ApplicationUsedMemoryDataFormatter();
        sendMetricList(formatter.formatData(collector.collectData()));
    }

    @Test
    public void sendMetric_systemCpu() throws IOException{
        InstrumentedTestRequirements.assumeCpuApiLevel();
        final SystemCpuUsageDataCollector collector = new SystemCpuUsageDataCollector();
        final SystemCpuDataFormatter formatter = new SystemCpuDataFormatter();
        sendMetricList(formatter.formatData(collector.collectData()));
    }

    @Test
    public void sendMetric_systemMemoryUsage() throws IOException{
        final Context context = InstrumentationRegistry.getInstrumentation().getContext();
        final SystemMemoryDataCollector collector = new SystemMemoryDataCollector(context);
        final SystemMemoryDataFormatter formatter = new SystemMemoryDataFormatter();
        sendMetricList(formatter.formatData(collector.collectData()));
    }
}
