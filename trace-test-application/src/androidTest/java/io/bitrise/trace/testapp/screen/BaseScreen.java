package io.bitrise.trace.testapp.screen;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import java.util.ArrayList;

import io.bitrise.trace.testapp.BaseUiTest;

/**
 * Base Screen class, which should be the parent class for all Screen classes. Contains the common ports for them.
 * Class is used to follow the Page Object pattern.
 *
 * @see <a href="https://martinfowler.com/bliki/PageObject.html">https://martinfowler.com/bliki/PageObject.html</a>
 * @see
 * <a href="https://alexilyenko.github.io/uiautomator-page-object/">https://alexilyenko.github.io/uiautomator-page-object/</a>
 */
public abstract class BaseScreen {

    /**
     * The given UiDevice that runs the tests.
     */
    @NonNull
    protected final UiDevice uiDevice;

    /**
     * The id prefix to find resource by id.
     */
    @NonNull
    protected static final String id = InstrumentationRegistry.getInstrumentation()
                                                              .getTargetContext()
                                                              .getPackageName() + ":id/";

    /**
     * The default value for timeouts.
     */
    protected long DEFAULT_TIMEOUT = 10000;

    /**
     * The number of times the test will try to click on a given BySelector before failing the test.
     */
    private static final int numberOfClickAttempts = 3;

    /**
     * Constructor for class.
     *
     * @param uiDevice the UiDevice that launches the test cases.
     */
    public BaseScreen(@NonNull final UiDevice uiDevice) {
        this.uiDevice = uiDevice;
        waitTillLoad();
    }

    /**
     * Searches for the given {@link BySelector} on the active UI.
     *
     * @param by the given BySelector to search for.
     * @return the {@link UiObject2} if found, {@code null} otherwise.
     */
    @Nullable
    public UiObject2 find(@NonNull final BySelector by) {
        return uiDevice.wait(Until.findObject(by), DEFAULT_TIMEOUT);
    }

    /**
     * Performs a click action on the given {@link BySelector}. Attempts to find the given BySelector 3 times. This
     * is needed for cases when the UI is not ready.
     *
     * @param by the given BySelector.
     */
    public void click(@NonNull final BySelector by) {
        for (int i = 0; i < numberOfClickAttempts; i++) {
            final UiObject2 uiObject2 = find(by);
            if (uiObject2 != null) {
                uiObject2.click();
                break;
            } else {
                Log.i(BaseUiTest.UI_TEST_TAG, String.format("Could not find selector with name %s, retrying %s",
                        by.toString(), i));
            }
        }
    }

    /**
     * Gets the currently active Activity on the screen.
     *
     * @return the given Activity, or {@code null}, when no Activity found.
     */
    @Nullable
    public Activity getActivity() {
        final Activity[] activities = new Activity[1];
        InstrumentationRegistry.getInstrumentation()
                               .runOnMainSync(() -> activities[0] = new ArrayList<>(
                                       ActivityLifecycleMonitorRegistry.getInstance()
                                                                       .getActivitiesInStage(Stage.RESUMED)).get(0));
        return activities[0];
    }

    /**
     * Waits till the given screen is loaded.
     */
    public abstract void waitTillLoad();
}
