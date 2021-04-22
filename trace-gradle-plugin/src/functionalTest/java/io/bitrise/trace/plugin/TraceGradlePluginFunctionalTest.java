package io.bitrise.trace.plugin;

import androidx.annotation.NonNull;

import org.apache.log4j.Logger;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.bitrise.trace.plugin.task.GenerateBuildIDTask;
import io.bitrise.trace.plugin.task.ManifestModifierTask;
import io.bitrise.trace.plugin.task.VerifyTraceTask;
import io.bitrise.trace.plugin.util.FunctionalTestUtils;
import io.bitrise.trace.plugin.util.TestConstants;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

/**
 * Functional test cases for {@link TraceGradlePlugin}. IMPORTANT: You must first run the Gradle task:
 * <b>trace-gradle-plugin:publishToMavenLocal</b>, before running this test, because this test needs to load and run
 * the Bitrise plugin.
 * In some cases you may get {@code Failed to deserialize script metadata extracted for build file}. The reason might
 * be a known Gradle issue. Best is to delete the '.gradle-test-kit' folder, which should be under the OS temp
 * directory ('/var/folders/'). Example:
 * 'Macintosh HD/private/var/folders/08/n7rfrzy555x793xxsl8tnpqw0000gn/.gradle-test-kit-richard.bogdan'
 *
 * @see <a href="https://github.com/gradle/gradle/issues/4506">https://github.com/gradle/gradle/issues/4506</a>
 */
public class TraceGradlePluginFunctionalTest {

    /**
     * A Rule to have the name of the current test.
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * A flag to enable or disable the deletion of the temporary folder, when the tests have finished. Default to
     * off, to allow inspection of the files after the test has run.
     */
    private static final boolean DELETE_TEMP_PROJECT = false;
    /**
     * A temporary folder for running functional tests. A new gradle project will be setup in this folder, and the
     * tests will run in this project.
     */
    private static File testProjectDir =
            new File(System.getProperty("user.home") + "/BitriseTrace-TestGradleProject");

    /**
     * Sets up the test class.
     */
    @BeforeClass
    public static void setUpTests() {
        final Logger logger = Logger.getRootLogger();
        logger.info("Publishing plugin, to make sure it is up-to-date");
        FunctionalTestUtils.publishTraceGradlePlugin();
        logger.info("Publishing plugin finished");
    }

