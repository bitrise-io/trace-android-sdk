package io.bitrise.trace.network.adapters;

import static io.bitrise.trace.test.TraceTestProvider.getTruncatableString;
import static org.junit.Assert.assertEquals;

import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.trace.v1.AttributeValue;
import io.opencensus.proto.trace.v1.Span;
import org.junit.Test;

/**
 * Tests for {@link SpanAttributeAdapter}.
 */
public class SpanAttributeAdapterTest {

  private final String jsonAttributeString =
      "{\"attribute_map\":{\"http.method\":{\"value\":{\"value_case\":1,\"value\":\"POST\"}}}}";
  private final String jsonAttributeInt =
      "{\"attribute_map\":{\"http.code\":{\"value\":{\"value_case\":2,\"value\":202}}}}";
  private final String jsonAttributeDouble =
      "{\"attribute_map\":{\"file.size\":{\"value\":{\"value_case\":4,\"value\":11.4}}}}";
  private final String jsonAttributeBoolean =
      "{\"attribute_map\":{\"success\":{\"value\":{\"value_case\":3,\"value\":false}}}}";
  private final String jsonAttributeUnknown =
      "{\"attribute_map\":{\"key\":{\"value\":{\"value_case\":9,\"value\":false}}}}";
  private final String jsonAttributeValueNotSet =
      "{\"attribute_map\":{\"success\":{\"value\":{\"value_case\":0}}}}";

  private Span.Attributes getAttributeString() {
    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    attributes.putAttributeMap(
        "http.method", AttributeValue.newBuilder()
                                     .setStringValue(getTruncatableString("POST"))
                                     .build());
    return attributes.build();
  }

  private Span.Attributes getAttributeInt() {
    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    attributes.putAttributeMap(
        "http.code", AttributeValue.newBuilder()
                                   .setIntValue(202)
                                   .build());
    return attributes.build();
  }

  private Span.Attributes getAttributeDouble() {
    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    attributes.putAttributeMap(
        "file.size", AttributeValue.newBuilder()
                                   .setDoubleValue(11.4)
                                   .build());
    return attributes.build();
  }

  private Span.Attributes getAttributeBoolean() {
    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    attributes.putAttributeMap(
        "success", AttributeValue.newBuilder()
                                 .setBoolValue(false)
                                 .build());
    return attributes.build();
  }

  private Span.Attributes getAttributeValueNotSet() {
    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    attributes.putAttributeMap(
        "success", AttributeValue.newBuilder()
                                 .build());
    return attributes.build();
  }

  @Test
  public void serialize_stringAttribute() {
    String json = NetworkClient.getGson().toJson(getAttributeString());
    assertEquals(jsonAttributeString, json);
  }

  @Test
  public void deserialize_stringAttribute() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeString, Span.Attributes.class);
    assertEquals(getAttributeString(), attributes);
  }

  @Test
  public void serialize_intAttribute() {
    String json = NetworkClient.getGson().toJson(getAttributeInt());
    assertEquals(jsonAttributeInt, json);
  }

  @Test
  public void deserialize_intAttribute() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeInt, Span.Attributes.class);
    assertEquals(getAttributeInt(), attributes);
  }

  @Test
  public void serialize_doubleAttribute() {
    String json = NetworkClient.getGson().toJson(getAttributeDouble());
    assertEquals(jsonAttributeDouble, json);
  }

  @Test
  public void deserialize_doubleAttribute() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeDouble, Span.Attributes.class);
    assertEquals(getAttributeDouble(), attributes);
  }

  @Test
  public void serialize_booleanAttribute() {
    String json = NetworkClient.getGson().toJson(getAttributeBoolean());
    assertEquals(jsonAttributeBoolean, json);
  }

  @Test
  public void deserialize_booleanAttribute() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeBoolean,
                                                  Span.Attributes.class);
    assertEquals(getAttributeBoolean(), attributes);
  }

  @Test
  public void serialize_valueNotSetAttribute() {
    String json = NetworkClient.getGson().toJson(getAttributeValueNotSet());
    assertEquals(jsonAttributeValueNotSet, json);
  }

  @Test
  public void deserialize_valueNotSetAttribute() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeValueNotSet,
                                                  Span.Attributes.class);
    final Span.Attributes expectedAttributes = Span.Attributes.newBuilder().build();
    assertEquals(expectedAttributes, attributes);
  }

  @Test
  public void deserialize_unknownType() {
    Span.Attributes attributes = NetworkClient.getGson()
                                              .fromJson(jsonAttributeUnknown,
                                                  Span.Attributes.class);
    final Span.Attributes expectedAttributes = Span.Attributes.newBuilder().build();
    assertEquals(expectedAttributes, attributes);
  }

}
