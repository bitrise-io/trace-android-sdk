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
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.DistributionValue;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.SummaryValue;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link Point} type fields.
 * This is required for the time being as the backend does not support double_value or int64_value
 * but instead requires value. This is because proto oneOfs are confusing.
 */
public class PointAdapter implements JsonSerializer<Point>, JsonDeserializer<Point> {

  private static final String PROPERTY_VALUE = "value";
  private static final String PROPERTY_TIMESTAMP = "timestamp";
  private static final String PROPERTY_VALUE_CASE = "value_case";

  @Override
  public Point deserialize(@NonNull final JsonElement json, @NonNull final Type typeOfT,
                           @NonNull final JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject srcPoint = json.getAsJsonObject();
    final JsonElement srcValueCase = srcPoint.get(PROPERTY_VALUE_CASE);
    final Point.Builder pointBuilder = Point.newBuilder();

    if (srcValueCase != null) {
      final Point.ValueCase valueCase = Point.ValueCase.forNumber(srcValueCase.getAsInt());

      if (valueCase != null) {
        switch (valueCase) {
          case INT64_VALUE:
            pointBuilder.setInt64Value(srcPoint.get(PROPERTY_VALUE).getAsInt());
            break;
          case DOUBLE_VALUE:
            pointBuilder.setDoubleValue(srcPoint.get(PROPERTY_VALUE).getAsDouble());
            break;
          case DISTRIBUTION_VALUE:
            final DistributionValue distributionValue = NetworkClient.getGson().fromJson(
                srcPoint.get(PROPERTY_VALUE), DistributionValue.class);
            pointBuilder.setDistributionValue(distributionValue);
            break;
          case SUMMARY_VALUE:
            final SummaryValue summaryValue = NetworkClient.getGson().fromJson(
                srcPoint.get(PROPERTY_VALUE), SummaryValue.class);
            pointBuilder.setSummaryValue(summaryValue);
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

    final Timestamp timestamp = NetworkClient.getGson().fromJson(
        srcPoint.get(PROPERTY_TIMESTAMP), Timestamp.class);
    pointBuilder.setTimestamp(timestamp);
    return pointBuilder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final Point src, @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();

    switch (src.getValueCase()) {
      case INT64_VALUE:
        jsonObject.addProperty(PROPERTY_VALUE, src.getInt64Value());
        break;
      case DOUBLE_VALUE:
        jsonObject.addProperty(PROPERTY_VALUE, src.getDoubleValue());
        break;
      case DISTRIBUTION_VALUE:
      case SUMMARY_VALUE:
        jsonObject .add(PROPERTY_VALUE, new JsonObject());
        break;
      case VALUE_NOT_SET:
        // we deliberately do not serialize values that are not set.
        break;
      default:
        // nop.
        break;
    }

    jsonObject.addProperty(PROPERTY_VALUE_CASE, src.getValueCase().getNumber());
    jsonObject.add(PROPERTY_TIMESTAMP, NetworkClient.getGson().toJsonTree(src.getTimestamp()));
    return jsonObject;
  }
}
