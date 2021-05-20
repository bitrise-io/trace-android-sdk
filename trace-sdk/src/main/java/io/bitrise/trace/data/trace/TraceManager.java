package io.bitrise.trace.data.trace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.opencensus.proto.trace.v1.Span;
import javax.inject.Singleton;

/**
 * Manages the {@link Trace}s.
 */
@Singleton
public interface TraceManager {

  /**
   * Starts the Trace.
   */
  void startTrace();

  /**
   * Stops the Trace.
   */
  void stopTrace();

  /**
   * Gets the active Trace.
   *
   * @return the active Trace or {@code null} when there is no active Trace.
   */
  @Nullable
  Trace getActiveTrace();

  /**
   * Adds a {@link Span} to the active Trace.
   *
   * @param span the Span to add.
   */
  void addSpanToActiveTrace(@NonNull Span span);

  /**
   * Gets the ID of the root {@link Span}.
   *
   * @return the ID of the root Span when there is one, {@code null} otherwise.
   */
  @Nullable
  String getRootSpanId();

  /**
   * Creates an ID for a {@link Span}.
   *
   * @param isRoot boolean value to determine if this is a root Span or not.
   * @return the created ID.
   */
  @NonNull
  String createSpanId(boolean isRoot);

  /**
   * Check if the TraceManager has been initialised or not.
   *
   * @return {@code true} when yes, {@code false} otherwise.
   */
  boolean isInitialised();
}
