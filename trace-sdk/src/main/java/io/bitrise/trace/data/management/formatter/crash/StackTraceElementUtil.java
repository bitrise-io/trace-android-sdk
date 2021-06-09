package io.bitrise.trace.data.management.formatter.crash;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility method to convert {@link StackTraceElement}'s into Strings.
 */
public class StackTraceElementUtil {

  /**
   * Creates a Map of String to String from a map of Thread to StackTraceElement[]. The resulting
   * map will use the key from the Thread id, and stringified StackTraceElement[].
   *
   * @param allStackTraces the result of calling <code>Thread.getAllStackTraces()</code>.
   * @return the map of thread id to stringified StackTraceElement[].
   */
  public static Map<String, String> createStringifiedReport(
      @NonNull Map<Thread, StackTraceElement[]> allStackTraces) {
    final Map<String, String> map = new HashMap<>();

    for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {

      final String stringifiedStackTrace = createStringifiedStackTrace(entry.getValue());
      final String threadId = Long.toString(entry.getKey().getId());
      map.put(threadId, stringifiedStackTrace);

    }

    return map;
  }

  /**
   * Creates a String from a list of {@link StackTraceElement}.
   *
   * @param stackTraceElements the StackTraceElements to convert to String.
   * @return the stringied representation of the stackTraceElements.
   */
  public static String createStringifiedStackTrace(
      @NonNull StackTraceElement[] stackTraceElements) {

    final StringBuilder sb = new StringBuilder();

    for (int i = 0; i < stackTraceElements.length; i++) {
      sb.append(stackTraceElements[i].toString());

      if (i != (stackTraceElements.length) - 1) {
        sb.append("\n");
      }
    }

    return sb.toString();
  }
}
