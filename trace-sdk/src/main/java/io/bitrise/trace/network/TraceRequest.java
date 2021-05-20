package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.trace.Trace;
import io.opencensus.proto.resource.v1.Resource;
import io.opencensus.proto.trace.v1.Span;
import java.util.List;

/**
 * Data class for sending {@link Trace}s over the network with the {@link NetworkCommunicator}.
 */
public class TraceRequest extends NetworkRequest {

  @NonNull
  private final List<Span> spans;

  public TraceRequest(@NonNull final Resource resource,
                      @NonNull final List<Span> spans) {
    this.resource = resource;
    this.spans = spans;
  }

  @NonNull
  public List<Span> getSpans() {
    return spans;
  }
}
