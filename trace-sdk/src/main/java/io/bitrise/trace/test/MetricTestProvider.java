package io.bitrise.trace.test;

import androidx.annotation.NonNull;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.collector.cpu.CpuUsageData;
import io.bitrise.trace.data.dto.ApplicationStartData;
import io.bitrise.trace.data.dto.ApplicationStartType;
import io.bitrise.trace.data.management.formatter.cpu.ApplicationCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.cpu.SystemCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.ApplicationUsedMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.MemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.SystemMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.view.ApplicationStartUpDataFormatter;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

/**
 * This class provides test {@link Metric}'s.
 */
public class MetricTestProvider {

  private MetricTestProvider() {
    throw new UnsupportedOperationException("Private constructor for MetricTestProvider!");
  }

  /**
   * Gets an empty {@link Metric} for testing purposes.
   *
   * @return the Metric.
   */
  @NonNull
  public static Metric getEmptyMetric() {
    return Metric.newBuilder().build();
  }

  /**
   * Gets a sample {@link Metric} for testing purposes.
   *
   * @return the Metric.
   */
  @NonNull
  public static Metric getSampleMetric() {
    final Metric.Builder metricBuilder = Metric.newBuilder();
    metricBuilder.addTimeseries(getSampleTimeSeries())
                 .setMetricDescriptor(getSampleMetricDescriptor());
    return metricBuilder.build();
  }

  /**
   * Gets a sample {@link Metric} for testing purposes, same as {@link #getSampleMetric()}, but
   * with different
   * {@link TimeSeries}.
   *
   * @return the Metric.
   */
  @NonNull
  public static Metric getSampleMetricWithOtherTimeSeries() {
    final Metric.Builder metricBuilder = Metric.newBuilder();
    metricBuilder.addTimeseries(getOtherSampleTimeSeries())
                 .setMetricDescriptor(getSampleMetricDescriptor());
    return metricBuilder.build();
  }

  /**
   * Gets a different sample {@link Metric} for testing purposes. This should be different from
   * {@link #getSampleMetric()}.
   *
   * @return the Metric.
   */
  @NonNull
  public static Metric getOtherMetric() {
    final Metric.Builder metricBuilder = Metric.newBuilder();
    metricBuilder.addTimeseries(getDifferentTimeSeries())
                 .setMetricDescriptor(getDifferentMetricDescriptor());
    return metricBuilder.build();
  }

  /**
   * Gets the merged result {@link Metric} of {@link #getSampleMetric()} and
   * {@link #getSampleMetricWithOtherTimeSeries()}.
   *
   * @return the Metric.
   */
  @NonNull
  public static Metric getMergedSampleMetric() {
    final Metric.Builder metricBuilder = Metric.newBuilder();
    metricBuilder.addTimeseries(getSampleTimeSeries())
                 .addTimeseries(getOtherSampleTimeSeries())
                 .setMetricDescriptor(getSampleMetricDescriptor());
    return metricBuilder.build();
  }

  /**
   * Gets a sample {@link TimeSeries} for {@link #getSampleMetricWithOtherTimeSeries()}.
   *
   * @return the TimeSeries.
   */
  @NonNull
  public static TimeSeries getOtherSampleTimeSeries() {
    final TimeSeries.Builder timeSeriesBuilder = TimeSeries.newBuilder();
    timeSeriesBuilder.addLabelValues(getLabelValue("First other label"))
                     .addLabelValues(getLabelValue("Second other label"))
                     .addPoints(getPoint(1, 78, 79))
                     .addPoints(getPoint(2, 87, 88));
    return timeSeriesBuilder.build();
  }

  /**
   * Gets a sample {@link TimeSeries} for {@link #getSampleMetric()}.
   *
   * @return the TimeSeries.
   */
  @NonNull
  public static TimeSeries getSampleTimeSeries() {
    final TimeSeries.Builder timeSeriesBuilder = TimeSeries.newBuilder();
    timeSeriesBuilder.addLabelValues(getLabelValue("Sample label"))
                     .addPoints(getPoint(1, 2, 3));
    return timeSeriesBuilder.build();
  }

  /**
   * Gets a sample {@link TimeSeries} for {@link #getOtherMetric()}.
   *
   * @return the TimeSeries.
   */
  @NonNull
  public static TimeSeries getDifferentTimeSeries() {
    final TimeSeries.Builder timeSeriesBuilder = TimeSeries.newBuilder();
    timeSeriesBuilder.addLabelValues(getLabelValue("Different label"))
                     .addPoints(getPoint(1, 55, 56));
    return timeSeriesBuilder.build();
  }

  /**
   * Gets a {@link LabelValue} for testing purposes.
   *
   * @param value the value of the Label.
   * @return the LabelValue.
   */
  @NonNull
  public static LabelValue getLabelValue(@NonNull final String value) {
    final LabelValue.Builder labelValueBuilder = LabelValue.newBuilder();
    labelValueBuilder.setValue(value);
    return labelValueBuilder.build();
  }

