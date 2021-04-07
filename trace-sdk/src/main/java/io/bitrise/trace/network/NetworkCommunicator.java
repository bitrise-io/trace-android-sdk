package io.bitrise.trace.network;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import io.bitrise.trace.data.trace.Trace;
import io.opencensus.proto.metrics.v1.Metric;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Holds the list of network calls.
 */
@Singleton
public interface NetworkCommunicator {

    /**
     * Sends the given List of {@link Metric}s to the server.
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
     *
     * @param traceRequest the {@link TraceRequest} to send that contains the Spans.
     * @return the result of the Call.
     */
    @POST("/api/v1/trace")
    Call<Void> sendTraces(@Body @NonNull final TraceRequest traceRequest);

    @POST("/api/v1/crashes/android")
    Call<Void> sendCrashes();
}
