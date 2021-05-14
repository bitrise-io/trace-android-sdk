package io.bitrise.trace.data.collector.network;

import androidx.annotation.NonNull;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Common interface for receiving OkHttp network call details.
 */
public interface OkHttpCallProcessor {

  /**
   * Callback to call for further processing of the OkHttp network call.
   *
   * @param request  the {@link Request}.
   * @param response the {@link Response}.
   * @param start    the start time of the network call in milliseconds.
   * @param end      the end time of the network call in milliseconds.
   */
  void processOkHttpCall(@NonNull final Request request, @NonNull final Response response,
                         final long start,
                         final long end);
}
