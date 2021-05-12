package io.bitrise.trace.testapp;

import org.junit.Test;

import io.bitrise.trace.testapp.screen.IndexActivityScreen;

import static org.junit.Assert.assertNotNull;

/**
 * UiTest for Application startup related behaviour.
 */
public class ApplicationStartUpUiTest extends BaseUiTest {

    /**
     * Tests when the app performs a cold start.
     */
    @Test
    public void coldStartTest() {
        final IndexActivityScreen indexActivityScreen = new IndexActivityScreen(uiDevice);
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);
        assertNotNull(indexActivityScreen.getButtonUiTestsLabel());
        uiDevice.pressHome();
    }

    /**
     * Tests when the app performs a hot start.
     */
    @Test
    public void hotStartTest() {
        final IndexActivityScreen indexActivityScreen = new IndexActivityScreen(uiDevice);
        assertNotNull(indexActivityScreen.getButtonUiTestsLabel());
        uiDevice.pressHome();
        uiDevice.waitForIdle(WAIT_FOR_IDLE_TIMEOUT);

        launchTraceTestApp(false);
    }
}
