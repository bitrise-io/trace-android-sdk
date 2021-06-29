package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import com.google.protobuf.ByteString;
import io.bitrise.trace.network.NetworkClient;
import io.bitrise.trace.utils.ByteStringConverter;
import org.junit.Test;

/**
 * Unit tests for {@link ByteStringAdapter}.
 */
public class ByteStringAdapterTest {

  private static final String jsonByteString = "\"hello world\"";

  private ByteString getByteString() {
    return ByteStringConverter.toByteString("hello world");
  }

  @Test
  public void deserialize() {
    final ByteString byteString = NetworkClient.getGson()
                                               .fromJson(jsonByteString, ByteString.class);
    assertEquals(getByteString(), byteString);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getByteString());
    assertEquals(jsonByteString, json);
  }
}
