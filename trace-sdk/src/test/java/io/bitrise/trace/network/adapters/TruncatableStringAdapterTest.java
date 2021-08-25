package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.trace.v1.TruncatableString;
import org.junit.Test;

/**
 * Unit tests for {@link TruncatableStringAdapter}.
 */
public class TruncatableStringAdapterTest {

  private static final String jsonTruncatableString = "{\"value\":\"hello world\"}";

  private TruncatableString getTruncatableString() {
    return TruncatableString.newBuilder()
                            .setValue("hello world")
                            .build();
  }

  @Test
  public void deserialize() {
    final TruncatableString truncatableString = NetworkClient.getGson()
        .fromJson(jsonTruncatableString, TruncatableString.class);
    assertEquals(getTruncatableString(), truncatableString);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getTruncatableString());
    assertEquals(jsonTruncatableString, json);
  }
}
