package io.bitrise.trace.testapp;

import org.junit.Test;

import io.bitrise.trace.testapp.screen.IndexActivityScreen;

/**
 * UiTest for Application startup related behaviour.
 */
public class ApplicationStartUpUiTest extends BaseUiTest {

    /**
     * Tests when the app performs a cold start.
     */
    @Test
    public void coldStartTest() {
        new IndexActivityScreen(uiDevice);
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
        uiDevice.pressHome();
    }

    /**
     * Tests when the app performs a hot start.
     */
    @Test
    public void hotStartTest() {
        new IndexActivityScreen(uiDevice);

        uiDevice.pressHome();
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);

        launchTraceTestApp(false);
    }
}
