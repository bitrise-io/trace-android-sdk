package io.bitrise.trace.data.trace;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.google.gson.Gson;

import io.bitrise.trace.network.NetworkClient;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;

/**
 * Converter for {@link Trace} to store it in {@link io.bitrise.trace.data.storage.TraceDatabase}.
 */
public class TraceConverter {
    private static final Gson gson = NetworkClient.getGson();

    /**
     * Converts the given String value to a {@link Trace}.
     *
     * @param value the value to convert.
     * @return the Trace for the given value.
     */
    @NonNull
    @TypeConverter
    public static Trace toTrace(@NonNull final String value) {
        try {
            return gson.fromJson(value, Trace.class);
        } catch (Exception e) {
            TraceLog.w(e);
            TraceLog.d(String.format(LogMessageConstants.CONVERTER_FAILED_WITH_VALUE, value));
            return new Trace();
        }
    }

    /**
     * Converts the given {@link Trace} to a String value.
     *
     * @param trace the Trace to convert.
     * @return the String value of the given Trace.
     */
    @TypeConverter
    public static String toString(@NonNull final Trace trace) {
        return gson.toJson(trace, Trace.class);
    }
}
