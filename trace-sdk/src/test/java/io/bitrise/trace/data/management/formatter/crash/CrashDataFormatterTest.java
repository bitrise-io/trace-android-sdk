package io.bitrise.trace.data.management.formatter.crash;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.dto.CrashReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link CrashDataFormatter}.
 */
public class CrashDataFormatterTest {

  private static final String exceptionClassName = "ExceptionClassName";

  @Test
  public void getCrashTitle() {
    final CrashReport.Frame frame = new CrashReport.Frame(
        "package", "function", "file", 123, 0);

    assertEquals("ExceptionClassName package.function(file:123)",
        CrashDataFormatter.getCrashTitle(frame, exceptionClassName));
  }

  @Test
  public void getCrashTitle_frameIsNull() {
    assertEquals(exceptionClassName,
        CrashDataFormatter.getCrashTitle(null, exceptionClassName));
  }

  @Test
  public void convertStackTraceElementsToCrashReportFrame() {
    final List<CrashReport.Frame> frames = CrashDataFormatter
        .convertStackTraceElementsToCrashReportFrame(
            CrashFormatterTestProvider.createStackTraceElements());
    assertEquals(CrashFormatterTestProvider.createStackTraceFrames(), frames);
  }

  @Test
  public void convertStackTraceElementsToCrashReportFrame_withNulls() {
    final List<CrashReport.Frame> frames = CrashDataFormatter
        .convertStackTraceElementsToCrashReportFrame(
            CrashFormatterTestProvider.createStackTraceElementsWithNulls());
    assertEquals(CrashFormatterTestProvider.createStackTraceFramesWithNulls(), frames);
  }

  @Test
  public void formatCrashData() {
    final Map<Thread, StackTraceElement[]> allStackTraces = new HashMap<>();
    final Thread mockThread = Mockito.mock(Thread.class);
    allStackTraces.put(mockThread, CrashFormatterTestProvider.createStackTraceElements());
    final RuntimeException runtimeException = new RuntimeException();
    runtimeException.setStackTrace(CrashFormatterTestProvider.createStackTraceElements());

    final CrashData input = new CrashData(runtimeException, 0, allStackTraces);

    when(mockThread.getId()).thenReturn(12345L);

    final CrashReport result = CrashDataFormatter.formatCrashData(input);

    final List<CrashReport.Thread> expectedThreads = new ArrayList<>();
    expectedThreads.add(new CrashReport.Thread(0L, true,
        CrashFormatterTestProvider.createStackTraceFrames()));
    expectedThreads.add(new CrashReport.Thread(12345L, false,
        CrashFormatterTestProvider.createStackTraceFrames()));

    assertEquals(expectedThreads, result.getThreads());
    assertEquals("", result.getDescription());
    assertEquals("java.lang.RuntimeException class1.method1(file1:1)", result.getTitle());
  }

}
