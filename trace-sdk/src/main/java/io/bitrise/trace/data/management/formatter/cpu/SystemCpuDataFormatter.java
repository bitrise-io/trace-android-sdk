package io.bitrise.trace.data.management.formatter.cpu;

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

import androidx.annotation.NonNull;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.collector.cpu.CpuUsageData.CpuStat;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * {@link Formatter} implementation, to handle formatting for
 * {@link DataSourceType#SYSTEM_CPU_USAGE}.
 */
public class SystemCpuDataFormatter extends DataFormatter {

  /**
   * Creates a {@link Metric} for the CPU usage of the System.
   *
   * @param systemCpuStat the {@link CpuStat} of the the CPU usage of the System.
   * @param timestamp     the {@link Timestamp} of the measurement.
   * @return the created Metric.
   */
  @NonNull
  public static Metric createSystemCpuMetric(@NonNull final CpuStat systemCpuStat,
                                             @NonNull final Timestamp timestamp) {
    final Metric.Builder builder = Metric.newBuilder();
    final MetricDescriptor.Builder cpuDescriptorBuilder =
        MetricDescriptor.newBuilder()
                        .setDescription("System CPU Usage")
                        .setName(getName(system, cpu, pct))
                        .setUnit(percent)
                        .setType(MetricDescriptor.Type.GAUGE_DOUBLE)
                        .addLabelKeys(LabelKey.newBuilder()
                                              .setKey(getName(cpu, state))
                                              .build());

    final ArrayList<TimeSeries> systemTimeSeries =
        createSystemCpuTimeSeries(systemCpuStat, timestamp);
    builder.setMetricDescriptor(cpuDescriptorBuilder.build());
    builder.addAllTimeseries(systemTimeSeries);
    return builder.build();
  }

  /**
   * Gets the {@link TimeSeries} for the System CPU usage.
   *
   * @param cpuStat   the {@link CpuStat} of the the CPU usage of the System.
   * @param timestamp the {@link Timestamp} of the measurement.
   * @return the List of TimeSeries.
   */
  @NonNull
  private static ArrayList<TimeSeries> createSystemCpuTimeSeries(@NonNull final CpuStat cpuStat,
                                                                 @NonNull
                                                                 final Timestamp timestamp) {
    return new ArrayList<>(Arrays.asList(
        createCpuTimeSeriesEntry(timestamp, user, cpuStat.getUser()),
        createCpuTimeSeriesEntry(timestamp, system, cpuStat.getSystem()),
        createCpuTimeSeriesEntry(timestamp, nice, cpuStat.getNice()),
        createCpuTimeSeriesEntry(timestamp, idle, cpuStat.getIdle()),
        createCpuTimeSeriesEntry(timestamp, ioWait, cpuStat.getIoWait()),
        createCpuTimeSeriesEntry(timestamp, irq, cpuStat.getIrq()),
        createCpuTimeSeriesEntry(timestamp, softIrq, cpuStat.getSoftIrq()),
        createCpuTimeSeriesEntry(timestamp, steal, cpuStat.getSteal())));
  }

  /**
   * Creates a {@link TimeSeries} entry with the given label name and value.
   *
   * @param timestamp the {@link Timestamp}.
   * @param labelName the name of the label.
   * @param value     the float value.
   * @return the created TimeSeries.
   */
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

  @NonNull
  @Override
  public FormattedData[] formatData(@NonNull final Data data) {
    if (!(data.getContent() instanceof CpuStat)) {
      return new FormattedData[] {};
    }

    final CpuStat systemCpuStat = (CpuStat) data.getContent();
    final Timestamp timestamp = getTimestamp();
    final Metric systemCpuMetric = createSystemCpuMetric(systemCpuStat, timestamp);

    return new FormattedData[] {new FormattedData(systemCpuMetric)};
  }
}
