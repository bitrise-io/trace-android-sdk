package io.bitrise.trace.network.adapters;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.opencensus.proto.trace.v1.TruncatableString;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link TruncatableString} objects.
 */
public class TruncatableStringAdapter implements JsonSerializer<TruncatableString>,
    JsonDeserializer<TruncatableString> {

  private static final String PROPERTY_VALUE = "value";

  @Override
  public TruncatableString deserialize(@NonNull final JsonElement json,
                                       @NonNull final Type typeOfT,
                                       @NonNull final  JsonDeserializationContext context)
      throws JsonParseException {

    final JsonObject srcString = json.getAsJsonObject();
    final TruncatableString.Builder builder = TruncatableString.newBuilder();
    builder.setValue(srcString.get(PROPERTY_VALUE).getAsString());

    return builder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final  TruncatableString src,
                               @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty(PROPERTY_VALUE, src.getValue());
    return jsonObject;
  }
}
