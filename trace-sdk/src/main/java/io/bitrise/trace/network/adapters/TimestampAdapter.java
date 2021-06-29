package io.bitrise.trace.network.adapters;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.Timestamp;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link Timestamp} objects.
 */
public class TimestampAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

  private static final String PROPERTY_SECONDS = "seconds";
  private static final String PROPERTY_NANOS = "nanos";

  @Override
  public Timestamp deserialize(@NonNull final JsonElement json,
                               @NonNull final Type typeOfT,
                               @NonNull final JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcTimestamp = json.getAsJsonObject();

    final Timestamp.Builder builder = Timestamp.newBuilder();
    builder.setSeconds(srcTimestamp.get(PROPERTY_SECONDS).getAsLong());
    builder.setNanos(srcTimestamp.get(PROPERTY_NANOS).getAsInt());
    return builder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final Timestamp src,
                               @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty(PROPERTY_SECONDS, src.getSeconds());
    jsonObject.addProperty(PROPERTY_NANOS, src.getNanos());
    return jsonObject;
  }
}