  /**
   * Gets a sample {@link Point} for testing purposes.
   *
   * @param int64value the Int64 value for the Point.
   * @param seconds    the seconds for the {@link Timestamp} of the Point.
   * @param nanos      the nanos for the {@link Timestamp} of the Point.
   * @return the Point.
   */
  @NonNull
  public static Point getPoint(final long int64value, final long seconds, final int nanos) {
    final Point.Builder pointBuilder = Point.newBuilder();
    pointBuilder.setInt64Value(int64value)
                .setTimestamp(DataTestUtils.getTimestamp(seconds, nanos));
    return pointBuilder.build();
  }

  /**
   * Gets a sample {@link MetricDescriptor} for testing purposes.
   *
   * @return the MetricDescriptor.
   */
  @NonNull
  public static MetricDescriptor getSampleMetricDescriptor() {
    final MetricDescriptor.Builder metricDescriptorBuilder = MetricDescriptor.newBuilder();
    metricDescriptorBuilder.setName("Sample name")
                           .setDescription("Sample Description")
                           .setType(MetricDescriptor.Type.GAUGE_INT64)
                           .addLabelKeys(getSampleLabelKey())
                           .setUnit("Sample Unit");
    return metricDescriptorBuilder.build();
  }

  /**
   * Gets a sample {@link LabelKey} for testing purposes.
   *
   * @return the LabelKey.
   */
  @NonNull
  public static LabelKey getSampleLabelKey() {
    final LabelKey.Builder labelKeyBuilder = LabelKey.newBuilder();
    labelKeyBuilder.setKey("some.sample.key").setDescription("Description value").build();
    return labelKeyBuilder.build();
  }

  /**
   * Gets a diffrent sample {@link MetricDescriptor} for testing purposes.
   *
   * @return the MetricDescriptor.
   */
  @NonNull
  public static MetricDescriptor getDifferentMetricDescriptor() {
    final MetricDescriptor.Builder metricDescriptorBuilder = MetricDescriptor.newBuilder();
    metricDescriptorBuilder.setName("Different name")
                           .setDescription("Different Description")
                           .setType(MetricDescriptor.Type.GAUGE_INT64)
                           .setUnit("Different Unit");
    return metricDescriptorBuilder.build();
  }

  /**
   * Creates an ApplicationStartUp Metric that a {@link ApplicationStartUpDataFormatter} would make.
   *
   * @return a Metric representing a ApplicationStartUp event.
   */
  @NonNull
  public static Metric getApplicationStartUpMetric() {
    return ApplicationStartUpDataFormatter.createApplicationStartUp(
        new ApplicationStartData(1234L, ApplicationStartType.COLD),
        Timestamp.newBuilder().setSeconds(1614687783L).setNanos(614000000).build());
  }

  /**
   * Creates an ApplicationCpuMetric that a {@link ApplicationCpuDataFormatter} would make.
   *
   * @return a Metric representing an application cpu event.
   */
  @NonNull
  public static Metric getApplicationCpuMetric() {
    return ApplicationCpuDataFormatter.createAppCpuMetric(21.5,
        Timestamp.newBuilder().setSeconds(1615384507).setNanos(681000000).build());
  }

  /**
   * Creates an SystemCpuMetric that a {@link SystemCpuDataFormatter} would make.
   *
   * @return a Metric representing a system cpu event.
   */
  @NonNull
  public static Metric getSystemCpuMetric() {
    final CpuUsageData.CpuStat cpuStat = new CpuUsageData.CpuStat(
        1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f
    );

    return SystemCpuDataFormatter.createSystemCpuMetric(cpuStat,
        Timestamp.newBuilder().setSeconds(1615394500).setNanos(981000000).build());
  }

  /**
   * Creates an ApplicationUsedMemoryMetric that a {@link ApplicationUsedMemoryDataFormatter}
   * would make.
   *
   * @return a Metric representing an application memory used event.
   */
  @NonNull
  public static Metric getApplicationMemoryUsage() {
    return MemoryDataFormatter.createMemoryMetric(10L,
        MemoryDataFormatter.MemoryType.APPLICATION,
        Timestamp.newBuilder().setSeconds(1615384500).setNanos(780000000).build());
  }

  /**
   * Creates an SystemMemoryMetric that a {@link SystemMemoryDataFormatter} would make.
   *
   * @return a Metric representing a system memory used event.
   */
  @NonNull
  public static Metric getSystemMemoryUsage() {
    return MemoryDataFormatter.createMemoryMetric(10L, MemoryDataFormatter.MemoryType.SYSTEM,
        Timestamp.newBuilder().setSeconds(1615384600).setNanos(881000000).build());
  }
}
