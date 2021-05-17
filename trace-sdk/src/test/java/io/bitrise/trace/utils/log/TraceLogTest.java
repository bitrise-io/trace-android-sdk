package io.bitrise.trace.utils.log;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link TraceLog}.
 */
public class TraceLogTest {

  /**
   * Tears down the required objects after all the tests run.
   */
  @AfterClass
  public static void tearDownClass() {
    TraceLog.reset();
  }

  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUp() {
    TraceLog.reset();
  }

  @Test
  public void makeAndroidLogger() {
    TraceLog.makeAndroidLogger();
    assertTrue(TraceLog.getLogger() instanceof AndroidLogger);
  }

  @Test
  public void makeSilentLogger() {
    TraceLog.makeSilentLogger();
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  public void makeSilentLogger_alreadyAndroidLogger() {
    TraceLog.makeAndroidLogger();
    TraceLog.makeSilentLogger();
    assertTrue(TraceLog.getLogger() instanceof AndroidLogger);
  }

  @Test
  public void makeErrorOnlyLogger() {
    TraceLog.makeErrorOnlyLogger();
    assertTrue(TraceLog.getLogger() instanceof ErrorOnlyLogger);
  }

  @Test
  public void getLogger_notSet() {
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  public void getLogger_setAsAndroidLogger() {
    TraceLog.makeAndroidLogger();
    assertTrue(TraceLog.getLogger() instanceof AndroidLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void destroyLogger_changesType() {
    TraceLog.makeAndroidLogger();
    assertTrue(TraceLog.getLogger() instanceof AndroidLogger);

    TraceLog.reset();
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void d_shouldPrintOnce() {
    Logger mockLogger = Mockito.mock(Logger.class);
    TraceLog.logger = mockLogger;

    TraceLog.d("debug message");
    verify(mockLogger, times(1)).d(TraceLog.TAG, "debug message");
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void e_shouldPrintOnce() {
    Logger mockLogger = Mockito.mock(Logger.class);
    TraceLog.logger = mockLogger;

    TraceLog.e("error message");
    verify(mockLogger, times(1)).e(TraceLog.TAG, "error message");
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void i_shouldPrintOnce() {
    Logger mockLogger = Mockito.mock(Logger.class);
    TraceLog.logger = mockLogger;

    TraceLog.i("info message");
    verify(mockLogger, times(1)).i(TraceLog.TAG, "info message");
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void v_shouldPrintOnce() {
    Logger mockLogger = Mockito.mock(Logger.class);
    TraceLog.logger = mockLogger;

    TraceLog.v("verbose message");
    verify(mockLogger, times(1)).v(TraceLog.TAG, "verbose message");
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void w_shouldPrintOnce() {
    Logger mockLogger = Mockito.mock(Logger.class);
    TraceLog.logger = mockLogger;

    TraceLog.w("warning message");
    verify(mockLogger, times(1)).w(TraceLog.TAG, "warning message");
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void d_notInitialisedShouldBeSilent() {
    TraceLog.d("debug message");
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void e_notInitialisedShouldBeSilent() {
    TraceLog.e("error message");
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void i_notInitialisedShouldBeSilent() {
    TraceLog.i("info message");
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void w_notInitialisedShouldBeSilent() {
    TraceLog.w("warning message");
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }

  @Test
  @SuppressWarnings("checkstyle:MethodName")
  public void v_notInitialisedShouldBeSilent() {
    TraceLog.v("verbose message");
    assertTrue(TraceLog.getLogger() instanceof SilentLogger);
  }
}
