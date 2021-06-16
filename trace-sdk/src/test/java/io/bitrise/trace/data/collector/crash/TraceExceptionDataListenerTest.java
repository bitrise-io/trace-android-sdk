package io.bitrise.trace.data.collector.crash;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.management.DataManager;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TraceExceptionDataListener}.
 */
public class TraceExceptionDataListenerTest {

  final Thread mockThread = Mockito.mock(Thread.class);
  final Throwable throwable = new Throwable();
  final DataManager mockDataManager = Mockito.mock(DataManager.class);
  final Context mockContext = Mockito.mock(Context.class);
  final TraceExceptionDataListener listener = new TraceExceptionDataListener(mockContext);


  @Test
  public void uncaughtException_withPreviousHandler() {
    final TraceExceptionDataListener mockListener = Mockito.mock(TraceExceptionDataListener.class,
        Mockito.CALLS_REAL_METHODS);

    final Thread.UncaughtExceptionHandler previousHandler =
        Mockito.mock(Thread.UncaughtExceptionHandler.class);
    mockListener.setDataManager(mockDataManager);
    mockListener.previousHandler = previousHandler;

    mockListener.uncaughtException(mockThread, throwable);

    verify(mockListener, times(1))
        .onDataCollected(new CrashData(throwable, any()));
    verify(previousHandler, times(1))
        .uncaughtException(any(), any());
  }

  @Test
  public void uncaughtException_noPreviousHandler() {
    final TraceExceptionDataListener mockListener = Mockito.mock(TraceExceptionDataListener.class,
        Mockito.CALLS_REAL_METHODS);

    mockListener.setDataManager(mockDataManager);
    mockListener.previousHandler = null;

    mockListener.uncaughtException(mockThread, throwable);

    verify(mockListener, times(1))
        .onDataCollected(new CrashData(throwable, any()));
  }

  @Test
  public void startAndStopCollecting() {
    final TraceExceptionDataListener mockListener = Mockito.mock(TraceExceptionDataListener.class,
        Mockito.CALLS_REAL_METHODS);

    assertFalse(mockListener.isActive());

    mockListener.startCollecting();
    assertTrue(mockListener.isActive());

    mockListener.stopCollecting();
    assertFalse(mockListener.isActive());
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], listener.getPermissions());
  }

}
