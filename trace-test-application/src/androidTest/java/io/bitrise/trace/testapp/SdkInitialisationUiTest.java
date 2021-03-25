package io.bitrise.trace.testapp;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * UI automated test that checks the SDK initialisation.
 */
@RunWith(AndroidJUnit4.class)
public class SdkInitialisationUiTest {

    private static final int LAUNCH_TIMEOUT = 30000;

    /**
     * Starts up the Application from the home screen before every test.
     */
    @Before
    public void startMainActivityFromHomeScreen() {
        final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();

        final String launcherPackage = device.getLauncherPackageName();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        final Context context = ApplicationProvider.getApplicationContext();
        final Intent intent =
                context.getPackageManager().getLaunchIntentForPackage(this.getClass().getPackage().getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Checks that on Application launch the initialisation of the Trace SDK should be called at least once. Reads the
     * logcat output and ensures the initialisation log message can be found.
     *
     * @throws IOException if any I/O error occurs.
     */
    @Test
    public void shouldInit() throws IOException {
        final boolean actualValue = checkForTraceInitInLogcatContent();
        assertThat(actualValue, is(true));
    }

    /**
     * Checks if the content of the Logcat till the first Activity of the Application is started, that it contains
     * the logging of the initialisation of the SDK.
     *
     * @return {@code true} if found, {@code false} otherwise.
     * @throws IOException if any I/O error occurs.
     */
    private boolean checkForTraceInitInLogcatContent() throws IOException {
        final InputStream inputStream = Runtime.getRuntime().exec("logcat -d").getInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder logcatContent = new StringBuilder();

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                throw new IllegalStateException("Could not find logcat entry, timeout happened!");
            }
        };
        final Timer timer = new Timer("Timeout");
        timer.schedule(timerTask, 45000);

        String logcatEntry;
        while ((logcatEntry = bufferedReader.readLine()) != null && !logcatEntry.contains("TraceSdk: Initialising the" +
                " SDK)")) {
            logcatContent.append(logcatEntry);
        }

        timer.cancel();
        return true;
    }
}
