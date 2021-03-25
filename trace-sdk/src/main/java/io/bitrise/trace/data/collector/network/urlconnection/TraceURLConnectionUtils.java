package io.bitrise.trace.data.collector.network.urlconnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

import io.bitrise.trace.utils.StringUtils;
import okhttp3.Headers;

/**
 * Utility methods for {@link TraceHttpURLConnection} and {@link TraceHttpsURLConnection}.
 */
public class TraceURLConnectionUtils {

    private TraceURLConnectionUtils() {
        throw new UnsupportedOperationException("Private constructor for class!");
    }

    @NonNull
    public static Headers getHeaders(@Nullable Map<String, List<String>> currentHeaderFields) {
        Headers.Builder headers = new Headers.Builder();

        if (currentHeaderFields != null &&
                !currentHeaderFields.isEmpty()) {

            for (Map.Entry<String, List<String>> entry : currentHeaderFields.entrySet()) {
                String stringValue = StringUtils.join(entry.getValue(), ",");
                headers.add(entry.getKey(), stringValue);
            }

        }

        return headers.build();
    }

}
