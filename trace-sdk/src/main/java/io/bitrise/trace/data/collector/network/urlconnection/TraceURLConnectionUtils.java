package io.bitrise.trace.data.collector.network.urlconnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.utils.StringUtils;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;

/**
 * Utility methods for {@link TraceHttpURLConnection} and {@link TraceHttpsURLConnection}.
 */
@SuppressWarnings("checkstyle:abbreviation")
public class TraceURLConnectionUtils {

  private TraceURLConnectionUtils() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  /**
   * Gets the {@link Headers} from a given Map.
   *
   * @param currentHeaderFields the given Map.
   * @return the Headers.
   */
  @NonNull
  public static Headers getHeaders(@Nullable final Map<String, List<String>> currentHeaderFields) {
    Headers.Builder headers = new Headers.Builder();

    if (currentHeaderFields != null && !currentHeaderFields.isEmpty()) {

      for (Map.Entry<String, List<String>> entry : currentHeaderFields.entrySet()) {
        String stringValue = StringUtils.join(entry.getValue(), ",");
        headers.add(entry.getKey(), stringValue);
      }

    }

    return headers.build();
  }
}
