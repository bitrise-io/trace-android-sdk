package io.bitrise.trace.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import io.bitrise.trace.plugin.task.GenerateBuildIdTask;
import io.bitrise.trace.plugin.task.ManifestModifierTask;
import io.bitrise.trace.plugin.task.UploadMappingFileTask;
import io.bitrise.trace.plugin.task.VerifyTraceTask;
import io.bitrise.trace.plugin.util.FunctionalTestHelper;
import io.bitrise.trace.plugin.util.FunctionalTestWriter;
import io.bitrise.trace.plugin.util.TestConstants;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Functional test cases for {@link TraceGradlePlugin}. IMPORTANT: You must first run the Gradle
 * task: <b>trace-gradle-plugin:publishToMavenLocal</b>, before running this test, because this
 * test needs to load and run the Bitrise plugin.
 * In some cases you may get {@code Failed to deserialize script metadata extracted for build
 * file}. The reason might be a known Gradle issue. Best is to delete the '.gradle-test-kit'
 * folder, which should be under the OS temp directory ('/var/folders/'). Example:
 * 'Macintosh HD/private/var/folders/08/n7rfrzy555x793xxsl8tnpqw0000gn/.gradle-test-kit-richard
 * .bogdan'
 *
 * @see
 * <a href="https://github.com/gradle/gradle/issues/4506">https://github.com/gradle/gradle/issues/4506</a>
 */
public class TraceGradlePluginFunctionalTest {

  /**
   * A flag to enable or disable the deletion of the temporary folder, when the tests have
   * finished. Default to off, to allow inspection of the files after the test has run.
   */
  private static final boolean DELETE_TEMP_PROJECT = false;
  /**
   * The {@link FunctionalTestHelper} class that provides some supportive functions to the tests.
   */
  private static final FunctionalTestHelper functionalTestHelper = new FunctionalTestHelper();
  /**
   * Format for the relative path of the build ID file. The formatting arguments should be the
   * build variant.
   */
  private static final String buildIdFileRelativePathFormat =
      "/build/outputs/apk/%s/bitriseBuildId.txt";
  /**
   * An instance of {@link FunctionalTestWriter} that will write the output of each test case
   * to the console with * AQUA4 color from the Bitkit.
   */
  private final FunctionalTestWriter stdOutWriter = new FunctionalTestWriter(System.out,
      FunctionalTestWriter.PrintColor.AQUA4);
  /**
   * An instance of {@link FunctionalTestWriter} that will write the error of each test case to
   * the console with RED4 color from the Bitkit.
   */
  private final FunctionalTestWriter errWriter = new FunctionalTestWriter(System.err,
      FunctionalTestWriter.PrintColor.RED4);
  /**
   * A Rule to have the name of the current test.
   */
  @Rule
  public TestName testName = new TestName();

  /**
   * Sets up the test class.
   */
  @BeforeClass
  public static void setUpTests() {
    functionalTestHelper
        .logInAsciiBox("Starting tests for " + TraceGradlePluginFunctionalTest.class.getName());
    functionalTestHelper.publishTraceGradlePlugin();
  }

  @AfterClass
  public static void tearDownClass() {
    functionalTestHelper
        .logInAsciiBox("Finished tests for " + TraceGradlePluginFunctionalTest.class.getName());
  }

  /**
   * Sets up the current test.
   */
  @Before
  public void setUpTest() {
    functionalTestHelper.logTestNameInAsciiBox(testName);
    functionalTestHelper.setupTestDir(testName);
    functionalTestHelper.setupTraceProperties(testName);
    functionalTestHelper.setupGradleProperties(testName, 0);
    functionalTestHelper.setupBitriseAddons(testName, 0);
    functionalTestHelper.setupAndroidManifest(testName, 0);
  }

