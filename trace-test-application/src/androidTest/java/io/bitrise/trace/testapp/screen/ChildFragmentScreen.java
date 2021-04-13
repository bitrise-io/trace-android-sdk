package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.ui.ChildDeprecatedFragment;
import io.bitrise.trace.testapp.ui.ChildFragment;

/**
 * Screen class for {@link ChildFragment} and {@link ChildDeprecatedFragment}.
 */
public class ChildFragmentScreen extends BaseScreen {

    private static final BySelector childFragmentText = By.res(PACKAGE_NAME, "child_fragment_text");

    /**
     * Constructor for class. Waits till the view is displayed.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public ChildFragmentScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    @Override
    public void waitTillLoad() {
        uiDevice.wait(Until.findObject(childFragmentText), DEFAULT_TIMEOUT);
    }
}
