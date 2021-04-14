package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import io.bitrise.trace.testapp.ui.ChildFragment;
import io.bitrise.trace.testapp.ui.ParentDeprecatedFragment;
import io.bitrise.trace.testapp.ui.ParentFragment;

/**
 * Screen class for {@link ParentFragment} and {@link ParentDeprecatedFragment}.
 */
public class ParentFragmentScreen extends BaseScreen {

    private static final UiSelector childFragmentButton = new UiSelector()
            .text("Show child fragment").className("android.widget.Button");

    /**
     * Constructor for class. Waits till the view is displayed.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public ParentFragmentScreen(@NonNull final UiDevice uiDevice) {
        super(uiDevice);
    }

    @Override
    public void waitTillLoad() {

    }

    /**
     * Clicks on the parent fragment button and shows the {@link ChildFragment}.
     *
     * @return the created {@link ChildFragmentScreen}.
     */
    @NonNull
    public ChildFragmentScreen showChildFragment() {
        click(childFragmentButton);
        return new ChildFragmentScreen(uiDevice);
    }
}
