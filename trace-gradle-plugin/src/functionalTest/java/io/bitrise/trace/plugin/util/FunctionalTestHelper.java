package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;

import org.apache.log4j.Logger;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import io.bitrise.trace.plugin.TraceConstants;

/**
 * Helper class to support {@link io.bitrise.trace.plugin.TraceGradlePluginFunctionalTest}
 */
public class FunctionalTestHelper {

    /**
     * A temporary folder for running functional tests. A new gradle project will be setup in this folder, and the
     * tests will run in this project.
     */
    private static final File testProjectDir =
            new File(System.getProperty("user.home") + "/BitriseTrace-TestGradleProject");
    @NonNull
    private final Logger logger;

    public FunctionalTestHelper() {
        this.logger = Logger.getRootLogger();
    }

    /**
     * Publishes the trace-gradle-plugin itself and logs it.
     */
    public void publishTraceGradlePlugin() {
        logger.info("Publishing plugin, to make sure it is up-to-date");
        FunctionalTestUtils.publishTraceGradlePlugin();
        logger.info("Publishing plugin finished");
    }

    /**
     * Gets the name of the current test's directory.
     *
     * @param testName the name of the test.
     * @return the name of the test's directory.
     */
    @NonNull
    public String getTestDirName(@NonNull final TestName testName) {
        return testProjectDir.getAbsolutePath() + "/" + testName.getMethodName() + "/";
    }

    /**
     * Gets the the current test's directory.
     *
     * @param testName the name of the test.
     * @return the test's directory.
     */
    @NonNull
    public File getTestDir(@NonNull final TestName testName) {
        return new File(getTestDirName(testName));
    }

    /**
     * Force-delete an existing folder and its contents, located at {@link #testProjectDir}
     *
     * @param testName the name of the test.
     */
    public void deleteTemporaryProject(@NonNull final TestName testName) {
        final File testDir = getTestDir(testName);
        try {
            if (testDir.exists()) {
                Path pathToBeDeleted = testDir.toPath();
                Files.walk(pathToBeDeleted)
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            }
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }


    /**
     * Sets up the test directory by deleting and creating it again.
     *
     * @param testName the name of the given test.
     */
    public void setupTestDir(@NonNull final TestName testName) {
        final String testDirPath = getTestDirName(testName);
        deleteTemporaryProject(testName);
        new File(testDirPath).mkdirs();
    }

    /**
     * Sets up the Trace properties by copying the given property files to the given test. Consumes any I/O error if
     * occurs.
     *
     * @param testName the name of the given test.
     */
    public void setupTraceProperties(@NonNull final TestName testName) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.copyFile(TestConstants.GRADLE_PROPERTIES_FILE_NAME,
                    testDirPath + "traceGradlePlugin.properties");
            FunctionalTestUtils.copyFile("../trace-sdk/" + TestConstants.GRADLE_PROPERTIES_FILE_NAME,
                    testDirPath + "traceSdk.properties");
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Sets up the Gradle properties by copying the given property file to the given test. Consumes any I/O error if
     * occurs.
     *
     * @param testName the name of the given test.
     * @param index    the prefix index of the property file.
     */
    public void setupGradleProperties(@NonNull final TestName testName, final int index) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getGradlePropertiesForResource(index),
                    testDirPath + TestConstants.GRADLE_PROPERTIES_FILE_NAME);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Sets up the Gradle build file by copying the given file to the given test. Consumes any I/O error if occurs.
     *
     * @param testName the name of the given test.
     * @param index    the prefix index of the build file.
     */
    public void setupBuildGradle(@NonNull final TestName testName, final int index) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBuildGradleForResource(index),
                    testDirPath + TestConstants.BUILD_GRADLE_FILE_NAME);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Sets up the Bitrise addons file by copying the given file to the given test. Consumes any I/O error if occurs.
     *
     * @param testName the name of the given test.
     * @param index    the prefix index of the addons file.
     */
    public void setupBitriseAddons(@NonNull final TestName testName, final int index) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBitriseAddonsConfigForResource(index),
                    testDirPath + TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Sets up the Android manifest file by copying the given file to the given test. Consumes any I/O error if occurs.
     *
     * @param testName the name of the given test.
     * @param index    the prefix index of the manifest file.
     */
    public void setupAndroidManifest(@NonNull final TestName testName, final int index) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getAndroidManifestForResource(index),
                    FunctionalTestUtils.getAndroidManifestDefaultPath(testDirPath));
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Sets up the local properties file by copying the given file to the given test. Consumes any I/O error if occurs.
     *
     * @param testName the name of the given test.
     */
    public void setupLocalProperties(@NonNull final TestName testName) {
        final String testDirPath = getTestDirName(testName);

        try {
            FunctionalTestUtils.setUpAndroidSdkDirectory(testDirPath);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
