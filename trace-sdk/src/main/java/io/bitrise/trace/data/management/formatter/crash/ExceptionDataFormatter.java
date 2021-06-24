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
public class ExceptionDataFormatter {

  /**
   * Formats a {@link CrashData} object into a {@link CrashReport} object.
   *
   * @param crashData the original crash data.
   * @return the formatted {@link CrashReport} object.
   */
  public static CrashReport formatCrashData(final @NonNull CrashData crashData) {

    final List<CrashReport.Thread> threads = new ArrayList<>();

    // add thread that crashed
    final CrashReport.Thread initialThread =
        new CrashReport.Thread(crashData.getCrashedThreadId(), true,
        convertStackTraceElementsToCrashReportFrame(crashData.getThrowable().getStackTrace()));
    threads.add(initialThread);

     // add all the other threads
    for (Map.Entry<Thread, StackTraceElement[]> entry : crashData.getAllStackTraces().entrySet()) {
      threads.add(new CrashReport.Thread(entry.getKey().getId(), false,
          convertStackTraceElementsToCrashReportFrame(entry.getValue())));
    }

    final String title = getCrashTitle(initialThread.getFirstFrame());
    final String description = crashData.getThrowable().getMessage();

    return new CrashReport(threads,
        title == null ? "" : title,
        description == null ? "" : description);
  }

  @VisibleForTesting
  @Nullable
  static String getCrashTitle(final CrashReport.Frame initialFrame) {
    if (initialFrame == null) {
      return null;
    }
    return initialFrame.getPackageName() + "." + initialFrame.getFunctionName() + "("
        + initialFrame.getFileName() + ":" + initialFrame.getLineNo() + ")";
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
      sequenceNumber ++;
    }
    return frames;
  }

}
