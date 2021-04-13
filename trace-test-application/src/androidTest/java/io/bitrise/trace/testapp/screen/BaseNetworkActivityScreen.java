package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.network.BaseNetworkActivity;

/**
 * Screen for {@link BaseNetworkActivity}.
 */
public class BaseNetworkActivityScreen extends BaseScreen {

    protected static final BySelector httpConnectButton = By.res(PACKAGE_NAME, "btn_connect_http");
    protected static final BySelector httpsConnectButton = By.res(PACKAGE_NAME, "btn_connect_https");
    protected static final BySelector responseBodyTextView = By.res(PACKAGE_NAME, "lbl_response_body");
    protected static final BySelector responseCodeTextView = By.res(PACKAGE_NAME, "lbl_response_status_code");

    /**
     * Constructor for class.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public BaseNetworkActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the HTTP connect button to do an HTTP call.
     */
    public void callHttp() {
        click(httpConnectButton);
    }

    /**
     * Clicks on the HTTPS connect button to do an HTTPS call.
     */
    public void callHttps() {
        click(httpsConnectButton);
    }

    /**
     * Waits for the call to be finished.
     */
    public void waitForCallFinish() {
        final UiObject2 httpsUiObject2 = uiDevice.wait(Until.findObject(httpConnectButton), DEFAULT_TIMEOUT);
        httpsUiObject2.wait(Until.enabled(true), DEFAULT_TIMEOUT);
        final UiObject2 httpUiObject2 = uiDevice.wait(Until.findObject(httpConnectButton), DEFAULT_TIMEOUT);
        httpUiObject2.wait(Until.enabled(true), DEFAULT_TIMEOUT);
    }

    /**
     * Gets the text from {@link #responseCodeTextView}, which contains the response code for the last network call.
     *
     * @return the response code as a String.
     */
    @NonNull
    public String getResponseCodeText() {
        return find(responseCodeTextView).getText();
    }

    @Override
    public void waitTillLoad() {
        uiDevice.wait(Until.findObject(httpConnectButton), DEFAULT_TIMEOUT);
        uiDevice.wait(Until.findObject(httpsConnectButton), DEFAULT_TIMEOUT);
        uiDevice.wait(Until.findObject(responseBodyTextView), DEFAULT_TIMEOUT);
        uiDevice.wait(Until.findObject(responseCodeTextView), DEFAULT_TIMEOUT);
    }
}
