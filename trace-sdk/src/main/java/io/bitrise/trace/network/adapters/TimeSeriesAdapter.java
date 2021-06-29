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
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link TimeSeries} objects.
 */
public class TimeSeriesAdapter implements JsonSerializer<TimeSeries>,
    JsonDeserializer<TimeSeries> {

  private static final String PROPERTY_VALUE = "value";
  private static final String PROPERTY_LABEL_VALUES = "label_values";
  private static final String PROPERTY_POINTS = "points";

  @Override
  public TimeSeries deserialize(@NonNull final JsonElement json,
                                @NonNull final Type typeOfT,
                                @NonNull final JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcTimeSeries = json.getAsJsonObject();
    final TimeSeries.Builder builder = TimeSeries.newBuilder();

    // add labels
    if (srcTimeSeries.get(PROPERTY_LABEL_VALUES).isJsonArray()) {
      final JsonArray jsonLabelVales = srcTimeSeries.get(PROPERTY_LABEL_VALUES).getAsJsonArray();

      for (JsonElement jsonElement : jsonLabelVales) {
        final JsonObject jsonValue = jsonElement.getAsJsonObject();
        final LabelValue.Builder labelValue = LabelValue.newBuilder();
        labelValue.setValue(jsonValue.get(PROPERTY_VALUE).getAsString());
        builder.addLabelValues(labelValue);
      }
    }

    // add points
    if (srcTimeSeries.get(PROPERTY_POINTS).isJsonArray()) {
      final JsonArray jsonPoints = srcTimeSeries.get(PROPERTY_POINTS).getAsJsonArray();

      for (JsonElement jsonElement : jsonPoints) {
        final Point point = NetworkClient.getGson().fromJson(
            jsonElement, Point.class);
        builder.addPoints(point);
      }
    }

    return builder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final TimeSeries src,
                               @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();

    // add label values
    if (src.getLabelValuesCount() > 0) {
      final JsonArray jsonLabelValues = new JsonArray();
      for (final LabelValue labelValue : src.getLabelValuesList()) {
        final JsonObject jsonValue = new JsonObject();
        jsonValue.addProperty(PROPERTY_VALUE, labelValue.getValue());
        jsonLabelValues.add(jsonValue);
      }
      jsonObject.add(PROPERTY_LABEL_VALUES, jsonLabelValues);
    }

    // add points
    if (src.getPointsCount() > 0) {
      final JsonArray jsonPoints = new JsonArray();
      for (final Point point : src.getPointsList()) {
        jsonPoints.add(NetworkClient.getGson().toJsonTree(point));
      }
      jsonObject.add(PROPERTY_POINTS, jsonPoints);
    }
    return jsonObject;
  }
}
