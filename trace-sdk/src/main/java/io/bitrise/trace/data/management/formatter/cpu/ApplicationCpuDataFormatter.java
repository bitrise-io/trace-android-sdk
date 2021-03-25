package io.bitrise.trace.data.management.formatter.cpu;

import androidx.annotation.NonNull;

import com.google.protobuf.Timestamp;

import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

import static io.bitrise.trace.data.dto.DataValues.cpu;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.pct;
import static io.bitrise.trace.data.dto.DataValues.percent;
import static io.bitrise.trace.data.dto.DataValues.process;

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#APP_CPU_USAGE}.
 */
public class ApplicationCpuDataFormatter extends DataFormatter {

    @NonNull
    @Override
    public FormattedData[] formatData(@NonNull final Data data) {
        if (!(data.getContent() instanceof Double)) {
            return new FormattedData[]{};
        }
        final Double appCpuStat = (Double) data.getContent();
        if (appCpuStat == null) {
            return new FormattedData[]{};
        }
        final Timestamp timestamp = getTimestamp();
        final Metric appCpuMetric = createAppCpuMetric(appCpuStat, timestamp);


        return new FormattedData[]{new FormattedData(appCpuMetric)};
    }

    /**
     * Creates a {@link Metric} for the CPU usage of the Application.
     *
     * @param cpuUsagePercent the percentage of CPU usage.
     * @param timestamp       the {@link Timestamp} of the measurement.
     * @return the created Metric.
     */
    @NonNull
    public static Metric createAppCpuMetric(final double cpuUsagePercent, @NonNull final Timestamp timestamp) {
        final Metric.Builder builder = Metric.newBuilder();
        final MetricDescriptor.Builder cpuDescriptorBuilder =
                MetricDescriptor.newBuilder()
                                .setDescription("Application CPU Usage")
                                .setName(getName(process, cpu, pct))
                                .setUnit(percent)
                                .setType(MetricDescriptor.Type.GAUGE_DOUBLE);

        final TimeSeries appTimeSeries = createCpuTimeSeriesEntry(timestamp, (float) cpuUsagePercent);
        builder.setMetricDescriptor(cpuDescriptorBuilder.build());
        builder.addTimeseries(appTimeSeries);
        return builder.build();
    }

    /**
     * Creates a {@link TimeSeries} entry with the given label name and value.
     *
     * @param timestamp the {@link Timestamp}.
     * @param value     the float value.
     * @return the created TimeSeries.
     */
    @NonNull
    private static TimeSeries createCpuTimeSeriesEntry(@NonNull final Timestamp timestamp, final float value) {
        return TimeSeries.newBuilder()
                         .addPoints(
                                 Point.newBuilder()
                                      .setTimestamp(timestamp)
                                      .setDoubleValue(value)
                                      .build())
                         .build();
    }
}