  /**
   * Delete (permanently) the temporary Gradle project folder, if {@link #DELETE_TEMP_PROJECT} is
   * enabled.
   */
  @After
  public void tearDownTest() {
    if (DELETE_TEMP_PROJECT) {
      functionalTestHelper.deleteTestProjectDir(testName);
    }
  }

  /**
   * Tests that the Trace tasks are hooked in all the required places when the 'assembleDebug'
   * task is executed.
   */
  @Test
  public void assembleDebugTraceTaskHookTest_withGradleConfig_0() {
    functionalTestHelper.setupBuildGradle(testName, 0);
    final BuildResult buildResult = executeTaskForResult(TestConstants.ASSEMBLE_DEBUG_TASK_NAME);

    verifyDebugManifestTasks(buildResult);
    verifyDebugGenerateBuildIdTasks(buildResult);
    verifyGenerateBuildIdTasksForAssembleDebug();
  }

  /**
   * Tests that the Trace tasks are hooked in all the required places when the 'assembleDebug'
   * task is executed.
   */
  @Test
  public void assembleDebugTraceTaskHookTest_withGradleConfig_1() {
    functionalTestHelper.setupBuildGradle(testName, 1);
    final BuildResult buildResult = executeTaskForResult(TestConstants.ASSEMBLE_DEBUG_TASK_NAME);

    verifyDebugManifestTasks(buildResult);
    verifyDebugGenerateBuildIdTasks(buildResult);
    verifyGenerateBuildIdTasksForAssembleDebug();
  }

  /**
   * Tests that the Trace tasks are hooked in all the required places when the 'build' task is
   * executed.
   */
  @Test
  public void buildTraceTaskHookTest_withGradleConfig_0() {
    functionalTestHelper.setupBuildGradle(testName, 0);
    final BuildResult buildResult = executeTaskForResult(TestConstants.BUILD_TASK_NAME);

    verifyDebugManifestTasks(buildResult);
    verifyDebugGenerateBuildIdTasks(buildResult);

    verifyReleaseManifestTasks(buildResult);
    verifyGenerateBuildIdTasksForBuild();

    verifyReleaseGenerateBuildIdTasks(buildResult);
    verifyUploadMappingFileTasksForAssembleRelease(buildResult);
  }

  /**
   * Tests that the  execution of {@link VerifyTraceTask} results in error. The reason is that
   * the given build .gradle does not have a dependency on trace-sdk.
   */
  @Test(expected = UnexpectedBuildFailure.class)
  public void verifyTraceTaskTest_withGradleConfig_0() {
    functionalTestHelper.setupBuildGradle(testName, 0);
    final BuildResult buildResult = executeTaskForResult(TestConstants.VERIFY_TRACE_TASK_NAME);
    final BuildTask verifyTraceTask =
        assertTaskHasRun(buildResult, TestConstants.VERIFY_TRACE_TASK_NAME);

    assertEquals(TaskOutcome.FAILED, verifyTraceTask.getOutcome());
  }

  /**
   * Tests that the {@link VerifyTraceTask} executes without any error. This will use the 1_build
   * .gradle which has dependency on the trace-sdk.
   */
  @Test
  public void verifyTraceTaskTest_withGradleConfig_1() {
    functionalTestHelper.setupBuildGradle(testName, 1);
    final BuildResult buildResult = executeTaskForResult(TestConstants.VERIFY_TRACE_TASK_NAME);

    final BuildTask verifyTraceTask =
        assertTaskHasRun(buildResult, TestConstants.VERIFY_TRACE_TASK_NAME);
    assertEquals(TaskOutcome.SUCCESS, verifyTraceTask.getOutcome());
  }

