package io.bitrise.trace.testapp;

import static org.junit.Assert.assertTrue;

import androidx.test.filters.SdkSuppress;
import io.bitrise.trace.testapp.screen.IndexActivityScreen;
import io.bitrise.trace.testapp.screen.MainActivityScreen;
import org.junit.Test;

/**
 * UiTest for Activity related behaviour.
 */
// This test currently fails on sdk21 devices
@SdkSuppress(minSdkVersion = 22)
public class ActivityUiTest extends BaseUiTest {

  /**
   * Tests Activity lifecycle states in different cases.
   * <ul>
   *     <li>Cold application start</li>
   *     <li>Hot application start</li>
   *     <li>Launching up new Activity</li>
   * </ul>
   */
  @Test
  public void activityStateTest() {
    final IndexActivityScreen indexActivityScreen = new IndexActivityScreen(uiDevice);
    final MainActivityScreen mainActivityScreen = indexActivityScreen.launchUiTests();
    uiDevice.pressHome();
    final boolean launchSuccessful1 = launchTraceTestApp(false);
    assertTrue(launchSuccessful1);

    mainActivityScreen.waitTillLoad();
    mainActivityScreen.launchSecondActivity();
    uiDevice.pressHome();
    final boolean launchSuccessful2 = launchTraceTestApp(false);
    assertTrue(launchSuccessful2);

    uiDevice.pressBack();
    uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
    uiDevice.pressBack();
    uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
    uiDevice.pressBack();
  }
}
