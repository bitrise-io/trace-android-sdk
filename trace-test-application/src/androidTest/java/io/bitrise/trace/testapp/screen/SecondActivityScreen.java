package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.ui.ParentFragment;
import io.bitrise.trace.testapp.ui.SecondActivity;
import io.bitrise.trace.testapp.ui.ThirdActivity;

/**
 * Screen class for {@link SecondActivity}.
 */
public class SecondActivityScreen extends BaseScreen {

    private static final UiSelector parentFragmentButton = new UiSelector()
            .text("Show fragment").className("android.widget.Button");
    private static final UiSelector thirdActivityButton =  new UiSelector()
            .text("Go to third activity").className("android.widget.Button");

    /**
     * Constructor for class. Waits till the view is displayed.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public SecondActivityScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    /**
     * Clicks on the second activity button and launches the {@link ThirdActivity}.
     *
     * @return the created {@link ThirdActivityScreen}.
     */
    @NonNull
    public ThirdActivityScreen launchThirdActivity() {
        click(thirdActivityButton);
        return new ThirdActivityScreen(uiDevice);
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

    }
}
