package io.bitrise.trace.data.trace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.room.Ignore;
import io.bitrise.trace.data.storage.TraceDatabase;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.utils.ByteStringConverter;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.opencensus.proto.trace.v1.Span;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data class for Traces.
 */
public class Trace {

  /**
   * The ID of the Trace. Product requires 16 char length for it.
   */
  @NonNull
  private String traceId;

  /**
   * The List of {@link Span}s.
   */
  @NonNull
  private List<Span> spanList;

  /**
   * The ID of the {@link Session}.
   */
  @NonNull
  private String sessionId;

  /**
   * Constructor for class. Used by {@link TraceDatabase}. This enables Room to create Trace
   * objects.
   */
  public Trace() {
    this.traceId = UniqueIdGenerator.makeTraceId();
    this.sessionId = getCurrentSessionId();
    this.spanList = new ArrayList<>();
  }

  /**
   * Constructor for creating Traces.
   *
   * @param traceId  the ID of the Trace.
   * @param spanList the List of {@link Span}s for this Trace.
   */
  @Ignore
  public Trace(@NonNull final String traceId, @NonNull final List<Span> spanList) {
    this.traceId = traceId;
    this.sessionId = getCurrentSessionId();
    this.spanList = spanList;
  }

  /**
   * Gets the active {@link Session}'s ID, or throws IllegalStateException when there is no
   * active Session.
   *
   * @return the Session ID.
   */
  @NonNull
  private String getCurrentSessionId() {
    final Session activeSession = ApplicationSessionManager.getInstance().getActiveSession();
    if (activeSession == null) {
      throw new IllegalStateException("There must be an active Session to create Traces!");
    }
    return activeSession.getUlid();
  }

  @Override
  public boolean equals(@Nullable final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Trace)) {
      return false;
    }
    final Trace trace = (Trace) o;
    return traceId.equals(trace.traceId)
        && sessionId.equals(trace.sessionId)
        && spanList.equals(trace.spanList);
  }

  @NonNull
  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(@NonNull final String traceId) {
    this.traceId = traceId;
  }

  @NonNull
  public String getSessionId() {
    return sessionId;
  }

  @VisibleForTesting
  public void setSessionId(@NonNull final String sessionId) {
    this.sessionId = sessionId;
  }

  @NonNull
  public List<Span> getSpanList() {
    return spanList;
  }

  public void setSpanList(@NonNull final List<Span> spanList) {
    this.spanList = spanList;
  }

  /**
   * Finds the last active view style {@link Span}, or null if none can be found.
   *
   * @return the Span that represents the last view on the screen.
   */
  @Nullable
  public Span getLastActiveViewSpan() {
    // if we have zero spans - return null
    if (spanList.isEmpty()) {
      return null;
    }

    // if we only have 1 span - we should only send back if it is an activity style
    if (spanList.size() == 1) {
      if (spanList.get(0).hasAttributes()) {
        return null;
      } else {
        return spanList.get(0);
      }
    }

    Span lastSpan = spanList.get(0);

    for (Span span : spanList) {

      long lastSpanStart = TraceClock.timestampToMillis(lastSpan.getStartTime());
      long start = TraceClock.timestampToMillis(span.getStartTime());

      // ignore network style spans
      if (! span.hasAttributes()) {
        if (start > lastSpanStart) {
          lastSpan = span;
        }
      }
    }

    // if we started with a network span - and never had any later spans - we need to ignore it.
    if (lastSpan.hasAttributes()) {
      return null;
    }

    return lastSpan;
  }

  /**
   * Creates a string that contains the trace id, span size, and each spans id and name. This is
   * useful for debugging what each trace actually contains.
   *
   * @return a string that can be logged for additional debugging info.
   */
  @NonNull
  public String getDebugLoggingInfo() {
    final StringBuilder sb = new StringBuilder();

    sb.append("trace id: ").append(traceId).append("\n");
    sb.append("span size: ").append(getSpanList().size()).append("\n");

    for (Span span : getSpanList()) {
      sb.append("   ")
        .append("span id: ").append(ByteStringConverter.toString(span.getSpanId()))
        .append(" span name: ").append(span.getName().getValue()).append("\n");
    }

    return sb.toString();
  }

  /**
   * Adds the given {@link Span} to the {@link #spanList}.
   *
   * @param span the Span to add.
   */
  public void addSpan(@NonNull final Span span) {
    spanList.add(span);
  }

  @Override
  public String toString() {
    return "Trace{"
        + "traceId='" + traceId + '\''
        + ", spanList=" + spanList
        + ", sessionId='" + sessionId + '\''
        + '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, spanList, sessionId);
  }
}
