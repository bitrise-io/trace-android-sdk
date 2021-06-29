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
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.TimeSeries;
import java.lang.reflect.Type;

/**
 * This adapter serializes and deserializes {@link Metric} objects.
 */
public class MetricAdapter implements JsonSerializer<Metric>, JsonDeserializer<Metric> {

  private static final String PROPERTY_METRIC_DESCRIPTOR = "metric_descriptor";
  private static final String PROPERTY_TIMESERIES = "timeseries";

  @Override
  public Metric deserialize(@NonNull final JsonElement json,
                            @NonNull final Type typeOfT,
                            @NonNull final JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcMetric = json.getAsJsonObject();
    final Metric.Builder builder = Metric.newBuilder();

    if (srcMetric.has(PROPERTY_METRIC_DESCRIPTOR)) {
      builder.setMetricDescriptor(NetworkClient.getGson().fromJson(
          srcMetric.get(PROPERTY_METRIC_DESCRIPTOR), MetricDescriptor.class));
    }

    if (srcMetric.has(PROPERTY_TIMESERIES)
        && srcMetric.get(PROPERTY_TIMESERIES).isJsonArray()) {
      final JsonArray jsonArray = srcMetric.get(PROPERTY_TIMESERIES).getAsJsonArray();
      for (JsonElement jsonElement : jsonArray) {
        builder.addTimeseries(NetworkClient.getGson().fromJson(
            jsonElement.getAsJsonObject(), TimeSeries.class));
      }
    }
    return builder.build();
  }

  @Override
  public JsonElement serialize(@NonNull final Metric src,
                               @NonNull final Type typeOfSrc,
                               @NonNull final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();

    if (src.getMetricDescriptor() != MetricDescriptor.getDefaultInstance()) {
      jsonObject.add(PROPERTY_METRIC_DESCRIPTOR,
          NetworkClient.getGson().toJsonTree(src.getMetricDescriptor()));
    }

    if (src.getTimeseriesCount() > 0) {
      final JsonArray jsonTimeSeries = new JsonArray();
      for (final TimeSeries timeSeries : src.getTimeseriesList()) {
        jsonTimeSeries.add(NetworkClient.getGson().toJsonTree(timeSeries));
      }
      jsonObject.add(PROPERTY_TIMESERIES, jsonTimeSeries);
    }

    return jsonObject;
  }
}
