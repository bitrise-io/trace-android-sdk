package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import com.android.build.gradle.api.BaseVariantOutput;
import io.bitrise.trace.plugin.util.TimeFormattingUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
   * The action that will be performed when the task is run. Will create a build ID an place it
   * in a file.
   */
  @TaskAction
  public void generateBuildId() {
    final String buildId = createBuildId();
    final String folderPath = getBuildOutputFolder();
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
   * Use the Android Gradle Plugin APIs to find the output folder path of the built Android APK
   * file. This path will be different for Debug and Release builds.
   *
   * @return The full folder path of the currently built APK file (parent folder).
   */
  @NonNull
  private String getBuildOutputFolder() {
    final BaseVariantOutput baseVariantOutput = getVariantOutput();
    if (baseVariantOutput == null) {
      throw new IllegalStateException(
          "Variant output should not be null, task initialised wrongly!");
    }
    final String apkFolderPath =
        baseVariantOutput.getOutputFile().getParentFile().getAbsolutePath();
    logger.debug("APK output folder = " + apkFolderPath);
    return apkFolderPath + "/";
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
    final File outputTextFile = new File(folderPath + BITRISE_BUILD_ID_TXT);
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(outputTextFile));
      writer.write(buildId);
      writer.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
