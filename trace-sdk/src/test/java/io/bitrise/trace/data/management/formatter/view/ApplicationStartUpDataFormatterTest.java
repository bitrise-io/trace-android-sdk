package io.bitrise.trace.data.management.formatter.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.dto.ApplicationStartData;
import io.bitrise.trace.data.dto.ApplicationStartType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;
import org.junit.Test;

/**
 * Unit tests for {@link ApplicationStartUpDataFormatter}.
 */
public class ApplicationStartUpDataFormatterTest extends BaseDataFormatterTest {

  final ApplicationStartUpDataFormatter formatter = new ApplicationStartUpDataFormatter();

  @Test
  public void formatData_notApplicationStartUp() {
    final Data data = new Data(ApplicationStartUpDataListener.class);
    data.setContent("kittens");
    assertArrayEquals(new FormattedData[] {}, formatter.formatData(data));
  }

  @Test
  public void formatData() {
    final ApplicationStartData applicationStartData =
        new ApplicationStartData(1234, ApplicationStartType.COLD);

    final Data data = new Data(ApplicationStartUpDataListener.class);
    data.setContent(applicationStartData);
    final FormattedData[] formattedData = formatter.formatData(data);
    assertEquals(1, formattedData.length);

    final Timestamp timestamp = formattedData[0].getMetricEntity().getMetric()
                                                .getTimeseries(0).getPoints(0).getTimestamp();

    assertEquals(createExpectedMetric(applicationStartData, timestamp),
        formattedData[0].getMetricEntity().getMetric());
  }

  private Metric createExpectedMetric(@NonNull final ApplicationStartData applicationStartData,
                                      @NonNull final Timestamp timestamp) {

    final MetricDescriptor expectedMetricDescriptor =
        MetricDescriptor.newBuilder()
                        .setName("app.startup.latency.ms")
                        .setDescription("App startup latency in milliseconds")
                        .setUnit("ms")
                        .setType(MetricDescriptor.Type.GAUGE_INT64)
                        .addLabelKeys(LabelKey.newBuilder().setKey("app.start.state").build())
                        .build();

    final TimeSeries expectedTimeSeries =
        TimeSeries.newBuilder()
                  .addLabelValues(
                      LabelValue.newBuilder()
                                .setValue(applicationStartData
                                    .getApplicationStartType()
                                    .getName())
                                .build())
                  .addPoints(
                      Point.newBuilder()
                           .setTimestamp(timestamp)
                           .setDoubleValue(
                               applicationStartData.getDuration())
                           .build())
                  .build();

    return Metric.newBuilder()
                 .setMetricDescriptor(expectedMetricDescriptor)
                 .addTimeseries(expectedTimeSeries).build();

  }
}
