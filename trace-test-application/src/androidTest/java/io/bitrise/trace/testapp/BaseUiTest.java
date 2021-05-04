package io.bitrise.trace.testapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

/**
 * Base class for UI tests. Has the commonly used methods and members.
 * <p>
 * Some methods are based on Google sample. These are:
 *  <ul>
 *      <li>{@link #getLauncherPackageName()}</li>
 *      <li>{@link #launchTraceTestApp(boolean)}</li>
 *  </ul>
 * <p>
 *  @see
 * <a href="https://github.com/android/testing-samples/blob/master/ui/uiautomator/BasicSample/app/src/androidTest/java/com/example/android/testing/uiautomator/BasicSample/ChangeTextBehaviorTest.java">https://github.com/android/testing-samples/blob/master/ui/uiautomator/BasicSample/app/src/androidTest/java/com/example/android/testing/uiautomator/BasicSample/ChangeTextBehaviorTest.java</a>
 */
public abstract class BaseUiTest {

    protected static final String PACKAGE_NAME = "io.bitrise.trace.testapp";
    protected static final int WAIT_FOR_IDLE_TIMEOUT = 10000;
    protected static final int SCREENSHOT_COMPRESSION = 100;
    protected static UiDevice uiDevice;
    public static final String UI_TEST_TAG = "Trace UI Test";
    private static final int LAUNCH_TIMEOUT = 15000;

    /**
     * Rule for collecting data for every test case.
     */
    @Rule
    public TestDataCollectionRule testDataCollectionRule = new TestDataCollectionRule();

    /**
     * Rule for the name of the test. Used to determine the name of the screenshot for the given test case.
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * Rule to grant the WRITE_EXTERNAL_STORAGE permission. Required to be able to store screenshots, when the API
     * level is lower than 29.
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        Log.i(UI_TEST_TAG, "Starting UI tests");
        uiDevice = UiDevice.getInstance(getInstrumentation());
        uiDevice.pressHome();
        registerANRWatcher();
    }

    @AfterClass
    public static void tearDownClass() {
        Log.i(UI_TEST_TAG, "Finished UI tests");
    }

    @Before
    public void setup() {
        Log.i(UI_TEST_TAG, "Launching test " + testName.getMethodName());
        assertThat(launchTraceTestApp(true), not(false));
    }

    @After
    public void tearDown() {
        Log.i(UI_TEST_TAG, "Finished test " + testName.getMethodName());
    }

    /**
     * Launches the test application.
     *
     * @param clear if any previous instance of the application should be cleared or not.
     * @return The final result returned by the {@code condition}, or {@code null} if the {@code condition} was not met
     * before the {@code timeout}.
     */
    @Nullable
    protected Boolean launchTraceTestApp(final boolean clear) {
        final String launcherPackage = getLauncherPackageName();
        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        final Context context = getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (clear) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);

