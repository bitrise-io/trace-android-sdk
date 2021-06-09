package io.bitrise.trace.data.management.formatter.crash;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StackTraceElementUtil}.
 */
public class StackTraceElementUtilTest {


  @Test
  public void createStringifiedReport() {
    final Map<Thread, StackTraceElement[]> input = new HashMap<>();
    final Thread mockThread = Mockito.mock(Thread.class);
    input.put(mockThread, CrashFormatterTestProvider.createStackTraceElements());

    when(mockThread.getId()).thenReturn(12345L);

    final Map<String, String> expectedResult = new HashMap<>();
    expectedResult.put("12345", CrashFormatterTestProvider.expectedStringifiedStackTrace);

    assertEquals(expectedResult, StackTraceElementUtil.createStringifiedReport(input));
  }

  @Test
  public void createStringifiedStackTrace_withEmptyStackTraces() {
    final String result = StackTraceElementUtil
        .createStringifiedStackTrace(new StackTraceElement[0]);
    assertEquals("", result);
  }

  @Test
  public void createStringifiedStackTrace_withStackTraces() {
    final String result = StackTraceElementUtil
        .createStringifiedStackTrace(CrashFormatterTestProvider.createStackTraceElements());
    assertEquals(CrashFormatterTestProvider.expectedStringifiedStackTrace, result);
  }

}
