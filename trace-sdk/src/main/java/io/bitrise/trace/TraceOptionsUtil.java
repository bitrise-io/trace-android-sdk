package io.bitrise.trace;

import androidx.annotation.Nullable;
import java.util.List;

/**
 * Utility object for working with {@link TraceOption}s.
 */
public class TraceOptionsUtil {

  /**
   * Determines if the sdk should trace {@link java.net.URLConnection} type network events.
   *
   * @param options the complete list of options provided when the sdk was initialised.
   * @return true by default, and false if the {@link TraceOption.NetworkUrlConnectionTracing}
   * object has been created and set to false.
   */
  protected static boolean determineIfNetworkUrlConnectionTracing(
       @Nullable final List<TraceOption> options) {

    if (options == null || options.size() == 0) {
      return true;
    }

    for (TraceOption option : options) {
      if (option instanceof TraceOption.NetworkUrlConnectionTracing) {
        return (Boolean) option.getValue();
      }
    }

    return true;
  }

  /**
   * Determines if the sdk should be in debug mode or not.
   *
   * @param options the complete list of options provided when the sdk was initialised.
   * @return false by default, and true if the {@link TraceOption.DebugMode} object has been created
   * and set to to true.
   */
  protected static boolean determineIfDebugMode(@Nullable final List<TraceOption> options) {
    if (options == null || options.size() == 0) {
      return false;
    }

    for (TraceOption option : options) {
      if (option instanceof TraceOption.DebugMode) {
        return (Boolean) option.getValue();
      }
    }

    return false;
  }

}
