package io.bitrise.trace.data.management.formatter.crash;

import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.network.CrashRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides test data for the crash formatter tests.
 */
public class CrashFormatterTestProvider {


  static StackTraceElement[] createStackTraceElements() {
    final StackTraceElement[] list = new StackTraceElement[3];

    list[0] = new StackTraceElement("class1", "method1", "file1", 1);
    list[1] = new StackTraceElement("class2", "method2", "file2", 2);
    list[2] = new StackTraceElement("class3", "method3", "file3", 3);

    return list;
  }

  static List<CrashReport.Frame> createStackTraceFrames() {
    final List<CrashReport.Frame> frames = new ArrayList<>();
    frames.add(new CrashReport.Frame("class1", "method1", "file1", 1, 0));
    frames.add(new CrashReport.Frame("class2", "method2", "file2", 2, 1));
    frames.add(new CrashReport.Frame("class3", "method3", "file3", 3, 2));
    return frames;
  }

  static StackTraceElement[] createStackTraceElementsWithNulls() {
    final StackTraceElement[] list = new StackTraceElement[1];
    list[0] = new StackTraceElement("class", "method", null, 0);
    return list;
  }

  static List<CrashReport.Frame> createStackTraceFramesWithNulls() {
    final List<CrashReport.Frame> frames = new ArrayList<>();
    frames.add(new CrashReport.Frame("class", "method", "", 0, 0));
    return frames;
  }

  /**
   * Creates a test {@link CrashReport}.
   *
   * @return a test {@link CrashReport}.
   */
  public static CrashReport createCrashReport() {
    final List<CrashReport.Thread> threads = new ArrayList<>();
    threads.add(new CrashReport.Thread(1L, true, createStackTraceFrames()));
    threads.add(new CrashReport.Thread(12345L, false, createStackTraceFramesWithNulls()));

    return new CrashReport(threads, "throwable class name", "description",
        null);
  }
  
  /**
   * Create a test {@link CrashRequest.Metadata} object.
   *
   * @return a test metadata object.
   */
  public static CrashRequest.Metadata createTestMetadata() {
    return new CrashRequest.Metadata(
        "throwableClassName",
        "description",
        "timestamp",
        "uuid",
        "traceid",
        "spanid",
        "allExceptionNames"
    );
  }

  /**
   * Creates a different {@link CrashRequest.Metadata} object.
   *
   * @return a test metadata object.
   */
  public static CrashRequest.Metadata createDifferentTestMetadata() {
    return new CrashRequest.Metadata(
        "throwableClassName2",
        "description2",
        "timestamp2",
        "uuid2",
        "traceid2",
        "spanid2",
        "allExceptionNames2"
    );
  }

}
