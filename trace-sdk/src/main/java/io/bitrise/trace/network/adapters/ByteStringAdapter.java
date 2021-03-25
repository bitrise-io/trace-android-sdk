package io.bitrise.trace.network.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.ByteString;

import java.lang.reflect.Type;

import io.bitrise.trace.utils.ByteStringConverter;

/**
 * This adapter serializes and deserializes {@link ByteString} type fields.
 */
public class ByteStringAdapter implements JsonSerializer<ByteString>, JsonDeserializer<ByteString> {
    @Override
    public ByteString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ByteStringConverter.toByteString(json.getAsString());
    }

    @Override
    public JsonElement serialize(ByteString src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(ByteStringConverter.toString(src));
    }
}
