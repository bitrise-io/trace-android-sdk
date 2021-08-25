package io.bitrise.trace.data.management.formatter.crash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.dto.CrashReport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Formats {@link CrashData} objects into {@link CrashReport} objects.
 */
public class CrashDataFormatter {

  /**
   * Formats a {@link CrashData} object into a {@link CrashReport} object.
   *
   * @param crashData the original crash data.
   * @return the formatted {@link CrashReport} object.
   */
  public static CrashReport formatCrashData(final @NonNull CrashData crashData) {

    final List<CrashReport.Thread> threads = new ArrayList<>();

    // add thread that crashed
    final Throwable rootThrowable = findRootCause(crashData.getThrowable());
    final CrashReport.Thread initialThread =
        new CrashReport.Thread(crashData.getCrashedThreadId(), true,
        convertStackTraceElementsToCrashReportFrame(rootThrowable.getStackTrace()));
    threads.add(initialThread);

    // add all the other threads
    for (Map.Entry<Thread, StackTraceElement[]> entry : crashData.getAllStackTraces().entrySet()) {
      threads.add(new CrashReport.Thread(entry.getKey().getId(), false,
          convertStackTraceElementsToCrashReportFrame(entry.getValue())));
    }

    final String description = rootThrowable.getMessage();

    return new CrashReport(
        threads,
        rootThrowable.getClass().getName(),
        description == null ? "" : description,
        printAllExceptionNames(crashData.getThrowable()));
  }

  @NonNull
  private static Throwable findRootCause(@NonNull final Throwable throwable) {
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  @Nullable
  static String printAllExceptionNames(@NonNull final Throwable throwable) {
    // not a nested exception, so we ignore these
    if (throwable.getCause() == null) {
      return null;
    }

    // loop down all the exceptions
    final StringBuilder sb = new StringBuilder();
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null
        && rootCause.getCause() != rootCause) {
      sb.append(rootCause.getClass().getName());
      sb.append(", ");
      rootCause = rootCause.getCause();
    }

    sb.append(rootCause.getClass().getName());

    return sb.toString();
  }

  /**
   * Converts a list of {@link StackTraceElement} into {@link CrashReport.Frame}.
   *
   * @param stackTraceElements the initial list of stacktraces.
   * @return the list of CrashReport.Frame's.
   */
  @VisibleForTesting
  static List<CrashReport.Frame> convertStackTraceElementsToCrashReportFrame(
      final @NonNull StackTraceElement[] stackTraceElements) {
    List<CrashReport.Frame> frames = new ArrayList<>();
    int sequenceNumber = 0;
    for (StackTraceElement element : stackTraceElements) {
      frames.add(new CrashReport.Frame(
          element.getClassName(),
          element.getMethodName(),
          element.getFileName() == null ? "" : element.getFileName(),
          element.getLineNumber(),
          sequenceNumber));
      sequenceNumber++;
    }
    return frames;
  }

}
