package io.bitrise.trace.plugin.task;

import androidx.annotation.Nullable;
import com.android.build.gradle.api.BaseVariantOutput;
import io.bitrise.trace.plugin.TraceGradlePlugin;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import java.io.File;
import org.gradle.api.tasks.TaskAction;

/**
 * Task for uploading a given variant's mapping file (if it exists).
 */
public class UploadMappingFileTask extends BaseTraceVariantTask {

  /**
   * Task action that will be called when run. Uploads the mapping file of the given variant to
   * the server.
   */
  @TaskAction
  public void uploadMappingFile() {
    final BaseVariantOutput baseVariantOutput = getVariantOutput();
    if (baseVariantOutput == null) {
      return;
    }

    final File mappingFile = new BuildHelper(logger).getMappingFiletPath(getVariant());
    uploadFile(mappingFile);
  }

  /**
   * Uploads the given File to the backend, so later an obfuscated crash report can be retraced.
   *
   * @param file the given File.
   */
  private void uploadFile(@Nullable final File file) {
    if (file == null) {
      logger.info(TraceGradlePlugin.LOGGER_TAG, "No mapping file found for variant {}, nothing to"
          + " upload.", getVariant().getName());
      return;
    }
    logger.info("Uploading mapping file {} for variant {}.", file.getName(), getVariant());
    // TODO upload code
  }
}
