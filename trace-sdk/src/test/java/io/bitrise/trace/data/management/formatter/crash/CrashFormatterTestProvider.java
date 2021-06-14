package io.bitrise.trace.data.management.formatter.crash;

/**
 * Provides test data for the crash formatter tests.
 */
public class CrashFormatterTestProvider {

  static final String expectedStringifiedStackTrace = "class1.method1(file1:1)\n"
      + "class2.method2(filename2:2)\n"
      + "class3.method3(filename3:3)";

  static StackTraceElement[] createStackTraceElements() {
    final StackTraceElement[] list = new StackTraceElement[3];

    list[0] = new StackTraceElement("class1", "method1", "file1", 1);
    list[1] = new StackTraceElement("class2", "method2", "filename2", 2);
    list[2] = new StackTraceElement("class3", "method3", "filename3", 3);

    return list;
  }

}
