package io.bitrise.trace.data.management.formatter.crash;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.dto.CrashReport;
import java.util.Map;

/**
 * Formats {@link CrashData} objects into {@link CrashReport} objects.
 */
public class ExceptionDataFormatter {

  /**
   * Formats a {@link CrashData} object into a {@link CrashReport} object.
   *
   * @param crashData the original crash data.
   * @return the formatted {@link CrashReport} object.
   */
  public static CrashReport formatCrashData(final @NonNull CrashData crashData) {

    final String throwable = StackTraceElementUtil
            .createStringifiedStackTrace(crashData.getThrowable().getStackTrace());
    final Map<String, String> threads =
        StackTraceElementUtil.createStringifiedReport(crashData.getAllStackTraces());

    return new CrashReport(throwable, threads);
  }
}
