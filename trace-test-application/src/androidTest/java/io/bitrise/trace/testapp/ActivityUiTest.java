package io.bitrise.trace.testapp;

import org.junit.Test;

import io.bitrise.trace.testapp.screen.IndexActivityScreen;
import io.bitrise.trace.testapp.screen.MainActivityScreen;

/**
 * UiTest for Activity related behaviour.
 */
public class ActivityUiTest extends BaseUiTest {

    /**
     * Tests Activity lifecycle states in different cases:
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
        launchTraceTestApp(false);

        mainActivityScreen.waitTillLoad();
        mainActivityScreen.launchSecondActivity();
        uiDevice.pressHome();
        launchTraceTestApp(false);

        uiDevice.pressBack();
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
        uiDevice.pressBack();
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
        uiDevice.pressBack();
    }
}
