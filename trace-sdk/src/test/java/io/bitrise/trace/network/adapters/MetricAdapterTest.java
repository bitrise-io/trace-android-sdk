package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.Metric;
import org.junit.Test;

/**
 * Unit tests for {@link MetricAdapter}.
 */
public class MetricAdapterTest {

  private static final String jsonMetric = "{\"metric_descriptor\":{\"name\":\"name\","
      + "\"description\":\"description\",\"unit\":\"unit\",\"type\":1,"
      + "\"label_keys\":[{\"key\":\"key1\"},{\"key\":\"key2\"}]},"
      + "\"timeseries\":[{\"label_values\":[{\"value\":\"label1\"},{\"value\":\"label2\"}],"
      + "\"points\":[{\"value\":123,\"value_case\":2,\"timestamp\":{\"seconds\":12345,"
      + "\"nanos\":678}},{\"value\":456,\"value_case\":2,\"timestamp\":{\"seconds\":23456,"
      + "\"nanos\":789}}]}]}";
  private static final String jsonEmptyMetric = "{\"metric_descriptor\":{\"name\":\"name\","
      + "\"description\":\"description\",\"unit\":\"unit\",\"type\":1,"
      + "\"label_keys\":[{\"key\":\"key1\"},{\"key\":\"key2\"}]}}";

  private Metric getMetric() {
    return Metric.newBuilder()
                 .setMetricDescriptor(MetricDescriptorAdapterTest.getMetricDescriptor())
                 .addTimeseries(TimeSeriesAdapterTest.getTimeSeries())
                 .build();
  }

  private Metric getEmptyMetric() {
    return Metric.newBuilder()
                 .setMetricDescriptor(MetricDescriptorAdapterTest.getMetricDescriptor())
                 .build();
  }

  @Test
  public void deserialize() {
    final Metric metric = NetworkClient.getGson()
        .fromJson(jsonMetric, Metric.class);
    assertEquals(getMetric(), metric);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getMetric());
    assertEquals(jsonMetric, json);
  }

  @Test
  public void deserialize_emptyMetric() {
    final Metric metric = NetworkClient.getGson()
        .fromJson(jsonEmptyMetric, Metric.class);
    assertEquals(getEmptyMetric(), metric);
  }

  @Test
  public void serialize_emptyMetric() {
    final String json = NetworkClient.getGson().toJson(getEmptyMetric());
    assertEquals(jsonEmptyMetric, json);
  }
}
