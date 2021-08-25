package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;
import java.util.List;

/**
 * Data class for sending {@link Metric}s over the network with the {@link NetworkCommunicator}.
 */
public class MetricRequest extends NetworkRequest {

  @SerializedName("metrics")
  @NonNull
  private final List<Metric> metrics;

  public MetricRequest(@NonNull final Resource resource, @NonNull final List<Metric> metrics) {
    this.resource = resource;
    this.metrics = metrics;
  }

  @NonNull
  public List<Metric> getMetrics() {
    return metrics;
  }
}
