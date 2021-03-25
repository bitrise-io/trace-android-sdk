package io.bitrise.trace.network.adapters;

import org.junit.Test;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.resource.v1.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link ResourceAdapter}.
 */
public class ResourceAdapterTest {

    private final String jsonResource = "{\"labels\":{\"key\":\"value\"},\"type\":\"mobile\"}";

    @Test
    public void serialize_withLabel() {
        final Resource.Builder resourceBuilder = Resource.newBuilder();
        resourceBuilder.setType("mobile");
        resourceBuilder.putLabels("key", "value");
        final Resource resource = resourceBuilder.build();

        final String json = NetworkClient.getGson().toJson(resource);
        assertEquals(jsonResource, json);
    }

    @Test
    public void serialize_withNoLabel() {
        final Resource.Builder resourceBuilder = Resource.newBuilder();
        resourceBuilder.setType("mobile");
        final Resource resource = resourceBuilder.build();

        final String json = NetworkClient.getGson().toJson(resource);
        final String jsonResourceNoLabels = "{\"labels\":{},\"type\":\"mobile\"}";
        assertEquals(jsonResourceNoLabels, json);
    }

    @Test
    public void deserialize() {
        final Resource resource = NetworkClient.getGson().fromJson(jsonResource, Resource.class);
        assertNull(resource);
    }
}
