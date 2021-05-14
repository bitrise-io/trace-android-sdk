package io.bitrise.trace.network.adapters;

import static io.bitrise.trace.test.TraceTestProvider.getTruncatableString;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.opencensus.proto.trace.v1.AttributeValue;
import io.opencensus.proto.trace.v1.Span;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This adapter serializes and deserializes {@link Span.Attributes} type objects.
 * This is required because the current ProtoTypeAdapter cannot serialize the Attributes and ends
 * with a StackOverflowError. We also need to serialize the values as value, not string_value etc to
 * satisfy the contract with our current backend systems.
 */
public class SpanAttributeAdapter
    implements JsonSerializer<Span.Attributes>, JsonDeserializer<Span.Attributes> {

  private static final String PROPERTY_VALUE = "value";
  private static final String PROPERTY_VALUE_CASE = "value_case";
  private static final String PROPERTY_ATTRIBUTE_MAP = "attribute_map";

  @Override
  public Span.Attributes deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
    JsonObject srcAttributes = json.getAsJsonObject();
    JsonObject srcAttributeMap = srcAttributes.get(PROPERTY_ATTRIBUTE_MAP).getAsJsonObject();

    final Span.Attributes.Builder attributes = Span.Attributes.newBuilder();
    for (String key : srcAttributeMap.keySet()) {
      JsonObject srcAttribute = srcAttributeMap.get(key).getAsJsonObject()
                                               .get(PROPERTY_VALUE).getAsJsonObject();

      AttributeValue.ValueCase valueCase = AttributeValue.ValueCase.forNumber(
          srcAttribute.get(PROPERTY_VALUE_CASE).getAsInt());
      if (valueCase != null) {
        switch (valueCase) {
          case STRING_VALUE:
            attributes.putAttributeMap(
                key, AttributeValue.newBuilder()
                                   .setStringValue(getTruncatableString(
                                       srcAttribute.get(PROPERTY_VALUE).getAsString()))
                                   .build());

            break;
          case INT_VALUE:
            attributes.putAttributeMap(
                key, AttributeValue.newBuilder()
                                   .setIntValue(srcAttribute.get(PROPERTY_VALUE).getAsInt())
                                   .build());
            break;
          case BOOL_VALUE:
            attributes.putAttributeMap(
                key, AttributeValue.newBuilder()
                                   .setBoolValue(srcAttribute.get(PROPERTY_VALUE).getAsBoolean())
                                   .build());
            break;
          case DOUBLE_VALUE:
            attributes.putAttributeMap(
                key, AttributeValue.newBuilder()
                                   .setDoubleValue(srcAttribute.get(PROPERTY_VALUE).getAsDouble())
                                   .build());
            break;
          case VALUE_NOT_SET:
            // we deliberately do not deserialize values that were not set.
            break;
          default:
            // nop.
            break;
        }
      }
    }
    return attributes.build();
  }

  @Override
  public JsonElement serialize(Span.Attributes src, Type typeOfSrc,
                               JsonSerializationContext context) {
    final JsonObject attributes = new JsonObject();
    final JsonObject attributeMap = new JsonObject();

    final Map<String, AttributeValue> srcAttributeMap = src.getAttributeMapMap();
    for (String key : srcAttributeMap.keySet()) {
      AttributeValue srcAttribute = srcAttributeMap.get(key);
      JsonObject attribute = new JsonObject();
      JsonObject value = new JsonObject();

      if (srcAttribute != null) {
        value.addProperty(PROPERTY_VALUE_CASE, srcAttribute.getValueCase().getNumber());
        switch (srcAttribute.getValueCase()) {
          case STRING_VALUE:
            value.addProperty(PROPERTY_VALUE, srcAttribute.getStringValue().getValue());
            break;
          case INT_VALUE:
            value.addProperty(PROPERTY_VALUE, srcAttribute.getIntValue());
            break;
          case BOOL_VALUE:
            value.addProperty(PROPERTY_VALUE, srcAttribute.getBoolValue());
            break;
          case DOUBLE_VALUE:
            value.addProperty(PROPERTY_VALUE, srcAttribute.getDoubleValue());
            break;
          case VALUE_NOT_SET:
            // we deliberately do not serialize values that are not set.
            break;
          default:
            // nop.
            break;
        }
      }

      attribute.add(PROPERTY_VALUE, value);
      attributeMap.add(key, attribute);
    }

    attributes.add(PROPERTY_ATTRIBUTE_MAP, attributeMap);
    return attributes;
  }
}
