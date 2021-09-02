package io.bitrise.trace.plugin.util;

/**
 * Constants to avoid code duplication.
 */
public class TestConstants {

  /**
   * The build.gradle file name.
   */
  public static final String BUILD_GRADLE_FILE_NAME = "build.gradle";
  /**
   * The build.gradle file name.
   */
  public static final String GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";
  /**
   * The local.properties (auto-generated) file name.
   */
  public static final String LOCAL_DOT_PROPERTIES = "local.properties";
  /**
   * The path for the test resources for functional tests.
   */
  public static final String FUNCTIONAL_TEST_RESOURCE_PATH = "src/functionalTest/resources/";
  /**
   * The name for the assembleDebug task.
   */
  public static final String ASSEMBLE_DEBUG_TASK_NAME = "assembleDebug";
  /**
   * The name for the verifyTrace task.
   */
  public static final String VERIFY_TRACE_TASK_NAME = "verifyTrace";
  /**
   * The name for the publishToMavenLocal task.
   */
  public static final String PUBLISH_TO_MAVEN_LOCAL_TASK_NAME = "publishToMavenLocal";
  /**
   * The name for the build task.
   */
  public static final String BUILD_TASK_NAME = "build";
  /**
   * The directory name for the main sources.
   */
  public static final String MAIN_SOURCE_DIRECTORY_NAME = "src/main";

  /**
   * The name for the versions.gradle file.
   */
  public static final String VERSIONS_GRADLE = "versions.gradle";

  private TestConstants() {
    throw new IllegalStateException(
        "Should not be instantiated, used only for storing static members!");
  }
}