package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import io.bitrise.trace.plugin.util.TaskUtils;
import io.bitrise.trace.plugin.util.TimeFormattingUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.TimeZone;
import org.gradle.api.tasks.TaskAction;

/**
 * Generates a build ID text file when the Android APK file is built. The build ID is a timestamp
 * String, displayed in both UTC time and Local time. The text file is placed in the folder of
 * the built APK file.
 */
public class GenerateBuildIdTask extends BaseTraceVariantTask {

  private static final String BITRISE_BUILD_ID_TXT = "bitriseBuildId.txt";

  /**
   * Gets the path for the file that stores the build ID.
   *
   * @param buildDir    the root build directory for the application.
   * @param variantName the name of the variant.
   * @return the path.
   */
  @NonNull
  public static String getBuildIdFilePath(@NonNull final File buildDir,
                                          @NonNull final String variantName) {
    return TaskUtils.getTraceOutputDirPath(buildDir, variantName) + "/" + BITRISE_BUILD_ID_TXT;
  }

  /**
   * Reads the build IO file and returns the build ID.
   *
   * @param buildDir    the build directory of the app.
   * @param variantName the name of the variant.
   * @return the build ID.
   * @throws IOException if any I/O error occurs.
   */
  @NonNull
  public static String readBuildIdFromFile(@NonNull final File buildDir,
                                           @NonNull final String variantName) throws IOException {
    final String buildIdFilePath = GenerateBuildIdTask.getBuildIdFilePath(buildDir, variantName);
    final Path path = Paths.get(buildIdFilePath);
    return Files.readAllLines(path).get(0);
  }

  /**
   * The action that will be performed when the task is run. Will create a build ID an place it
   * in a file.
   */
  @TaskAction
  public void generateBuildId() {
    final String buildId = createBuildId();
    final String folderPath =
        TaskUtils.getTraceOutputDirPath(project.getBuildDir(), getVariant().getName());
    writeBuildIdToFile(buildId, folderPath);
  }

  /**
   * Creates a build ID. The Build ID will be the combination of the UTC and local timezone
   * timestamps, with a resolution of 1 second.
   *
   * @return an ID for the build.
   */
  @NonNull
  private String createBuildId() {
    final Date date = new Date(System.currentTimeMillis());
    final String dateUtc = TimeFormattingUtils.formatTime(date, TimeZone.getTimeZone("UTC"));
    final String dateLocal = TimeFormattingUtils.formatTime(date);
    final String buildId = dateUtc + " UTC (" + dateLocal + " Local)";
    logger.debug("Generated build ID = " + buildId);
    return buildId;
  }

  /**
   * Writes the given build ID to the {@link #BITRISE_BUILD_ID_TXT} build file with the given
   * parent folder.
   *
   * @param buildId    the build ID.
   * @param folderPath the parent folder for the file.
   */
  private void writeBuildIdToFile(@NonNull final String buildId, @NonNull final String folderPath) {
    final File parentDir = new File(folderPath);
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }
    final File outputTextFile = new File(folderPath, BITRISE_BUILD_ID_TXT);
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(outputTextFile));
      writer.write(buildId);
      writer.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
