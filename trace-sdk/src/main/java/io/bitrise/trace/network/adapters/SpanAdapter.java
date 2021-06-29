package io.bitrise.trace.network.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;
import java.lang.reflect.Type;

public class SpanAdapter implements JsonSerializer<Span>, JsonDeserializer<Span> {

  private static final String PROPERTY_TRACE_ID = "trace_id";
  private static final String PROPERTY_SPAN_ID = "span_id";
  private static final String PROPERTY_PARENT_SPAN_ID = "parent_span_id";
  private static final String PROPERTY_NAME = "name";
  private static final String PROPERTY_START_TIME = "start_time";
  private static final String PROPERTY_END_TIME = "end_time";
  private static final String PROPERTY_ATTRIBUTES = "attributes";
  private static final String PROPERTY_KIND = "kind";


  @Override
  public Span deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcSpan = json.getAsJsonObject();
    final Span.Builder builder = Span.newBuilder();

    builder.setTraceId(NetworkClient.getGson().fromJson(
        srcSpan.get(PROPERTY_TRACE_ID), ByteString.class));
    builder.setSpanId(NetworkClient.getGson().fromJson(
        srcSpan.get(PROPERTY_SPAN_ID), ByteString.class));
    builder.setName(NetworkClient.getGson().fromJson(
        srcSpan.get(PROPERTY_NAME), TruncatableString.class));

    if (srcSpan.has(PROPERTY_PARENT_SPAN_ID)) {
      builder.setParentSpanId(NetworkClient.getGson().fromJson(
          srcSpan.get(PROPERTY_PARENT_SPAN_ID), ByteString.class));
    }

    builder.setStartTime(NetworkClient.getGson().fromJson(
        srcSpan.get(PROPERTY_START_TIME), Timestamp.class));
    builder.setEndTime(NetworkClient.getGson().fromJson(
        srcSpan.get(PROPERTY_END_TIME), Timestamp.class));

    if (srcSpan.has(PROPERTY_ATTRIBUTES)) {
      builder.setAttributes(NetworkClient.getGson().fromJson(
          srcSpan.get(PROPERTY_ATTRIBUTES), Span.Attributes.class));
    }

    builder.setKindValue(srcSpan.get(PROPERTY_KIND).getAsInt());

    return builder.build();
  }

  @Override
  public JsonElement serialize(Span src, Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();
    final Gson gson = NetworkClient.getGson();
    jsonObject.add(PROPERTY_TRACE_ID, gson.toJsonTree(src.getTraceId()));
    jsonObject.add(PROPERTY_SPAN_ID, gson.toJsonTree(src.getSpanId()));

    if (!src.getParentSpanId().isEmpty()) {
      jsonObject.add(PROPERTY_PARENT_SPAN_ID, gson.toJsonTree(src.getParentSpanId()));
    }

    jsonObject.add(PROPERTY_NAME, gson.toJsonTree(src.getName()));
    jsonObject.add(PROPERTY_START_TIME, gson.toJsonTree(src.getStartTime()));
    jsonObject.add(PROPERTY_END_TIME, gson.toJsonTree(src.getEndTime()));

    if (src.getAttributes().getAttributeMapCount() > 0) {
      jsonObject.add(PROPERTY_ATTRIBUTES, NetworkClient.getGson().toJsonTree(src.getAttributes()));
    }

    jsonObject.addProperty(PROPERTY_KIND, src.getKindValue());

    return jsonObject;
  }
}
