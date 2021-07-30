package io.bitrise.trace;

import androidx.annotation.VisibleForTesting;

/**
 * Object for providing options when initialising the trace sdk. TraceOption should not be
 * directly created, but there should be individual objects for each type of option which extend
 * from the TraceOption object.
 */
public class TraceOption {

  private final Object value;

  public Object getValue() {
    return value;
  }

  // this should not be used by anyone
  @VisibleForTesting
  protected TraceOption(Object value) {
    this.value = value;
  }

  /**
   * Option object for customising whether the trace sdk should trace URL Connection type network
   * events.
   */
  public static class NetworkUrlConnectionTracing extends TraceOption {
    public NetworkUrlConnectionTracing(boolean isEnabled) {
      super(isEnabled);
    }
  }



}

