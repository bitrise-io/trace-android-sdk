package io.bitrise.trace.utils;

import java.util.UUID;

/**
 * Creates unique identifiers.
 */
public class UniqueIdGenerator {

  public static final int SPAN_ID_LENGTH = 16;
  public static final int TRACE_ID_LENGTH = 32;

  /**
   * Creates a 16 character span id.
   */
  public static String makeSpanId() {
    return UUID.randomUUID()
               .toString()
               .replace("-", "")
               .substring(0, SPAN_ID_LENGTH);
  }

  /**
   * Creates a 32 character trace id.
   */
  public static String makeTraceId() {
    return UUID.randomUUID()
               .toString()
               .replace("-", "")
               .substring(0, TRACE_ID_LENGTH);
  }
}
