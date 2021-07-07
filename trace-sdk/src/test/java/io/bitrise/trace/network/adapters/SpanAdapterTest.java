package io.bitrise.trace.network.adapters;

import static junit.framework.TestCase.assertEquals;

import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import io.bitrise.trace.utils.ByteStringConverter;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;
import org.junit.Test;

/**
 * Unit tests for {@link SpanAdapter}.
 */
public class SpanAdapterTest {

  private static final String jsonSpan = "{\"trace_id\":\"trace id\",\"span_id\":\"span id\","
      + "\"parent_span_id\":\"parent span id\",\"name\":{\"value\":\"name\"},"
      + "\"start_time\":{\"seconds\":12345,\"nanos\":678},\"end_time\":{\"seconds\":23456,"
      + "\"nanos\":789},\"attributes\":{\"attribute_map\":{\"http"
      + ".method\":{\"value\":{\"value_case\":1,\"value\":\"POST\"}}}},\"kind\":2}";
  private static final String jsonEmptySpan = "{\"trace_id\":\"trace id\",\"span_id\":\"span "
      + "id\",\"name\":{\"value\":\"name\"},\"start_time\":{\"seconds\":12345,\"nanos\":678},"
      + "\"end_time\":{\"seconds\":23456,\"nanos\":789},\"kind\":2}";

  private Span getSpan() {
    final Timestamp timestamp1 = Timestamp.newBuilder().setSeconds(12345L).setNanos(678).build();
    final Timestamp timestamp2 = Timestamp.newBuilder().setSeconds(23456L).setNanos(789).build();
    return Span.newBuilder()
               .setTraceId(ByteStringConverter.toByteString("trace id"))
               .setSpanId(ByteStringConverter.toByteString("span id"))
               .setName(TruncatableString.newBuilder().setValue("name"))
               .setParentSpanId(ByteStringConverter.toByteString("parent span id"))
               .setStartTime(timestamp1)
               .setEndTime(timestamp2)
               .setAttributes(SpanAttributeAdapterTest.getAttributeString())
               .setKindValue(2)
               .build();
  }

  private Span getEmptySpan() {
    final Timestamp timestamp1 = Timestamp.newBuilder().setSeconds(12345L).setNanos(678).build();
    final Timestamp timestamp2 = Timestamp.newBuilder().setSeconds(23456L).setNanos(789).build();
    return Span.newBuilder()
               .setTraceId(ByteStringConverter.toByteString("trace id"))
               .setSpanId(ByteStringConverter.toByteString("span id"))
               .setName(TruncatableString.newBuilder().setValue("name"))
               .setStartTime(timestamp1)
               .setEndTime(timestamp2)
               .setKindValue(2)
               .build();
  }

  @Test
  public void deserialize() {
    final Span span = NetworkClient.getGson()
        .fromJson(jsonSpan, Span.class);
    assertEquals(getSpan(), span);
  }

  @Test
  public void serialize() {
    final String json = NetworkClient.getGson().toJson(getSpan());
    assertEquals(jsonSpan, json);
  }

  @Test
  public void deserialize_emptySpan() {
    final Span span = NetworkClient.getGson()
        .fromJson(jsonEmptySpan, Span.class);
    assertEquals(getEmptySpan(), span);
  }

  @Test
  public void serialize_emptySpan() {
    final String json = NetworkClient.getGson().toJson(getEmptySpan());
    assertEquals(jsonEmptySpan, json);
  }
}