    /**
     * Sets up the current test.
     */
    @Before
    public void setUpTest() {
        final String testDirPath = getTestDirName();

        try {
            deleteTemporaryProject();
            new File(testDirPath).mkdirs();
            FunctionalTestUtils.setUpAndroidSdkDirectory(testDirPath);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            FunctionalTestUtils.copyFile("../" + TestConstants.VERSIONS_GRADLE,
                    testDirPath + TestConstants.VERSIONS_GRADLE);
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getGradlePropertiesForResource(0),
                    testDirPath + TestConstants.GRADLE_PROPERTIES_FILE_NAME);
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBuildGradleForResource(0),
                    testDirPath + TestConstants.BUILD_GRADLE_FILE_NAME);
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBitriseAddonsConfigForResource(0),
                    testDirPath + TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE);
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getAndroidManifestForResource(0),
                    FunctionalTestUtils.getAndroidManifestDefaultPath(testDirPath));
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Gets the name of the current test's directory.
     *
     * @return the name of the test's directory.
     */
    @NonNull
    private String getTestDirName() {
        return testProjectDir.getAbsolutePath() + "/" + testName.getMethodName() + "/";
    }

    /**
     * Gets the the current test's directory.
     *
     * @return the test's directory.
     */
    @NonNull
    private File getTestDir() {
        return new File(getTestDirName());
    }

    /**
     * Force-delete an existing folder and its contents, located at {@link #testProjectDir}
     */
    private void deleteTemporaryProject() throws IOException {
        final File testDir = getTestDir();
        if (testDir.exists()) {
            Path pathToBeDeleted = testDir.toPath();
            Files.walk(pathToBeDeleted)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    /**
     * Delete (permanently) the temporary gradle project folder, if {@link #DELETE_TEMP_PROJECT} is enabled.
     */
    @After
    public void deleteTemporaryProjectAfterTest() throws IOException {
        if (DELETE_TEMP_PROJECT) {
            deleteTemporaryProject();
        }
    }

    /**
     * Tests that the Trace tasks are hooked in all the required places when the 'assembleDebug' task is executed.
     * build.
     */
    @Test
    public void assembleDebugTraceTaskHookTest_withGradleConfig_0() {
        final BuildResult buildResult =
                GradleRunner.create()
                            .withProjectDir(getTestDir())
                            .withArguments(TestConstants.ASSEMBLE_DEBUG_TASK_NAME)
                            .forwardOutput()
                            .build();
        verifyDebugManifestTasks(buildResult);
        verifyDebugGenerateBuildIdTasks(buildResult);
        verifyGenerateBuildIdTasksForAssembleDebug();
    }

    /**
     * Tests that the Trace tasks are hooked in all the required places when the 'assembleDebug' task is executed.
     * build.
     */
    @Test
    public void assembleDebugTraceTaskHookTest_withGradleConfig_1() {
        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBuildGradleForResource(1),
                    getTestDirName() + TestConstants.BUILD_GRADLE_FILE_NAME);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }

        final BuildResult buildResult =
                GradleRunner.create()
                            .withProjectDir(getTestDir())
                            .withArguments(TestConstants.ASSEMBLE_DEBUG_TASK_NAME)
                            .forwardOutput()
                            .build();
        verifyDebugManifestTasks(buildResult);
        verifyDebugGenerateBuildIdTasks(buildResult);
        verifyGenerateBuildIdTasksForAssembleDebug();
    }

    /**
     * Tests that the Trace tasks are hooked in all the required places when the 'build' task is executed.
     */
    @Test
    public void buildTraceTaskHookTest_withGradleConfig_0() {
        final BuildResult buildResult =
                GradleRunner.create()
                            .withProjectDir(getTestDir())
                            .withArguments(TestConstants.BUILD_TASK_NAME)
                            .forwardOutput()
                            .build();
        verifyDebugManifestTasks(buildResult);
        verifyDebugGenerateBuildIdTasks(buildResult);

        verifyReleaseManifestTasks(buildResult);
        verifyGenerateBuildIdTasksForBuild();

        verifyReleaseGenerateBuildIdTasks(buildResult);
    }

    /**
     * Tests that the  execution of {@link VerifyTraceTask} results in error. The reason is that the given build
     * .gradle does not have a dependency on trace-sdk.
     */
    @Test(expected = UnexpectedBuildFailure.class)
    public void verifyTraceTaskTest_withGradleConfig_0() {
        final BuildResult buildResult = GradleRunner.create()
                                                    .withProjectDir(getTestDir())
                                                    .withArguments(TestConstants.VERIFY_TRACE_TASK_NAME)
                                                    .forwardOutput()
                                                    .build();

        final List<BuildTask> buildTasks = buildResult.getTasks();
        final Optional<BuildTask> verifyTraceTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":" + TestConstants.VERIFY_TRACE_TASK_NAME))
                          .findFirst();

        assertThat(verifyTraceTaskOptional.isPresent(), is(true));
        assertEquals(TaskOutcome.FAILED, verifyTraceTaskOptional.get().getOutcome());
    }

    /**
     * Tests that the {@link VerifyTraceTask} executes without any error. This will use the 1_build.gradle which has
     * dependency on the trace-sdk.
     */
    @Test
    public void verifyTraceTaskTest_withGradleConfig_1() {
        try {
            FunctionalTestUtils.copyFile(FunctionalTestUtils.getBuildGradleForResource(1),
                    getTestDirName() + TestConstants.BUILD_GRADLE_FILE_NAME);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        final BuildResult buildResult = GradleRunner.create()
                                                    .withProjectDir(getTestDir())
                                                    .withArguments(TestConstants.VERIFY_TRACE_TASK_NAME)
                                                    .forwardOutput()
                                                    .build();

        final List<BuildTask> buildTasks = buildResult.getTasks();
        final Optional<BuildTask> verifyTraceTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":" + TestConstants.VERIFY_TRACE_TASK_NAME))
                          .findFirst();

        assertThat(verifyTraceTaskOptional.isPresent(), is(true));
        assertEquals(TaskOutcome.SUCCESS, verifyTraceTaskOptional.get().getOutcome());
    }

    /**
     * Verifies when 'assembleDebug' task is executed, that the {@link ManifestModifierTask} has run in the given
     * {@link BuildResult} and that it ran after the manifest processing.
     *
     * @param buildResult the BuildResult.
     */
    private void verifyDebugManifestTasks(@NonNull final BuildResult buildResult) {
        final List<BuildTask> buildTasks = buildResult.getTasks();

        final Optional<BuildTask> processDebugManifestTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":processDebugManifest"))
                          .findFirst();
        final Optional<BuildTask> debugModifyManifestTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":debugModifyManifest"))
                          .findFirst();

        assertThat(processDebugManifestTaskOptional.isPresent(), is(true));
        assertThat(debugModifyManifestTaskOptional.isPresent(), is(true));

        final BuildTask processDebugManifestTask = processDebugManifestTaskOptional.get();
        final BuildTask debugModifyManifestTask = debugModifyManifestTaskOptional.get();
        final int manifestProcessIndex = buildTasks.indexOf(processDebugManifestTask);
        final int manifestModifyIndex = buildTasks.indexOf(debugModifyManifestTask);

        assertThat(manifestProcessIndex, is(lessThan(manifestModifyIndex)));
        assertEquals(TaskOutcome.SUCCESS, processDebugManifestTask.getOutcome());
        assertEquals(TaskOutcome.SUCCESS, debugModifyManifestTask.getOutcome());
    }

    /**
     * Verifies when 'assembleRelease' task is executed, that the {@link ManifestModifierTask} has run in the given
     * {@link BuildResult} and that it ran after the manifest processing.
     *
     * @param buildResult the BuildResult.
     */
    private void verifyReleaseManifestTasks(@NonNull final BuildResult buildResult) {
        final List<BuildTask> buildTasks = buildResult.getTasks();

        final Optional<BuildTask> processReleaseManifestTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":processReleaseManifest"))
                          .findFirst();
        final Optional<BuildTask> releaseModifyManifestTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":releaseModifyManifest"))
                          .findFirst();

        assertThat(processReleaseManifestTaskOptional.isPresent(), is(true));
        assertThat(releaseModifyManifestTaskOptional.isPresent(), is(true));

        final BuildTask processReleaseManifestTask = processReleaseManifestTaskOptional.get();
        final BuildTask releaseModifyManifestTask = releaseModifyManifestTaskOptional.get();
        final int manifestProcessIndex = buildTasks.indexOf(processReleaseManifestTask);
        final int manifestModifyIndex = buildTasks.indexOf(releaseModifyManifestTask);

        assertThat(manifestProcessIndex, is(lessThan(manifestModifyIndex)));
        assertEquals(TaskOutcome.SUCCESS, processReleaseManifestTask.getOutcome());
        assertEquals(TaskOutcome.SUCCESS, releaseModifyManifestTask.getOutcome());
    }

    /**
     * Verifies when 'assembleDebug' task is executed, that the {@link GenerateBuildIDTask} has run in the given
     * {@link BuildResult} and that it ran after the assemble task.
     *
     * @param buildResult the BuildResult.
     */
    private void verifyDebugGenerateBuildIdTasks(@NonNull final BuildResult buildResult) {
        final List<BuildTask> buildTasks = buildResult.getTasks();

        final Optional<BuildTask> assembleDebugTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":assembleDebug"))
                          .findFirst();
        final Optional<BuildTask> debugGenerateBitriseBuildIdTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":debugGenerateBitriseBuildId"))
                          .findFirst();

        assertThat(assembleDebugTaskOptional.isPresent(), is(true));
        assertThat(debugGenerateBitriseBuildIdTaskOptional.isPresent(), is(true));

        final BuildTask assembleDebugTask = assembleDebugTaskOptional.get();
        final BuildTask debugGenerateBitriseBuildIdTask = debugGenerateBitriseBuildIdTaskOptional.get();
        final int assembleDebugIndex = buildTasks.indexOf(assembleDebugTask);
        final int generateBitriseBuildIdIndex = buildTasks.indexOf(debugGenerateBitriseBuildIdTask);

        assertThat(generateBitriseBuildIdIndex, is(greaterThan(assembleDebugIndex)));
        assertEquals(TaskOutcome.SUCCESS, assembleDebugTask.getOutcome());
        assertEquals(TaskOutcome.SUCCESS, debugGenerateBitriseBuildIdTask.getOutcome());
    }

    /**
     * Verifies when 'assembleRelease' task is executed, that the {@link GenerateBuildIDTask} has run in the given
     * {@link BuildResult} and that it ran after the assemble task.
     *
     * @param buildResult the BuildResult.
     */
    private void verifyReleaseGenerateBuildIdTasks(@NonNull final BuildResult buildResult) {
        final List<BuildTask> buildTasks = buildResult.getTasks();

        final Optional<BuildTask> assembleReleaseTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":assembleRelease"))
                          .findFirst();
        final Optional<BuildTask> releaseGenerateBitriseBuildIdTaskOptional =
                buildTasks.stream()
                          .filter(buildTask -> buildTask.getPath().equals(":releaseGenerateBitriseBuildId"))
                          .findFirst();

        assertThat(assembleReleaseTaskOptional.isPresent(), is(true));
        assertThat(releaseGenerateBitriseBuildIdTaskOptional.isPresent(), is(true));

        final BuildTask assembleReleaseTask = assembleReleaseTaskOptional.get();
        final BuildTask releaseGenerateBitriseBuildIdTask = releaseGenerateBitriseBuildIdTaskOptional.get();
        final int assembleReleaseIndex = buildTasks.indexOf(assembleReleaseTask);
        final int generateBitriseBuildIdIndex = buildTasks.indexOf(releaseGenerateBitriseBuildIdTask);

        assertThat(generateBitriseBuildIdIndex, is(greaterThan(assembleReleaseIndex)));
        assertEquals(TaskOutcome.SUCCESS, assembleReleaseTask.getOutcome());
        assertEquals(TaskOutcome.SUCCESS, releaseGenerateBitriseBuildIdTask.getOutcome());
    }

    /**
     * Verifies when 'build' task is executed, that the {@link GenerateBuildIDTask} has created the files that
     * contains the build ID.
     */
    private void verifyGenerateBuildIdTasksForBuild() {
        final File debugBuildIdFile =
                new File(getTestDir().getPath() + "/build/outputs/apk/debug/bitriseBuildId.txt");
        final File releaseBuildIdFile =
                new File(getTestDir().getPath() + "/build/outputs/apk/release/bitriseBuildId.txt");

        assertThat(debugBuildIdFile.exists(), is(true));
        assertThat(releaseBuildIdFile.exists(), is(true));
    }

    /**
     * Verifies when 'assembleDebug' task is executed, that the {@link GenerateBuildIDTask} has created the file that
     * contains the build ID.
     */
    private void verifyGenerateBuildIdTasksForAssembleDebug() {
        final File debugBuildIdFile =
                new File(getTestDir().getPath() + "/build/outputs/apk/debug/bitriseBuildId.txt");

        assertThat(debugBuildIdFile.exists(), is(true));
    }
}