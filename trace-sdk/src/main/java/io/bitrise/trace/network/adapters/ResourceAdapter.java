package io.bitrise.trace.network.adapters;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.opencensus.proto.resource.v1.Resource;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeSet;

/**
 * Adapter for {@link Resource} objects.
 */
public class ResourceAdapter implements JsonSerializer<Resource>, JsonDeserializer<Resource> {

  private static final String PROPERTY_LABELS = "labels";
  private static final String PROPERTY_TYPE = "type";

  @Override
  public Resource deserialize(@NonNull final JsonElement json, @NonNull final Type typeOfT,
                              @NonNull final JsonDeserializationContext context)
      throws JsonParseException {

    final JsonObject srcResource = json.getAsJsonObject();
    final Resource.Builder resource = Resource.newBuilder();

    if (srcResource.has(PROPERTY_TYPE)) {
      resource.setType(srcResource.get(PROPERTY_TYPE).getAsString());
    }

    if (srcResource.has(PROPERTY_LABELS)) {
      final JsonObject srcLabels = srcResource.get(PROPERTY_LABELS).getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : srcLabels.entrySet()) {
        resource.putLabels(entry.getKey(), entry.getValue().getAsString());
      }
    }
    return resource.build();
  }

  @Override
  public JsonElement serialize(@NonNull final Resource src, Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject resource = new JsonObject();
    final JsonObject labels = new JsonObject();
    if (src.getLabelsCount() > 0) {

      final Map<String, String> labelsMap = src.getLabelsMap();
      final TreeSet<String> labelKeys = new TreeSet<>(labelsMap.keySet());
      for (String key : labelKeys) {
        labels.addProperty(key, labelsMap.get(key));
      }
    }
    resource.add(PROPERTY_LABELS, labels);
    resource.addProperty(PROPERTY_TYPE, src.getType());
    return resource;
  }
}
