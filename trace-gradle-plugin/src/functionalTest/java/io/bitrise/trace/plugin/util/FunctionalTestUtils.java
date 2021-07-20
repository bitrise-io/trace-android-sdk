package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import io.bitrise.trace.plugin.TraceConstants;
import io.bitrise.trace.plugin.TraceGradlePluginFunctionalTest;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.gradle.testkit.runner.GradleRunner;

/**
 * Utility methods to support {@link TraceGradlePluginFunctionalTest}.
 */
public class FunctionalTestUtils {

  /**
   * Copies a single file to the given destination.
   *
   * @param source the path of the source.
   * @param dest   the path of the destination.
   * @throws IOException if an I/O error occurs.
   */
  public static void copyFile(@NonNull final String source, @NonNull final String dest)
      throws IOException {
    final Path sourcePath = Paths.get(source);
    final Path destPath = Paths.get(dest);
    Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Gets the build.gradle file from the resources with the given prefix index. Build files are
   * prefixed to enable usage for multiple resource files.
   *
   * @param index the prefix index.
   * @return the path of the file.
   */
  @NonNull
  public static String getBuildGradleForResource(final int index) {
    return String.format("%1$s%2$s_%3$s", TestConstants.FUNCTIONAL_TEST_RESOURCE_PATH, index,
        TestConstants.BUILD_GRADLE_FILE_NAME);
  }

  /**
   * Gets the gradle.properties file from the resources with the given prefix index. Property
   * files are prefixed to enable usage for multiple resource files.
   *
   * @param index the prefix index.
   * @return the path of the file.
   */
  @NonNull
  public static String getGradlePropertiesForResource(final int index) {
    return String.format("%1$s%2$s_%3$s", TestConstants.FUNCTIONAL_TEST_RESOURCE_PATH, index,
        TestConstants.GRADLE_PROPERTIES_FILE_NAME);
  }

  /**
   * Gets the {@link TraceConstants#BITRISE_ADDONS_CONFIGURATION_FILE} file from the resources
   * with the given prefix index. Config files are prefixed to enable usage for multiple resource
   * files.
   *
   * @param index the prefix index.
   * @return the path of the file.
   */
  @NonNull
  public static String getBitriseAddonsConfigForResource(final int index) {
    return String.format("%1$s%2$s_%3$s", TestConstants.FUNCTIONAL_TEST_RESOURCE_PATH, index,
        TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE);
  }

  /**
   * Gets the AndroidManifest.xml file from the resources with the given prefix index. Build
   * files are prefixed to enable usage for multiple resource files.
   *
   * @param index the prefix index.
   * @return the path of the file.
   */
  @NonNull
  public static String getAndroidManifestForResource(final int index) {
    return String.format("%1$s%2$s_%3$s", TestConstants.FUNCTIONAL_TEST_RESOURCE_PATH, index,
        BuildHelper.ANDROID_MANIFEST_FILE_NAME);
  }

  /**
   * Gets the proguard-rules.pro file from the resources with the given prefix index. Build
   * files are prefixed to enable usage for multiple resource files.
   *
   * @param index the prefix index.
   * @return the path of the file.
   */
  @NonNull
  public static String getProguardRulesFileForResource(final int index) {
    return String.format("%1$s%2$s_%3$s", TestConstants.FUNCTIONAL_TEST_RESOURCE_PATH, index,
        BuildHelper.PROGUARD_RULES_FILE_NAME);
  }

  /**
   * Gets the default path for the AndroidManifest.xml.
   *
   * @param rootPath the root path of the project.
   * @return the path of the file.
   */
  @NonNull
  public static String getAndroidManifestDefaultPath(@NonNull final String rootPath) {
    final File dir = new File(rootPath + TestConstants.MAIN_SOURCE_DIRECTORY_NAME);
    dir.mkdirs();
    return dir.getPath() + File.separator + BuildHelper.ANDROID_MANIFEST_FILE_NAME;
  }

  /**
   * Gets the default path for the proguard-rules.pro.
   *
   * @param rootPath the root path of the project.
   * @return the path of the file.
   */
  @NonNull
  public static String getProguardRulesFileDefaultPath(@NonNull final String rootPath) {
    final File dir = new File(rootPath);
    dir.mkdirs();
    return dir.getPath() + File.separator + BuildHelper.PROGUARD_RULES_FILE_NAME;
  }

  /**
   * Publishes the 'trace-gradle-plugin' with it's current state, to make sure tests are run on
   * the up-to-date version.
   */
  public static void publishTraceGradlePlugin() {
    GradleRunner.create()
                .withArguments(TestConstants.PUBLISH_TO_MAVEN_LOCAL_TASK_NAME)
                .forwardOutput()
                .withProjectDir(new File(""))
                .build();
  }
}
