package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import org.junit.Test;

/**
 * Unit tests for {@link TimestampAdapter}.
 */
public class TimestampAdapterTest {

  private static final String jsonTimestamp = "{\"seconds\":12345,\"nanos\":6789}";

  private Timestamp getTimestamp() {
    return Timestamp.newBuilder()
                    .setSeconds(12345L)
                    .setNanos(6789)
                    .build();
  }

  @Test
  public void deserialize() {
    final Timestamp timestamp = NetworkClient.getGson()
        .fromJson(jsonTimestamp, Timestamp.class);
    assertEquals(getTimestamp(), timestamp);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getTimestamp());
    assertEquals(jsonTimestamp, json);
  }
}