  /**
   * Verifies when 'assembleDebug' task is executed, that the {@link ManifestModifierTask} has
   * run in the given {@link BuildResult} and that it ran after the manifest processing.
   *
   * @param buildResult the BuildResult.
   */
  private void verifyDebugManifestTasks(@NonNull final BuildResult buildResult) {
    final List<BuildTask> buildTasks = buildResult.getTasks();

    final BuildTask processDebugManifestTask =
        assertTaskHasRun(buildResult, "processDebugManifest");
    final BuildTask debugModifyManifestTask = assertTaskHasRun(buildResult, "debugModifyManifest");
    final int manifestProcessIndex = buildTasks.indexOf(processDebugManifestTask);
    final int manifestModifyIndex = buildTasks.indexOf(debugModifyManifestTask);

    assertThat(manifestProcessIndex, is(lessThan(manifestModifyIndex)));
    assertEquals(TaskOutcome.SUCCESS, processDebugManifestTask.getOutcome());
    assertEquals(TaskOutcome.SUCCESS, debugModifyManifestTask.getOutcome());
  }

  /**
   * Verifies when 'assembleRelease' task is executed, that the {@link ManifestModifierTask}
   * has run in the given {@link BuildResult} and that it ran after the manifest processing.
   *
   * @param buildResult the BuildResult.
   */
  private void verifyReleaseManifestTasks(@NonNull final BuildResult buildResult) {
    final List<BuildTask> buildTasks = buildResult.getTasks();

    final BuildTask processReleaseManifestTask =
        assertTaskHasRun(buildResult, "processReleaseManifest");
    final BuildTask releaseModifyManifestTask =
        assertTaskHasRun(buildResult, "releaseModifyManifest");
    final int manifestProcessIndex = buildTasks.indexOf(processReleaseManifestTask);
    final int manifestModifyIndex = buildTasks.indexOf(releaseModifyManifestTask);

    assertThat(manifestProcessIndex, is(lessThan(manifestModifyIndex)));
    assertEquals(TaskOutcome.SUCCESS, processReleaseManifestTask.getOutcome());
    assertEquals(TaskOutcome.SUCCESS, releaseModifyManifestTask.getOutcome());
  }

  /**
   * Verifies when 'assembleDebug' task is executed, that the {@link GenerateBuildIdTask} has run
   * in the given {@link BuildResult} and that it ran after the assemble task.
   *
   * @param buildResult the BuildResult.
   */
  private void verifyDebugGenerateBuildIdTasks(@NonNull final BuildResult buildResult) {
    final List<BuildTask> buildTasks = buildResult.getTasks();

    final BuildTask assembleDebugTask = assertTaskHasRun(buildResult, "assembleDebug");
    final BuildTask debugGenerateBitriseBuildIdTask =
        assertTaskHasRun(buildResult, "debugGenerateBitriseBuildId");
    final int assembleDebugIndex = buildTasks.indexOf(assembleDebugTask);
    final int generateBitriseBuildIdIndex = buildTasks.indexOf(debugGenerateBitriseBuildIdTask);

    assertThat(generateBitriseBuildIdIndex, is(greaterThan(assembleDebugIndex)));
    assertEquals(TaskOutcome.SUCCESS, assembleDebugTask.getOutcome());
    assertEquals(TaskOutcome.SUCCESS, debugGenerateBitriseBuildIdTask.getOutcome());
  }

  /**
   * Verifies when 'assembleRelease' task is executed, that the {@link GenerateBuildIdTask} has
   * run in the given {@link BuildResult} and that it ran after the assemble task.
   *
   * @param buildResult the BuildResult.
   */
  private void verifyReleaseGenerateBuildIdTasks(@NonNull final BuildResult buildResult) {
    final List<BuildTask> buildTasks = buildResult.getTasks();

    final BuildTask assembleReleaseTask = assertTaskHasRun(buildResult, "assembleRelease");
    final BuildTask releaseGenerateBitriseBuildIdTask =
        assertTaskHasRun(buildResult, "releaseGenerateBitriseBuildId");
    final int assembleReleaseIndex = buildTasks.indexOf(assembleReleaseTask);
    final int generateBitriseBuildIdIndex = buildTasks.indexOf(releaseGenerateBitriseBuildIdTask);

    assertThat(generateBitriseBuildIdIndex, is(greaterThan(assembleReleaseIndex)));
    assertEquals(TaskOutcome.SUCCESS, assembleReleaseTask.getOutcome());
    assertEquals(TaskOutcome.SUCCESS, releaseGenerateBitriseBuildIdTask.getOutcome());
  }

