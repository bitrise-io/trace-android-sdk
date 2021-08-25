package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.trace.v1.TruncatableString;
import org.junit.Test;

/**
 * Unit tests for {@link MetricDescriptorAdapter}.
 */
public class MetricDescriptorAdapterTest {

  private static final String jsonMetricDescriptor = "{\"name\":\"name\","
      + "\"description\":\"description\",\"unit\":\"unit\",\"type\":1,"
      + "\"label_keys\":[{\"key\":\"key1\"},{\"key\":\"key2\"}]}";
  private static final String jsonEmptyMetricDescriptor = "{\"name\":\"name\","
      + "\"description\":\"description\",\"unit\":\"unit\",\"type\":1}";

  /**
   * Creates a test {@link MetricDescriptor} object.
   *
   * @return the test {@link MetricDescriptor} object.
   */
  public static MetricDescriptor getMetricDescriptor() {
    return MetricDescriptor.newBuilder()
                           .setName("name")
                           .setDescription("description")
                           .setUnit("unit")
                           .setTypeValue(1)
                           .addLabelKeys(LabelKey.newBuilder().setKey("key1").build())
                           .addLabelKeys(LabelKey.newBuilder().setKey("key2").build())
                           .build();
  }

  private MetricDescriptor getEmptyMetricDescriptor() {
    return MetricDescriptor.newBuilder()
                           .setName("name")
                           .setDescription("description")
                           .setUnit("unit")
                           .setTypeValue(1)
                           .build();
  }

  @Test
  public void deserialize() {
    final MetricDescriptor metricDescriptor = NetworkClient.getGson()
        .fromJson(jsonMetricDescriptor, MetricDescriptor.class);
    assertEquals(getMetricDescriptor(), metricDescriptor);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getMetricDescriptor());
    assertEquals(jsonMetricDescriptor, json);
  }

  @Test
  public void deserialize_emptyMetric() {
    final MetricDescriptor metricDescriptor = NetworkClient.getGson()
        .fromJson(jsonEmptyMetricDescriptor, MetricDescriptor.class);
    assertEquals(getEmptyMetricDescriptor(), metricDescriptor);
  }

  @Test
  public void serialize_emptyMetric() {
    final String json = NetworkClient.getGson().toJson(getEmptyMetricDescriptor());
    assertEquals(jsonEmptyMetricDescriptor, json);
  }
}
