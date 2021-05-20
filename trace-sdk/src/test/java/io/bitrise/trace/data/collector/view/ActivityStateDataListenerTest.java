package io.bitrise.trace.data.collector.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.content.Context;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ActivityStateDataListener}.
 */
public class ActivityStateDataListenerTest {

  final Context mockContext = Mockito.mock(Context.class);
  final ActivityStateDataListener listener = new ActivityStateDataListener(mockContext);
  final Activity mockActivity = Mockito.mock(Activity.class);

  @Test
  public void onActivityCreated_notActive() {
    listener.onActivityCreated(mockActivity, null);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void onActivityStarted_notActive() {
    listener.onActivityStarted(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void onActivityStopped_notActive() {
    listener.onActivityStopped(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], listener.getPermissions());
  }
}
