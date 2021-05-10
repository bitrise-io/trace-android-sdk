package io.bitrise.trace.data.management.formatter.cpu;

import org.junit.Test;

import io.bitrise.trace.data.collector.cpu.ApplicationCpuUsageDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

import static io.bitrise.trace.data.dto.DataValues.cpu;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.pct;
import static io.bitrise.trace.data.dto.DataValues.percent;
import static io.bitrise.trace.data.dto.DataValues.process;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ApplicationCpuDataFormatter}.
 */
public class ApplicationCpuDataFormatterTest extends BaseDataFormatterTest {

    final ApplicationCpuDataFormatter formatter = new ApplicationCpuDataFormatter();
    final double cpuValue = 12.3;
    final String metricDescriptorDescription = "Application CPU Usage";

    @Test
    public void formatData_contentNotDouble() {
        final Data data = new Data(ApplicationCpuUsageDataCollector.class);
        data.setContent("cpu usage");
        assertArrayEquals(new FormattedData[]{}, formatter.formatData(data));
    }

    @Test
    public void formatData() {
        final Data data = new Data(ApplicationCpuUsageDataCollector.class);
        data.setContent(cpuValue);
        final FormattedData formattedData = formatter.formatData(data)[0];

        final TimeSeries expectedTimeSeries = TimeSeries.newBuilder()
                .addPoints(Point.newBuilder()
                                .setTimestamp(formattedData.getMetricEntity().getMetric()
                                        .getTimeseries(0).getPoints(0).getTimestamp())
                                .setDoubleValue(cpuValue)
                                .build())
                .build();

        final Metric.Builder metricBuilder = Metric.newBuilder();
        final MetricDescriptor.Builder cpuDescriptorBuilder =
                MetricDescriptor.newBuilder()
                        .setDescription(metricDescriptorDescription)
                        .setName(getName(process, cpu, pct))
                        .setUnit(percent)
                        .setType(MetricDescriptor.Type.GAUGE_DOUBLE);

        metricBuilder.setMetricDescriptor(cpuDescriptorBuilder.build());
        metricBuilder.addTimeseries(expectedTimeSeries);
        final Metric expectedMetric = metricBuilder.build();

        assertEquals(expectedMetric, formattedData.getMetricEntity().getMetric() );
    }
}
