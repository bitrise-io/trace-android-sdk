package io.bitrise.trace.data.metric;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;

import io.bitrise.trace.network.NetworkClient;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.metrics.v1.Metric;

/**
 * Converter for {@link Metric} to store it in {@link io.bitrise.trace.data.storage.TraceDatabase}. Due to an
 * existing GSON issue, we cannot convert directly to Metric and back, first we have to convert to a {@link ByteString}.
 *
 * @see <a href="https://github.com/google/gson/issues/1084">Related Github issue</a>
 */
public class MetricConverter {

    private static final Gson gson = NetworkClient.getGson();

    /**
     * Converts the given String value to a {@link Metric}.
     *
     * @param value the value to convert.
     * @return the Metric for the given value.
     */
    @NonNull
    @TypeConverter
    public static Metric toMetric(@NonNull final String value) {
        try {
            return gson.fromJson(value, Metric.class);
        } catch (Exception e) {
            TraceLog.w(e);
            TraceLog.d(String.format(LogMessageConstants.CONVERTER_FAILED_WITH_VALUE, value));
            return Metric.newBuilder().build();
        }
    }

    /**
     * Converts the given {@link Metric} to a String value.
     *
     * @param metric the Metric to convert.
     * @return the String value of the given Metric.
     */
    @TypeConverter
    public static String toString(@NonNull final Metric metric) {
        return gson.toJson(metric, Metric.class);
    }
}
