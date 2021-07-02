package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;
import org.junit.Test;

/**
 * Unit tests for {@link TimeSeriesAdapter}.
 */
public class TimeSeriesAdapterTest {

  private static final String jsonTimeSeries = "{\"label_values\":[{\"value\":\"label1\"},"
      + "{\"value\":\"label2\"}],\"points\":[{\"value\":123,\"value_case\":2,"
      + "\"timestamp\":{\"seconds\":12345,\"nanos\":678}},{\"value\":456,\"value_case\":2,"
      + "\"timestamp\":{\"seconds\":23456,\"nanos\":789}}]}";
  private static final String jsonEmptyTimeSeries = "{}";

  /**
   * Creates a test {@link TimeSeries} object.
   *
   * @return the {@link TimeSeries} object.
   */
  public static TimeSeries getTimeSeries() {
    final Timestamp timestamp1 = Timestamp.newBuilder().setSeconds(12345L).setNanos(678).build();
    final Timestamp timestamp2 = Timestamp.newBuilder().setSeconds(23456L).setNanos(789).build();
    final Point point1 = Point.newBuilder().setInt64Value(123L).setTimestamp(timestamp1).build();
    final Point point2 = Point.newBuilder().setInt64Value(456L).setTimestamp(timestamp2).build();
    return TimeSeries.newBuilder()
                     .addLabelValues(LabelValue.newBuilder().setValue("label1"))
                     .addLabelValues(LabelValue.newBuilder().setValue("label2"))
                     .addPoints(point1)
                     .addPoints(point2)
                     .build();
  }

  private TimeSeries getEmptyTimeSeries() {
    return TimeSeries.newBuilder().build();
  }

  @Test
  public void deserialize() {
    final TimeSeries timeSeries = NetworkClient.getGson()
        .fromJson(jsonTimeSeries, TimeSeries.class);
    assertEquals(getTimeSeries(), timeSeries);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getTimeSeries());
    assertEquals(jsonTimeSeries, json);
  }

  @Test
  public void deserialize_emptyTimeSeries() {
    final TimeSeries timeSeries = NetworkClient.getGson()
        .fromJson(jsonEmptyTimeSeries, TimeSeries.class);
    assertEquals(getEmptyTimeSeries(), timeSeries);
  }

  @Test
  public void serialize_emptyTimeSeries() {
    final String json = NetworkClient.getGson().toJson(getEmptyTimeSeries());
    assertEquals(jsonEmptyTimeSeries, json);
  }
}
