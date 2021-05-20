package io.bitrise.trace.data.trace;

import androidx.annotation.NonNull;
import io.opencensus.proto.trace.v1.Span;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Trace} related utility methods.
 */
public class TraceUtils {

  private TraceUtils() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  /**
   * Converts a List of {@link TraceEntity}s to a List of {@link Trace}.
   *
   * @param traceEntityList the List to convert.
   * @return the converted List.
   */
  @NonNull
  public static List<Trace> toTraceList(@NonNull final List<TraceEntity> traceEntityList) {
    final List<Trace> traceList = new ArrayList<>();
    for (@NonNull final TraceEntity traceEntity : traceEntityList) {
      traceList.add(traceEntity.getTrace());
    }
    return traceList;
  }

  /**
   * Gets all the {@link Span}s for the given List of {@link Trace}s.
   *
   * @param traceList the given List of Traces.
   * @return the List of Spans.
   */
  @NonNull
  public static List<Span> getSpans(@NonNull final List<Trace> traceList) {
    final List<Span> spans = new ArrayList<>();
    for (@NonNull final Trace trace : traceList) {
      spans.addAll(trace.getSpanList());
    }
    return spans;
  }
}
