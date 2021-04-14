package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.ui.MainActivity;
import io.bitrise.trace.testapp.ui.ParentFragment;
import io.bitrise.trace.testapp.ui.SecondActivity;

/**
 * Screen class for {@link MainActivity}.
 */
public class MainActivityScreen extends BaseScreen {

    private static final BySelector parentFragmentButton = By.res(id + "fragment_parent_button");
    private static final BySelector secondActivityButton = By.res(id + "second_activity_button");

    /**
     * Constructor for class. Waits till the view is displayed.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public MainActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the second activity button and launches the {@link SecondActivity}.
     *
     * @return the created {@link SecondActivityScreen}.
     */
    @NonNull
    public SecondActivityScreen launchSecondActivity() {
        click(secondActivityButton);
        return new SecondActivityScreen(uiDevice);
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
        uiDevice.wait(Until.findObject(secondActivityButton), DEFAULT_TIMEOUT);
        uiDevice.wait(Until.findObject(parentFragmentButton), DEFAULT_TIMEOUT);
    }
}
