package io.bitrise.trace.testapp.network;

import org.junit.Test;

import io.bitrise.trace.testapp.BaseUiTest;
import io.bitrise.trace.testapp.screen.BaseNetworkActivityScreen;
import io.bitrise.trace.testapp.screen.IndexActivityScreen;
import io.bitrise.trace.testapp.screen.NetworkActivityScreen;

import static org.junit.Assert.assertEquals;

/**
 * UI tests related to OkHttp network calls.
 */
public class OkHttpUiTest extends BaseUiTest {

    /**
     * Checks that the app can make OkHttp network calls and gets the correct response codes.
     */
    @Test
    public void okHttpCallTest() {
        final IndexActivityScreen indexActivityScreen = new IndexActivityScreen(uiDevice);

        final NetworkActivityScreen networkActivityScreen = indexActivityScreen.launchNetworkTests();
        final BaseNetworkActivityScreen okHttpActivityScreen = networkActivityScreen.launchOkHttpActivity();
        okHttpActivityScreen.callHttp();
        okHttpActivityScreen.waitForCallFinish();
        assertEquals(okHttpActivityScreen.getResponseCodeText(), "301");
        okHttpActivityScreen.callHttps();
        okHttpActivityScreen.waitForCallFinish();
        assertEquals(okHttpActivityScreen.getResponseCodeText(), "200");
    }
}
