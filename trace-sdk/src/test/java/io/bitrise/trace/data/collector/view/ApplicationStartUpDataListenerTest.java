package io.bitrise.trace.data.collector.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ApplicationStartUpDataListener}.
 */
public class ApplicationStartUpDataListenerTest {

  final Context mockContext = Mockito.mock(Context.class);
  final ApplicationForegroundStateDataListener applicationForegroundStateDataListener =
      new ApplicationForegroundStateDataListener(mockContext);
  final Activity mockActivity = Mockito.mock(Activity.class);

  ApplicationStartUpDataListener listener = new ApplicationStartUpDataListener(
      mockContext, applicationForegroundStateDataListener);

  @Test
  public void onActivityCreated_notActive() {
    listener.onActivityCreated(mockActivity, null);
    assertFalse(listener.isActive());
    assertEquals(listener.onCreateCounter.get(), 0);
  }

  @Test
  public void onActivityStarted_notActive() {
    listener.onActivityStarted(mockActivity);
    assertFalse(listener.isActive());
    assertNull(listener.start);
  }

  @Test
  public void onActivityResumed_notActive() {
    listener.onActivityResumed(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.onResumeCounter.get(), 0);
  }

  @Test
  public void onActivityPaused_notActive() {
    final Long start = 12345L;
    listener.start = start;
    listener.onActivityPaused(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.start, start);
  }

  @Test
  public void onActivityStopped_noInteractions() {
    final ApplicationStartUpDataListener mockListener =
        Mockito.mock(ApplicationStartUpDataListener.class);
    mockListener.onActivityStopped(mockActivity);
    verify(mockListener, times(1)).onActivityStopped(mockActivity);
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void onActivitySaveInstanceState_noInteractions() {
    final ApplicationStartUpDataListener mockListener =
        Mockito.mock(ApplicationStartUpDataListener.class);
    final Bundle mockBundle = Mockito.mock(Bundle.class);
    mockListener.onActivitySaveInstanceState(mockActivity, mockBundle);
    verify(mockListener, times(1))
        .onActivitySaveInstanceState(mockActivity, mockBundle);
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void onActivityDestroyed_noInteractions() {
    final ApplicationStartUpDataListener mockListener =
        Mockito.mock(ApplicationStartUpDataListener.class);
    mockListener.onActivityDestroyed(mockActivity);
    verify(mockListener, times(1)).onActivityDestroyed(mockActivity);
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], listener.getPermissions());
  }

}
