package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.network.NetworkActivity;
import io.bitrise.trace.testapp.network.OkHttpActivity;

/**
 * Screen class for {@link NetworkActivity}
 */
public class NetworkActivityScreen extends BaseScreen {

    private static final UiSelector okHttpButton = new UiSelector()
            .text("okhttp tests").className("android.widget.Button");
    private static final UiSelector urlConnectionButton = new UiSelector()
            .text("URLConnection tests").className("android.widget.Button");

    /**
     * Constructor for class.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public NetworkActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the OkHttp tests button and launches the {@link OkHttpActivity}.
     *
     * @return the created {@link BaseNetworkActivityScreen}.
     */
    @NonNull
    public BaseNetworkActivityScreen launchOkHttpActivity() {
        click(okHttpButton);
        return new BaseNetworkActivityScreen(uiDevice);
    }

    @Override
    public void waitTillLoad() {

    }
}
