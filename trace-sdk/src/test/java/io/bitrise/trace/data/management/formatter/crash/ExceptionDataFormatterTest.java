package io.bitrise.trace.data.management.formatter.crash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.dto.CrashReport;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ExceptionDataFormatter}.
 */
public class ExceptionDataFormatterTest {

  @Test
  public void formatCrashData() {
    final Map<Thread, StackTraceElement[]> allStackTraces = new HashMap<>();
    final Thread mockThread = Mockito.mock(Thread.class);
    allStackTraces.put(mockThread, CrashFormatterTestProvider.createStackTraceElements());
    final Throwable mockThrowable = Mockito.mock(Throwable.class);
    final CrashData input = new CrashData(mockThrowable, allStackTraces);

    when(mockThread.getId()).thenReturn(12345L);
    when(mockThrowable.getStackTrace())
        .thenReturn(CrashFormatterTestProvider.createStackTraceElements());

    final CrashReport result = ExceptionDataFormatter.formatCrashData(input);

    assertNotNull(result);

    final Map<String, String> expectedThreads = new HashMap<>();
    expectedThreads.put("12345", CrashFormatterTestProvider.expectedStringifiedStackTrace);

    assertEquals(result.getThreads(), expectedThreads);
    assertEquals(result.getThrowable(), CrashFormatterTestProvider.expectedStringifiedStackTrace);
  }



}
