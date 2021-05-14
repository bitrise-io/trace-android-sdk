package io.bitrise.trace.test;

import androidx.annotation.NonNull;
import com.google.protobuf.ByteString;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.management.formatter.network.NetworkDataFormatter;
import io.bitrise.trace.data.management.formatter.view.ActivityStateDataFormatter;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceEntity;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides test {@link Trace} and {@link Span}'s.
 */
public class TraceTestProvider {

  private TraceTestProvider() {
    throw new UnsupportedOperationException("Private constructor for TraceTestProvider!");
  }

  /**
   * Gets an empty {@link TraceEntity} for testing purposes.
   *
   * @return the Trace.
   */
  @NonNull
  public static Trace getEmptyTrace() {
    return new Trace("Empty Trace", Collections.emptyList());
  }

  /**
   * Gets a sample {@link TraceEntity} for testing purposes.
   *
   * @return the Trace.
   */
  @NonNull
  public static Trace getSampleTrace() {
    final Trace trace = new Trace();
    final String sampleTraceId = "Sample Trace ID";
    trace.setTraceId(sampleTraceId);
    trace.setSpanList(Arrays.asList(
        getSampleSpan(sampleTraceId, "Sample Span 1"),
        getSampleSpan(sampleTraceId, "Sample Span 2")));
    return trace;
  }

  /**
   * Gets a different sample {@link Trace} for testing purposes. This should be different from
   * {@link #getSampleTrace()}.
   *
   * @return the Trace.
   */
  @NonNull
  public static Trace getOtherTrace() {
    final Trace trace = new Trace();
    final String otherTraceId = "Other Trace ID";
    trace.setTraceId(otherTraceId);
    trace.setSpanList(Arrays.asList(
        getSampleSpan(otherTraceId, "Other Span 1"),
        getSampleSpan(otherTraceId, "Other Span 2")));
    return trace;
  }

  /**
   * Gets a sample {@link Span} for testing purposes.
   *
   * @param traceId the ID of the parent {@link TraceEntity}.
   * @return the Span.
   */
  @NonNull
  public static Span getSampleSpan(@NonNull final String traceId, @NonNull final String spanName) {
    final Span span = ActivityStateDataFormatter.createActivityViewSpan(
        spanName, 1613645681150L, 1613645691150L, "span-id");
    ;
    return span.toBuilder()
               .setTraceId(ByteString.copyFrom(traceId, Charset.defaultCharset()))
               .build();
  }

  /**
   * Create a Network {@link Span} that a {@link NetworkDataFormatter} would make.
   *
   * @return a Span representing a network event.
   */
  @NonNull
  public static Span createNetworkSpan() {
    final NetworkData networkData = new NetworkData("span-id2", "parent-span-id");
    networkData.setMethod("POST");
    networkData.setUrl("https:///www.google.com");
    networkData.setStatusCode(200);
    networkData.setStart(1613645681140L);
    networkData.setEnd(1613645691150L);
    final Span networkSpan =
        NetworkDataFormatter.createNetworkSpan(networkData, networkData.getUrl());
    return networkSpan.toBuilder()
                      .setTraceId(ByteString.copyFrom("trace-id", Charset.defaultCharset()))
                      .build();
  }

  /**
   * Create a View {@link Span} that a {@link ActivityStateDataFormatter} would make.
   *
   * @return a Span representing a view activity event.
   */
  @NonNull
  public static Span createActivityViewSpan() {
    final Span viewSpan = ActivityStateDataFormatter.createActivityViewSpan(
        "name", 1613645681160L, 1613645691170L, "span-id");
    return viewSpan.toBuilder()
                   .setTraceId(ByteString.copyFrom("trace-id", Charset.defaultCharset()))
                   .build();
  }

  /**
   * Gets a {@link TruncatableString} from the given String input.
   *
   * @param string the String input.
   * @return the TruncatableString.
   */
  @NonNull
  public static TruncatableString getTruncatableString(@NonNull final String string) {
    final TruncatableString.Builder truncatableStringBuilder = TruncatableString.newBuilder();
    truncatableStringBuilder.setValue(string);
    return truncatableStringBuilder.build();
  }

  /**
   * Gets a sample attribute for testing purposes.
   *
   * @return the Attributes.
   */
  @NonNull
  public static Map<String, Object> getSampleAttributes() {
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("Sample attribute name", "Sample attribute value");

    return attributes;
  }
}
