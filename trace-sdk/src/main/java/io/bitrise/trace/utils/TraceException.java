package io.bitrise.trace.utils;

import androidx.annotation.NonNull;
import io.opencensus.proto.trace.v1.Span;


/**
 * TraceExceptions are used to express and describe some kind of business type exceptions.
 */
public final class TraceException {

  /**
   * Used when an Activity context is not found.
   */
  public static class ActivityContextNotFoundException extends Exception {
    public ActivityContextNotFoundException() {
      super("Could not find an application context.");
    }
  }

  /**
   * Used for when a Trace is deleted without any child spans.
   */
  public static class EmptyTraceException extends Exception {

    /**
     * Constructor for class.
     *
     * @param traceId the Trace Id.
     */
    public EmptyTraceException(@NonNull final String traceId) {
      super(String.format("Trace with ID '%1$s' does not have any child spans and will be deleted.",
          traceId));
    }
  }

  /**
   * Used when attempting to save FormattedData and the span is non null.
   */
  public static class FormattedDataSpanNonNullException extends Exception {

    /**
     * Constructor for class.
     *
     * @param span the given Span.
     */
    public FormattedDataSpanNonNullException(@NonNull final Span span) {
      super(String.format("FormattedData Span value is non null, it should be sent through "
          + "the TraceManager. The Span value: %1$s ", span.toString()));
    }
  }

  /**
   * Used when the FragmentLifecycleCallback cannot be registered on a class.
   */
  public static class FragmentLifecycleCallbackException extends Exception {

    /**
     * Constructor for class.
     *
     * @param className the name of the class.
     */
    public FragmentLifecycleCallbackException(@NonNull final String className) {
      super(String
          .format("Could not register FragmentLifecycleCallbacks on class '%1$s'.", className));
    }
  }

  /**
   * Used when a FragmentStateData has no valid spans.
   */
  public static class FragmentStateDataNoValidSpansException extends Exception {

    /**
     * Constructor for class.
     *
     * @param fragmentName the name of the Fragment.
     */
    public FragmentStateDataNoValidSpansException(@NonNull final String fragmentName) {
      super(
          String.format("No valid spans in this FragmentStateDataEntry for '%1$s'", fragmentName));
    }
  }

  /**
   * Used when a FragmentStateData has incomplete spans.
   */
  public static class FragmentStateDataIncompleteSpansException extends Exception {

    /**
     * Constructor for class.
     *
     * @param onViewCreatedSpans the number of Spans from onViewCreated.
     * @param onViewPausedSpans  the number of Spans from onViewPaused.
     */
    public FragmentStateDataIncompleteSpansException(final int onViewCreatedSpans,
                                                     final int onViewPausedSpans) {
      super(String.format(
          "Incomplete spans: number of onViewCreated states were %1$s, but number of paused "
              + "states were %2$s", onViewCreatedSpans, onViewPausedSpans));
    }
  }

  /**
   * Used when a FragmentStateData cannot find any onPaused records after an onCreated record.
   */
  public static class FragmentStateNoViewPausedAfterCreatedTimestampException extends Exception {

    /**
     * Constructor for class.
     *
     * @param timestamp the timestamp.
     */
    public FragmentStateNoViewPausedAfterCreatedTimestampException(final long timestamp) {
      super(String.format("Could not find a later span for an onViewCreated timestamp of %1$s.",
          timestamp));
    }
  }

  /**
   * Used when a FragmentStateData has a missing onViewCreated span.
   */
  public static class FragmentStateDataNoOnViewCreatedException extends Exception {

    /**
     * Constructor for class.
     *
     * @param fragmentName the name of the Fragment.
     */
    public FragmentStateDataNoOnViewCreatedException(@NonNull final String fragmentName) {
      super(String
          .format("No onViewCreated states for fragment '%1$s' to create the span", fragmentName));
    }
  }

  /**
   * Used when a FragmentStateData finds no onPaused spans.
   */
  public static class FragmentStateDataNoPausedSpansException extends Exception {

    /**
     * Constructor for class.
     *
     * @param fragmentName the name of the Fragment.
     */
    public FragmentStateDataNoPausedSpansException(@NonNull final String fragmentName) {
      super(String.format("No paused states for fragment '%1$s' to create the span", fragmentName));
    }
  }

  /**
   * Used in the TraceSdk when the ConfigurationManager cannot initialise.
   */
  public static class TraceConfigNotInitialisedException extends Exception {

    /**
     * Constructor for class.
     */
    public TraceConfigNotInitialisedException() {
      super(
          "Trace could not be initialised. Please make sure the right "
              + "bitrise-addons-configuration.json is in the correct location. Trace will not "
              + "report any data for this session.");
    }
  }

  /**
   * Used for when a Metric fails to send.
   */
  public static class MetricSenderFailedException extends Exception {

    /**
     * Constructor for class.
     *
     * @param statusCode the HTTP status code.
     * @param message    the message.
     */
    public MetricSenderFailedException(final int statusCode, @NonNull final String message) {
      super(String.format("Metric failed to send: received HTTP %1$s : %2$s", statusCode,
          message));
    }
  }

  /**
   * Used for when a Trace fails to send.
   */
  public static class TraceSenderFailedException extends Exception {

    /**
     * Constructor for class.
     *
     * @param statusCode the HTTP status code.
     * @param message    the message.
     */
    public TraceSenderFailedException(final int statusCode, @NonNull final String message) {
      super(String.format("Trace failed to send: received HTTP %1$s : %2$s", statusCode, message));
    }
  }
}
