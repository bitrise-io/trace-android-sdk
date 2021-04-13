package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.ui.ParentFragment;
import io.bitrise.trace.testapp.ui.ThirdActivity;

/**
 * Screen class for {@link ThirdActivity}.
 */
public class ThirdActivityScreen extends BaseScreen {

    private static final BySelector parentFragmentButton = By.res(PACKAGE_NAME, "fragment_parent_button");
    private static final BySelector thirdActivityTextView = By.res(PACKAGE_NAME, "hello_third_textview");

    /**
     * Constructor for class. Waits till the view is displayed.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public ThirdActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the parent fragment button and shows the {@link ParentFragment}.
     *
     * @return the created {@link ParentFragmentScreen}.
     */
    @NonNull
    public ParentFragmentScreen showParentFragment() {
        click(parentFragmentButton);
        return new ParentFragmentScreen(uiDevice);
    }

    @Override
    public void waitTillLoad() {
        uiDevice.wait(Until.findObject(thirdActivityTextView), DEFAULT_TIMEOUT);
    }
}
