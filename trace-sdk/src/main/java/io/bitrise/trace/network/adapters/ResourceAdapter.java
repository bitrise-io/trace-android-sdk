package io.bitrise.trace.network.adapters;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeSet;

import io.opencensus.proto.resource.v1.Resource;

/**
 * Adapter for {@link Resource} objects. This class is required because the current
 * {@link com.google.gson.protobuf.ProtoTypeAdapter} causes a StackOverflowException when
 * it serializes Resource objects.
 */
public class ResourceAdapter implements JsonSerializer<Resource>, JsonDeserializer<Resource> {

    private final String PROPERTY_LABELS = "labels";
    private final String PROPERTY_TYPE = "type";

    @Override
    public Resource deserialize(@NonNull final JsonElement json, @NonNull final Type typeOfT,
                                @NonNull final JsonDeserializationContext context) throws JsonParseException {
        // todo this is not currently needed - we do not save Resource objects to storage.
        return null;
    }

    @Override
    public JsonElement serialize(@NonNull final Resource src, Type typeOfSrc,
                                 @NonNull final JsonSerializationContext context) {
        final JsonObject resource = new JsonObject();
        final JsonObject labels = new JsonObject();
        if (src.getLabelsCount() > 0 ) {

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
