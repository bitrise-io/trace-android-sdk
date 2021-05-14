package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.management.DataFormatterDelegator;
import io.bitrise.trace.utils.StringUtils;

/**
 * Constant values used mainly by {@link DataFormatterDelegator} to add names and values to
 * different {@link io.opencensus.proto.metrics.v1.Metric}s,
 * {@link io.opencensus.proto.resource.v1.Resource}s and Spans.
 */
public final class DataValues {

  public static final String app = "app";
  public static final String br = "br";
  public static final String build = "build";
  public static final String bytes = "bytes";
  public static final String carrier = "carrier";
  public static final String cpu = "cpu";
  public static final String device = "device";
  public static final String http = "http";
  public static final String id = "id";
  public static final String idle = "idle";
  public static final String ioWait = "ioWait";
  public static final String irq = "irq";
  public static final String key = "key";
  public static final String latency = "latency";
  public static final String locale = "locale";
  public static final String memory = "memory";
  public static final String method = "method";
  public static final String ms = "ms";
  public static final String network = "network";
  public static final String nice = "nice";
  public static final String os = "os";
  public static final String pct = "pct";
  public static final String percent = "percent";
  public static final String platform = "platform";
  public static final String process = "process";
  public static final String res = "res";
  public static final String rooted = "rooted";
  public static final String session = "session";
  public static final String softIrq = "softIrq";
  public static final String start = "start";
  public static final String startup = "startup";
  public static final String state = "state";
  public static final String status_code = "status_code";
  public static final String steal = "steal";
  public static final String system = "system";
  public static final String type = "type";
  public static final String url = "url";
  public static final String user = "user";
  public static final String version = "version";

  private DataValues() {
    throw new UnsupportedOperationException("Private constructor for class.");
  }

  /**
   * Joins a name from the String entries. Adds "." as a delimiter.
   *
   * @param names the names to join.
   * @return the joined String.
   */
  @NonNull
  public static String getName(@NonNull final String... names) {
    if (names.length == 0) {
      throw new IllegalArgumentException("At least one value for name should be provided.");
    }
    return StringUtils.join(names, ".");
  }
}
