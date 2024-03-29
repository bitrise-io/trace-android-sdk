package io.bitrise.trace.data.collector.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.content.Context;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ApplicationForegroundStateDataListener}.
 */
public class ApplicationForegroundStateDataListenerTest {

  private final Context mockContext = Mockito.mock(Context.class);
  private final ApplicationForegroundStateDataListener listener = new
      ApplicationForegroundStateDataListener(mockContext);
  private final Activity mockActivity = Mockito.mock(Activity.class);

  @Test
  public void onActivityStarted_notActive() {
    listener.onActivityStarted(mockActivity);
    assertFalse(listener.isActive());
  }

  @Test
  public void onActivityStopped_notActive() {
    listener.onActivityStopped(mockActivity);
    assertFalse(listener.isActive());
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], listener.getPermissions());
  }
}
