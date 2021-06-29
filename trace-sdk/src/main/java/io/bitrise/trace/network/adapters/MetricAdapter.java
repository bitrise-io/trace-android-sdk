package io.bitrise.trace.network.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.network.NetworkClient;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.TimeSeries;
import java.lang.reflect.Type;

public class MetricAdapter implements JsonSerializer<Metric>, JsonDeserializer<Metric> {

  private static final String PROPERTY_METRIC_DESCRIPTOR = "metric_descriptor";
  private static final String PROPERTY_TIMESERIES = "timeseries";

  @Override
  public Metric deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject srcMetric = json.getAsJsonObject();
    final Metric.Builder builder = Metric.newBuilder();

    builder.setMetricDescriptor(NetworkClient.getGson().fromJson(
        srcMetric.get(PROPERTY_METRIC_DESCRIPTOR), MetricDescriptor.class));

    if (srcMetric.get(PROPERTY_TIMESERIES).isJsonArray()) {
      final JsonArray jsonArray = srcMetric.get(PROPERTY_TIMESERIES).getAsJsonArray();
      for (JsonElement jsonElement : jsonArray) {
        builder.addTimeseries(NetworkClient.getGson().fromJson(
            jsonElement.getAsJsonObject(), TimeSeries.class));
      }
    }
    return builder.build();
  }

  @Override
  public JsonElement serialize(Metric src, Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();

    jsonObject.add(PROPERTY_METRIC_DESCRIPTOR,
        NetworkClient.getGson().toJsonTree(src.getMetricDescriptor()));

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
