package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.IndexActivity;
import io.bitrise.trace.testapp.network.NetworkActivity;
import io.bitrise.trace.testapp.ui.MainActivity;

/**
 * Screen class for {@link IndexActivity}.
 */
public class IndexActivityScreen extends BaseScreen {

    private static final BySelector uiTestsButton = By.res(id + "btn_ui_tests");
    private static final BySelector networkTestsButton = By.res(id + "btn_network_tests");

    /**
     * Constructor for class.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public IndexActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the UI tests button and launches the {@link MainActivity}.
     *
     * @return the created {@link MainActivityScreen}.
     */
    @NonNull
    public MainActivityScreen launchUiTests() {
        click(uiTestsButton);
        return new MainActivityScreen(uiDevice);
    }

    /**
     * Clicks on the Network tests button and launches the {@link NetworkActivity}.
     *
     * @return the created {@link NetworkActivityScreen}.
     */
    @NonNull
    public NetworkActivityScreen launchNetworkTests() {
        click(networkTestsButton);
        return new NetworkActivityScreen(uiDevice);
    }

    @Override
    public void waitTillLoad() {
        uiDevice.wait(Until.findObject(uiTestsButton), DEFAULT_TIMEOUT);
        uiDevice.wait(Until.findObject(networkTestsButton), DEFAULT_TIMEOUT);
    }
}