  /**
   * Verifies when 'build' task is executed, that the {@link GenerateBuildIdTask} has created the
   * files that contains the build ID.
   */
  private void verifyGenerateBuildIdTasksForBuild() {
    final File debugBuildIdFile = new File(
        functionalTestHelper.getTestDir(testName).getPath()
            + String.format(buildIdFileRelativePathFormat, "debug"));
    final File releaseBuildIdFile = new File(
        functionalTestHelper.getTestDir(testName).getPath()
            + String.format(buildIdFileRelativePathFormat, "release"));

    assertThat(debugBuildIdFile.exists(), is(true));
    assertThat(releaseBuildIdFile.exists(), is(true));
  }

  /**
   * Verifies when 'assembleDebug' task is executed, that the {@link GenerateBuildIdTask} has
   * created the file that contains the build ID.
   */
  private void verifyGenerateBuildIdTasksForAssembleDebug() {
    final File debugBuildIdFile = new File(
        functionalTestHelper.getTestDir(testName).getPath()
            + String.format(buildIdFileRelativePathFormat, "debug"));

    assertThat(debugBuildIdFile.exists(), is(true));
  }

  /**
   * Verifies when 'assembleRelease' task is executed, that the {@link UploadMappingFileTask} has
   * run.
   */
  private void verifyUploadMappingFileTasksForAssembleRelease(
      @NonNull final BuildResult buildResult) {
    final List<BuildTask> buildTasks = buildResult.getTasks();
    final BuildTask assembleReleaseTask = assertTaskHasRun(buildResult, "assembleRelease");
    final BuildTask releaseUploadMappingFile =
        assertTaskHasRun(buildResult, "releaseUploadMappingFile");
    final int assembleReleaseIndex = buildTasks.indexOf(assembleReleaseTask);
    final int generateBitriseBuildIdIndex = buildTasks.indexOf(releaseUploadMappingFile);

    assertThat(generateBitriseBuildIdIndex, is(greaterThan(assembleReleaseIndex)));
    assertEquals(TaskOutcome.SUCCESS, assembleReleaseTask.getOutcome());
    assertEquals(TaskOutcome.SUCCESS, releaseUploadMappingFile.getOutcome());
  }

  /**
   * Executes the given Gradle task in a project directory with the name of the current test. Uses
   * {@link FunctionalTestWriter} to modify the text appearance of it's output.
   *
   * @param taskName the name of the task to execute.
   * @return the {@link BuildResult}.
   */
  @NonNull
  private BuildResult executeTaskForResult(@NonNull final String taskName) {
    return GradleRunner.create()
                       .withProjectDir(functionalTestHelper.getTestDir(testName))
                       .withArguments(taskName)
                       .forwardStdOutput(stdOutWriter)
                       .forwardStdError(errWriter)
                       .build();
  }

  /**
   * Checks the given {@link BuildResult} if the given task has run or not. Runs an assert on it.
   *
   * @param buildResult the BuildResult of Gradle task execution.
   * @param taskName    the name of the task we are looking for.
   * @return the given task.
   */
  private BuildTask assertTaskHasRun(@NonNull final BuildResult buildResult,
                                     @NonNull final String taskName) {
    final List<BuildTask> buildTasks = buildResult.getTasks();
    final Optional<BuildTask> buildTaskOptional =
        buildTasks.stream()
                  .filter(buildTask -> buildTask.getPath().equals(":" + taskName))
                  .findFirst();
    assertThat(buildTaskOptional.isPresent(), is(true));
    return buildTaskOptional.get();
  }
}