package io.bitrise.trace.network.adapters;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.resource.v1.Resource;
import org.junit.Test;

/**
 * Tests for {@link ResourceAdapter}.
 */
public class ResourceAdapterTest {

  private final String jsonResource = "{\"labels\":{\"key\":\"value\"},\"type\":\"mobile\"}";
  final String jsonResourceNoLabels = "{\"labels\":{},\"type\":\"\"}";

  private Resource createResourceWithLabel() {
    final Resource.Builder resourceBuilder = Resource.newBuilder();
    resourceBuilder.setType("mobile");
    resourceBuilder.putLabels("key", "value");
    return resourceBuilder.build();
  }

  private Resource createResourceNoContent() {
    final Resource.Builder resourceBuilder = Resource.newBuilder();
    return resourceBuilder.build();
  }

  @Test
  public void serialize_withLabel() {
    final String json = NetworkClient.getGson().toJson(createResourceWithLabel());
    assertEquals(jsonResource, json);
  }

  @Test
  public void serialize_noContent() {
    final String json = NetworkClient.getGson().toJson(createResourceNoContent());
    assertEquals(jsonResourceNoLabels, json);
  }

  @Test
  public void deserialize_withLabel() {
    final Resource resource = NetworkClient.getGson().fromJson(jsonResource, Resource.class);
    assertEquals(createResourceWithLabel(), resource);
  }

  @Test
  public void deserialize_noContent() {
    final Resource resource = NetworkClient.getGson().fromJson(jsonResourceNoLabels, Resource.class);
    assertEquals(createResourceNoContent(), resource);
  }

}
