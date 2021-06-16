package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.trace.Trace;
import io.opencensus.proto.metrics.v1.Metric;
import java.io.File;
import java.util.Map;
import javax.inject.Singleton;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Holds the list of network calls.
 */
@Singleton
public interface NetworkCommunicator {

  String METRIC_HEADER_ACCEPTED_COUNT = "accepted-metrics-count";
  String METRIC_HEADER_ACCEPTED_LABELS = "accepted-metrics-labels";

  /**
   * Sends the given List of {@link Metric}s to the server.
   * Note: These headers are required for the updated endpoint.
   *
   * @param metricRequest the {@link MetricRequest} to send that contains the Metrics.
   * @return the result of the Call.
   */
  @Headers({
      "Accept: application/vnd.bitrise.trace-v1+json",
      "Content-Type: application/vnd.bitrise.trace-v1+json"
  })
  @POST("/api/metrics")
  Call<Void> sendMetrics(@Body @NonNull final MetricRequest metricRequest);

  /**
   * Sends the given List of {@link Trace} to the server.
   * Note: These headers are different to the metrics endpoint. When we have an updated endpoint
   * for traces these should be removed and added to the NetworkClient.
   *
   * @param traceRequest the {@link TraceRequest} to send that contains the Spans.
   * @return the result of the Call.
   */
  @Headers({
      "Accept: application/json",
      "Content-Type: application/json"
  })
  @POST("/api/v1/trace")
  Call<Void> sendTraces(@Body @NonNull final TraceRequest traceRequest);

  @Multipart
  @POST("/api/v1.0.1/crashes/android")
  Call<Void> sendCrashes(@PartMap Map<String, RequestBody> fields,
                         @Part("report") RequestBody file);
}
