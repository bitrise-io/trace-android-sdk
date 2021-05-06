package io.bitrise.trace.testapp;

import androidx.test.filters.SdkSuppress;

import org.junit.Test;

import io.bitrise.trace.testapp.screen.IndexActivityScreen;
import io.bitrise.trace.testapp.screen.MainActivityScreen;

import static org.junit.Assert.assertNotNull;

/**
 * UiTest for Activity related behaviour.
 */
// This test currently fails on sdk21 devices
@SdkSuppress(minSdkVersion = 22)
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
        assertNotNull(indexActivityScreen.getButtonUiTestsLabel());
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
