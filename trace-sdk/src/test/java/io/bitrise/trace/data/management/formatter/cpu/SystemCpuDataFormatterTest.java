package io.bitrise.trace.data.management.formatter.cpu;

import androidx.annotation.NonNull;

import com.google.protobuf.Timestamp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.bitrise.trace.data.collector.cpu.CpuUsageData;
import io.bitrise.trace.data.collector.cpu.SystemCpuUsageDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

import static io.bitrise.trace.data.dto.DataValues.cpu;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.idle;
import static io.bitrise.trace.data.dto.DataValues.ioWait;
import static io.bitrise.trace.data.dto.DataValues.irq;
import static io.bitrise.trace.data.dto.DataValues.nice;
import static io.bitrise.trace.data.dto.DataValues.pct;
import static io.bitrise.trace.data.dto.DataValues.percent;
import static io.bitrise.trace.data.dto.DataValues.softIrq;
import static io.bitrise.trace.data.dto.DataValues.state;
import static io.bitrise.trace.data.dto.DataValues.steal;
import static io.bitrise.trace.data.dto.DataValues.system;
import static io.bitrise.trace.data.dto.DataValues.user;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link SystemCpuDataFormatter}.
 */
public class SystemCpuDataFormatterTest extends BaseDataFormatterTest {

    final SystemCpuDataFormatter formatter = new SystemCpuDataFormatter();
    final CpuUsageData.CpuStat cpuStat = new CpuUsageData.CpuStat(1,2,3,4,5,6,7,8);
    final String metricDescriptionDescription = "System CPU Usage";

    @Test
    public void formatData_notCpuStat() {
        final Data data = new Data(SystemCpuUsageDataCollector.class);
        data.setContent("cpu usage");
        assertArrayEquals(new FormattedData[]{}, formatter.formatData(data));
    }

    @Test
    public void formatData() {
        final Data data = new Data(SystemCpuUsageDataCollector.class);
        data.setContent(cpuStat);

        final FormattedData formattedData = formatter.formatData(data)[0];

        final Timestamp timestamp = formattedData.getMetricEntity().getMetric()
                .getTimeseries(0).getPoints(0).getTimestamp();

        List<TimeSeries> expectedTimeSeries = new ArrayList<>(Arrays.asList(
                createCpuTimeSeriesEntry(timestamp, user, cpuStat.getUser()),
                createCpuTimeSeriesEntry(timestamp, system, cpuStat.getSystem()),
                createCpuTimeSeriesEntry(timestamp, nice, cpuStat.getNice()),
                createCpuTimeSeriesEntry(timestamp, idle, cpuStat.getIdle()),
                createCpuTimeSeriesEntry(timestamp, ioWait, cpuStat.getIoWait()),
                createCpuTimeSeriesEntry(timestamp, irq, cpuStat.getIrq()),
                createCpuTimeSeriesEntry(timestamp, softIrq, cpuStat.getSoftIrq()),
                createCpuTimeSeriesEntry(timestamp, steal, cpuStat.getSteal())));

        final Metric.Builder metricBuilder = Metric.newBuilder();
        final MetricDescriptor.Builder cpuDescriptorBuilder =
                MetricDescriptor.newBuilder()
                        .setDescription(metricDescriptionDescription)
                        .setName(getName(system, cpu, pct))
                        .setUnit(percent)
                        .setType(MetricDescriptor.Type.GAUGE_DOUBLE)
                        .addLabelKeys(LabelKey.newBuilder()
                                .setKey(getName(cpu, state))
                                .build());

        metricBuilder.setMetricDescriptor(cpuDescriptorBuilder.build());
        metricBuilder.addAllTimeseries(expectedTimeSeries);
        final Metric expectedMetric = metricBuilder.build();

        assertEquals(expectedMetric, formattedData.getMetricEntity().getMetric() );
    }

    @NonNull
    private static TimeSeries createCpuTimeSeriesEntry(@NonNull final Timestamp timestamp,
                                                       @NonNull final String labelName,
                                                       final float value) {
        return TimeSeries.newBuilder()
                .addLabelValues(
                        LabelValue.newBuilder()
                                .setValue(labelName)
                                .build())
                .addPoints(
                        Point.newBuilder()
                                .setTimestamp(timestamp)
                                .setDoubleValue(value)
                                .build())
                .build();
    }
}
