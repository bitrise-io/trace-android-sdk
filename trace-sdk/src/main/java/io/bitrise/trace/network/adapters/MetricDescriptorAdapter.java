package io.bitrise.trace.network.adapters;

import androidx.annotation.NonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link MetricDescriptor} objects.
 */
public class MetricDescriptorAdapter implements JsonSerializer<MetricDescriptor>,
    JsonDeserializer<MetricDescriptor> {

  private static final String PROPERTY_NAME = "name";
  private static final String PROPERTY_DESCRIPTION = "description";
  private static final String PROPERTY_UNIT = "unit";
  private static final String PROPERTY_TYPE = "type";
  private static final String PROPERTY_LABEL_KEYS = "label_keys";
  private static final String PROPERTY_KEY = "key";

  @Override
  public MetricDescriptor deserialize(@NonNull final JsonElement json,
                                      @NonNull final Type typeOfT,
                                      @NonNull final JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcMetric = json.getAsJsonObject();

    final MetricDescriptor.Builder builder = MetricDescriptor.newBuilder();
    builder.setName(srcMetric.get(PROPERTY_NAME).getAsString());
    builder.setDescription(srcMetric.get(PROPERTY_DESCRIPTION).getAsString());
    builder.setUnit(srcMetric.get(PROPERTY_UNIT).getAsString());
    builder.setTypeValue(srcMetric.get(PROPERTY_TYPE).getAsInt());

    if (srcMetric.get(PROPERTY_LABEL_KEYS).isJsonArray()) {
      final JsonArray srcLabels = srcMetric.get(PROPERTY_LABEL_KEYS).getAsJsonArray();

      for (JsonElement jsonElement : srcLabels) {
        final JsonObject jsonKey = jsonElement.getAsJsonObject();
        final LabelKey.Builder labelKeyBuilder = LabelKey.newBuilder();
        labelKeyBuilder.setKey(jsonKey.get(PROPERTY_KEY).getAsString());
        builder.addLabelKeys(labelKeyBuilder);
      }
    }

    return builder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final MetricDescriptor src,
                               @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {

    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty(PROPERTY_NAME, src.getName());
    jsonObject.addProperty(PROPERTY_DESCRIPTION, src.getDescription());
    jsonObject.addProperty(PROPERTY_UNIT, src.getUnit());
    jsonObject.addProperty(PROPERTY_TYPE, src.getTypeValue());

    if (src.getLabelKeysCount() > 0) {
      final JsonArray jsonKeys = new JsonArray();
      for (LabelKey key : src.getLabelKeysList()) {
        final JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty(PROPERTY_KEY, key.getKey());
        jsonKeys.add(jsonKey);
      }
      jsonObject.add(PROPERTY_LABEL_KEYS, jsonKeys);
    }

    return jsonObject;
  }
}
