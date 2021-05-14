package io.bitrise.trace.data.management.formatter.view;

import static io.bitrise.trace.data.dto.DataValues.app;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.latency;
import static io.bitrise.trace.data.dto.DataValues.ms;
import static io.bitrise.trace.data.dto.DataValues.start;
import static io.bitrise.trace.data.dto.DataValues.startup;
import static io.bitrise.trace.data.dto.DataValues.state;

import androidx.annotation.NonNull;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.dto.ApplicationStartData;
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

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#APP_START}.
 */
public class ApplicationStartUpDataFormatter extends DataFormatter {

  /**
   * Gets the {@link LabelKey} for the startup {@link Metric}.
   *
   * @return the LabelKey.
   */
  @NonNull
  private static LabelKey getLabelKey() {
    return LabelKey.newBuilder()
                   .setKey(getName(app, start, state))
                   .build();
  }

  /**
   * Gets the {@link MetricDescriptor} for the startup {@link Metric}.
   *
   * @return the MetricDescriptor.
   */
  @NonNull
  private static MetricDescriptor getMetricDescriptor() {
    return MetricDescriptor.newBuilder()
                           .setName(getName(app, startup, latency, ms))
                           .setDescription("App startup latency in milliseconds")
                           .setUnit(ms)
                           .setType(MetricDescriptor.Type.GAUGE_INT64)
                           .addLabelKeys(getLabelKey())
                           .build();
  }

  /**
   * Creates an ApplicationStartUp Metric.
   *
   * @param applicationStartData the data created from the {@link ApplicationStartUpDataListener}.
   * @param timestamp            the time stamp for when the data was captured.
   * @return the metric representing the application start up.
   */
  @NonNull
  public static Metric createApplicationStartUp(
      @NonNull final ApplicationStartData applicationStartData,
      @NonNull final Timestamp timestamp) {
    final TimeSeries timeSeries = TimeSeries.newBuilder()
                                            .addLabelValues(
                                                LabelValue.newBuilder()
                                                          .setValue(applicationStartData
                                                              .getApplicationStartType().getName())
                                                          .build())
                                            .addPoints(
                                                Point.newBuilder()
                                                     .setTimestamp(timestamp)
                                                     .setDoubleValue(
                                                         applicationStartData.getDuration())
                                                     .build())
                                            .build();

    return Metric.newBuilder()
                 .setMetricDescriptor(getMetricDescriptor())
                 .addTimeseries(timeSeries).build();
  }

  @NonNull
  @Override
  public FormattedData[] formatData(@NonNull final Data data) {
    if (!(data.getContent() instanceof ApplicationStartData)) {
      return new FormattedData[] {};
    }
    final ApplicationStartData applicationStartData = (ApplicationStartData) data.getContent();

    return new FormattedData[] {new FormattedData(createStartUpMetric(applicationStartData))};
  }

  /**
   * Creates a {@link Metric} from the given {@link ApplicationStartData}.
   *
   * @param applicationStartData the given {@link ApplicationStartData}.
   * @return the Metric.
   */
  @NonNull
  private Metric createStartUpMetric(@NonNull final ApplicationStartData applicationStartData) {
    return createApplicationStartUp(applicationStartData, getTimestamp());
  }
}
