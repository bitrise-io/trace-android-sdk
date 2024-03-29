package io.bitrise.trace.data.management.formatter.crash;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.CrashTestDataProvider;
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

  @Test
  public void convertStackTraceElementsToCrashReportFrame() {
    final List<CrashReport.Frame> frames = CrashDataFormatter
        .convertStackTraceElementsToCrashReportFrame(
            CrashTestDataProvider.createStackTraceElements());
    assertEquals(CrashTestDataProvider.createStackTraceFrames(), frames);
  }

  @Test
  public void convertStackTraceElementsToCrashReportFrame_withNulls() {
    final List<CrashReport.Frame> frames = CrashDataFormatter
        .convertStackTraceElementsToCrashReportFrame(
            CrashTestDataProvider.createStackTraceElementsWithNulls());
    assertEquals(CrashTestDataProvider.createStackTraceFramesWithNulls(), frames);
  }

  @Test
  public void formatCrashData() {
    final Map<Thread, StackTraceElement[]> allStackTraces = new HashMap<>();
    final Thread mockThread = Mockito.mock(Thread.class);
    allStackTraces.put(mockThread, CrashTestDataProvider.createStackTraceElements());
    final RuntimeException runtimeException = new RuntimeException();
    runtimeException.setStackTrace(CrashTestDataProvider.createStackTraceElements());

    final CrashData input = new CrashData(runtimeException, 0, allStackTraces);

    when(mockThread.getId()).thenReturn(12345L);

    final CrashReport result = CrashDataFormatter.formatCrashData(input);

    final List<CrashReport.Thread> expectedThreads = new ArrayList<>();
    expectedThreads.add(new CrashReport.Thread(0L, true,
        CrashTestDataProvider.createStackTraceFrames()));
    expectedThreads.add(new CrashReport.Thread(12345L, false,
        CrashTestDataProvider.createStackTraceFrames()));

    assertEquals(expectedThreads, result.getThreads());
    assertEquals("", result.getDescription());
    assertEquals("java.lang.RuntimeException", result.getThrowableClassName());
  }

}