        final boolean result = uiDevice.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
        takeScreenShot(TestEvent.LAUNCHED);
        return result;
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package is
     * "com.android.launcher" but can be different at times. This is a generic solution which works on all platforms.`
     *
     * @return the package name.
     */
    @NonNull
    private String getLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        final PackageManager packageManager = getApplicationContext().getPackageManager();
        final ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * Takes and stores a screenshot of the device.
     *
     * @param testEvent a {@link TestEvent} value to postfix the tested method.
     */
    protected void takeScreenShot(@NonNull final TestEvent testEvent) {
        final Bitmap screenshotBitmap = getInstrumentation().getUiAutomation().takeScreenshot();
        final String screenShotFileName = getTestReportFileBaseName(testEvent);

        storeScreenshot(screenshotBitmap, screenShotFileName);
    }

    /**
     * Gets the base name for collected test data files, which is the current time concatenated with the name of the
     * test and the {@link TestEvent}.
     *
     * @param testEvent the current TestEvent.
     * @return the base name for test data files.
     */
    @NonNull
    private String getTestReportFileBaseName(@NonNull final TestEvent testEvent) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        final String timeStamp = simpleDateFormat.format(Calendar.getInstance().getTime());
        return timeStamp + "_" + testName.getMethodName() + "_" + testEvent;
    }

    /**
     * Stores a screenshot of of the device and will store it in /sdcard/Pictures/UiTestScreenShots/.
     *
     * @param screenShotBitmap   the Bitmap of the screenshot.
     * @param screenShotFileName the name of the screenshot file.
     */
    private void storeScreenshot(@NonNull final Bitmap screenShotBitmap, @NonNull final String screenShotFileName) {
        final ContentResolver contentResolver =
                InstrumentationRegistry.getInstrumentation()
                                       .getTargetContext()
                                       .getApplicationContext()
                                       .getContentResolver();

        final String UiTestScreenShotsDirName = "UiTestScreenShots";

        try {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                storeWithMediaStore(new ContentValues(), contentResolver, screenShotFileName,
                        UiTestScreenShotsDirName, screenShotBitmap);
            } else {
                storeWithFileOutputStream(new ContentValues(), contentResolver, screenShotFileName,
                        UiTestScreenShotsDirName, screenShotBitmap);
            }
            Log.i(UI_TEST_TAG, "Created screenshot " + screenShotFileName);
        } catch (final IOException e) {
            Log.e(UI_TEST_TAG, "Failed to take screenshot!", e);
            e.printStackTrace();
        }
    }

    /**
     * Stores the given screenshot with the MediaStore.
     *
     * @param contentValues      the ContentValues which will be inserted to the MediaStore.
     * @param contentResolver    the ContentResolver to insert it to the MediaStore.
     * @param screenshotFileName the name of the screenshot file.
     * @param screenshotLocation the subdirectory for the screenshot.
     * @param screenshotBitmap   the Bitmap of the screenshot.
     * @throws IOException if the target File cannot be created, or the used OutputStream cannot be closed.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private void storeWithMediaStore(@NonNull final ContentValues contentValues,
                                     @NonNull final ContentResolver contentResolver,
                                     @NonNull final String screenshotFileName,
                                     @NonNull final String screenshotLocation,
                                     @NonNull final Bitmap screenshotBitmap) throws IOException {
        applyBaseContentValues(contentValues);
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, screenshotFileName + ".jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/" + screenshotLocation);

        final Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (final OutputStream outputStream = contentResolver.openOutputStream(uri)) {
                saveScreenshotToStream(screenshotBitmap, outputStream);
            }
            contentResolver.update(uri, contentValues, null, null);
        }
    }

    /**
     * Stores the given screenshot with regular file access, and inserts the image to the MediaStore.
     *
     * @param contentValues      the ContentValues which will be inserted to the MediaStore.
     * @param contentResolver    the ContentResolver to insert it to the MediaStore.
     * @param screenshotFileName the name of the screenshot file.
     * @param screenshotLocation the subdirectory for the screenshot.
     * @param screenshotBitmap   the Bitmap of the screenshot.
     * @throws IOException if the target File cannot be created, or the used OutputStream cannot be closed.
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void storeWithFileOutputStream(@NonNull final ContentValues contentValues,
                                           @NonNull final ContentResolver contentResolver,
                                           @NonNull final String screenshotFileName,
                                           @NonNull final String screenshotLocation,
                                           @NonNull final Bitmap screenshotBitmap) throws IOException {
        final File picturesDir = new File("/sdcard/Pictures/" + screenshotLocation);
        final File screenshotFile = new File(picturesDir, screenshotFileName + ".jpg");
        screenshotFile.mkdirs();
        if (screenshotFile.exists()) {
            screenshotFile.delete();
        }

        try (final FileOutputStream outputStream = new FileOutputStream(screenshotFile)) {
            saveScreenshotToStream(screenshotBitmap, outputStream);
        }

        applyBaseContentValues(contentValues);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }


    /**
     * Compresses the bitmap object to a .jpeg image format using the specified OutputStream of bytes.
     *
     * @param screenshotBitmap the Bitmap of the screenshot.
     * @param outputStream     the given OutputStream.
     */
    private void saveScreenshotToStream(@NonNull final Bitmap screenshotBitmap,
                                        @NonNull final OutputStream outputStream) {
        screenshotBitmap.compress(Bitmap.CompressFormat.JPEG, SCREENSHOT_COMPRESSION, outputStream);
    }

    /**
     * Applies the mime type and the date on the given ContentValues.
     *
     * @param contentValues the ContentValues to update.
     * @return the updated ContentValues.
     */
    @NonNull
    private ContentValues applyBaseContentValues(@NonNull final ContentValues contentValues) {
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return contentValues;
    }

    /**
     * Registers a watcher that will looks for ANR dialogs, and will tries to wait for the given apps.
     */
    private static void registerANRWatcher() {
        uiDevice.registerWatcher("ANR", () -> {
            UiObject anrDialog = uiDevice.findObject(
                    new UiSelector().className("com.android.server.am.AppNotRespondingDialog"));
            if (!anrDialog.exists()) {
                anrDialog = uiDevice.findObject(new UiSelector()
                        .packageName("android")
                        .textContains("isn't responding."));
            }
            return clickWaitForAnrDialog(anrDialog);
        });
    }

    /**
     * Tries to press the button to wait for the app that is not responding.
     *
     * @param anrDialog the given ANR dialog.
     * @return {@code true} if there was an ANR dialog, {@code false} otherwise.
     */
    private static boolean clickWaitForAnrDialog(@NonNull final UiObject anrDialog) {
        if (anrDialog.exists()) {
            try {
                anrDialog.getChild(new UiSelector().text("Wait")).click();
            } catch (final UiObjectNotFoundException e) {
                Log.i(UI_TEST_TAG, "Detected ANR, but window disappeared!");
            }
            return true;
        }
        return false;
    }

    /**
     * Dumps the view hierarchy to an xml file.
     *
     * @param testEvent the current {@link TestEvent}.
     */
    private void dumpWindowHierarchy(@NonNull final TestEvent testEvent) {
        try {
            uiDevice.dumpWindowHierarchy(createViewHierarchyFile(testEvent));
        } catch (IOException e) {
            Log.i(UI_TEST_TAG, "Failed to dump view hierarchy.", e);
            e.printStackTrace();
        }
    }

    /**
     * Creates a File to store the view hierarchy.
     *
     * @param testEvent the current {@link TestEvent}.
     * @return the created File.
     */
    @NonNull
    private File createViewHierarchyFile(@NonNull final TestEvent testEvent) {
        final String hierarchyReportFileName = getTestReportFileBaseName(testEvent) + "_hierarchy.xml";
        final File hierarchyDump = new File("/sdcard/Documents/UiTestHierarchy", hierarchyReportFileName);
        hierarchyDump.mkdirs();
        if (hierarchyDump.exists()) {
            hierarchyDump.delete();
        }

        return hierarchyDump;
    }

    /**
     * Event to identify the state of the test.
     */
    public enum TestEvent {
        START,
        LAUNCHED,
        FAIL,
    }

    /**
     * Inner class for creating Rules for collecting test data.
     */
    public class TestDataCollectionRule extends TestWatcher {

        @Override
        protected void starting(@NonNull final Description description) {
            super.starting(description);
            Log.i(UI_TEST_TAG, "Test started: " + testName);
            takeScreenShot(TestEvent.START);
            dumpWindowHierarchy(TestEvent.START);
        }

        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            Log.i(UI_TEST_TAG, "Test success: " + testName);
        }

        @Override
        protected void failed(@NonNull final Throwable e, @NonNull final Description description) {
            super.failed(e, description);
            Log.i(UI_TEST_TAG, "Test failed: " + testName);
            takeScreenShot(TestEvent.FAIL);
            dumpWindowHierarchy(TestEvent.FAIL);
        }
    }
}
