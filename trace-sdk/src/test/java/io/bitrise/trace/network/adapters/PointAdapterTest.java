package io.bitrise.trace.network.adapters;

import static org.junit.Assert.assertEquals;

import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.DistributionValue;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.SummaryValue;
import org.junit.Test;

/**
 * Tests for {@link PointAdapter}.
 */
public class PointAdapterTest {

  private final String jsonIntPoint =
      "{\"value\":1234,\"value_case\":2,\"timestamp\":{\"seconds\":100,\"nanos\":200}}";
  private final String jsonDoublePoint =
      "{\"value\":56.78,\"value_case\":3,\"timestamp\":{\"seconds\":300,\"nanos\":400}}";
  private final String jsonDistributionValuePoint =
      "{\"value\":{},\"value_case\":4,\"timestamp\":{\"seconds\":500,\"nanos\":600}}";
  private final String jsonSummaryValuePoint =
      "{\"value\":{},\"value_case\":5,\"timestamp\":{\"seconds\":700,\"nanos\":800}}";

  private Point getIntPoint() {
    return Point.newBuilder()
                .setInt64Value(1234)
                .setTimestamp(Timestamp.newBuilder().setSeconds(100).setNanos(200).build())
                .build();
  }

  private Point getDoublePoint() {
    return Point.newBuilder()
                .setDoubleValue(56.78)
                .setTimestamp(Timestamp.newBuilder().setSeconds(300).setNanos(400).build())
                .build();
  }

  private Point getDistributionValuePoint() {
    return Point.newBuilder()
                .setDistributionValue(DistributionValue.newBuilder().build())
                .setTimestamp(Timestamp.newBuilder().setSeconds(500).setNanos(600).build())
                .build();
  }

  private Point getSummaryValuePoint() {
    return Point.newBuilder()
                .setSummaryValue(SummaryValue.newBuilder().build())
                .setTimestamp(Timestamp.newBuilder().setSeconds(700).setNanos(800).build())
                .build();
  }

  @Test
  public void serialize_intValue() {
    final String json = NetworkClient.getGson().toJson(getIntPoint());
    assertEquals(jsonIntPoint, json);
  }

  @Test
  public void deserialize_intValue() {
    final Point point = NetworkClient.getGson().fromJson(jsonIntPoint, Point.class);
    assertEquals(getIntPoint(), point);
  }

  @Test
  public void serialize_doubleValue() {
    final String json = NetworkClient.getGson().toJson(getDoublePoint());
    assertEquals(jsonDoublePoint, json);
  }

  @Test
  public void deserialize_doubleValue() {
    final Point point = NetworkClient.getGson().fromJson(jsonDoublePoint, Point.class);
    assertEquals(getDoublePoint(), point);
  }

  @Test
  public void serialize_distributionValue() {
    final String json = NetworkClient.getGson().toJson(getDistributionValuePoint());
    assertEquals(jsonDistributionValuePoint, json);
  }

  @Test
  public void deserialize_distributionValue() {
    final Point point = NetworkClient.getGson().fromJson(jsonDistributionValuePoint, Point.class);
    assertEquals(getDistributionValuePoint(), point);
  }

  @Test
  public void serialize_summaryValue() {
    final String json = NetworkClient.getGson().toJson(getSummaryValuePoint());
    assertEquals(jsonSummaryValuePoint, json);
  }

  @Test
  public void deserialize_summaryValue() {
    final Point point = NetworkClient.getGson().fromJson(jsonSummaryValuePoint, Point.class);
    assertEquals(getSummaryValuePoint(), point);
  }
}
