package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.build.gradle.AppExtension;
import io.bitrise.trace.plugin.TraceGradlePlugin;
import io.bitrise.trace.plugin.configuration.BuildConfigurationManager;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import io.bitrise.trace.plugin.network.SymbolCollectorCommunicator;
import io.bitrise.trace.plugin.network.SymbolCollectorNetworkClient;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Task for uploading a given variant's mapping file (if it exists).
 */
public class UploadMappingFileTask extends BaseTraceVariantTask {

  /**
   * Task action that will be called when run. Uploads the mapping file of the given variant to
   * the server.
   *
   * @throws IOException if any I/O error occurs.
   */
  @TaskAction
  public void uploadMappingFile() throws IOException {
    final File mappingFile = new BuildHelper(logger).getMappingFiletPath(getVariant());
    uploadFile(mappingFile);
  }

  /**
   * Uploads the given File to the backend, so later an obfuscated crash report can be retraced.
   *
   * @param file the given File.
   * @throws IOException if any I/O error occurs.
   */
  private void uploadFile(@Nullable final File file) throws IOException {
    if (file == null) {
      logger.info(TraceGradlePlugin.LOGGER_TAG, "No mapping file found for variant {}, nothing to"
          + " upload.", getVariant().getName());
      return;
    }

    final RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), file);

    // get customer application information.
    final AppExtension androidProjectInfo = (AppExtension) project.getExtensions()
                                                                  .findByName("android");
    if (androidProjectInfo == null || androidProjectInfo.getDefaultConfig() == null) {
      logger.info(TraceGradlePlugin.LOGGER_TAG, "Could not find android default config.");
      return;
    }
    final String versionName = androidProjectInfo.getDefaultConfig().getVersionName();
    final String buildCode = androidProjectInfo.getDefaultConfig().getVersionCode().toString();

    uploadFile(requestFile, file.getName(), buildCode, versionName);
  }

  /**
   * Uploads the given File to the backend, so later an obfuscated crash report can be retraced.
   *
   * @param requestFile       the file itself as a multipart body.
   * @param name              the name of the file.
   * @param buildCode    the version code of the customer application.
   * @param versionName    the version name of the customer application.
   * @throws IOException      if any I/O exception occurs.
   */
  private void uploadFile(@NonNull final RequestBody requestFile,
                          @NonNull final String name,
                          @NonNull final String buildCode,
                          @NonNull final String versionName) throws IOException {
    final SymbolCollectorCommunicator symbolCollectorCommunicator =
        SymbolCollectorNetworkClient.getCommunicator();
    final String token = String.format("Bearer %1$s",
        BuildConfigurationManager.getInstance(project.getRootDir().getAbsolutePath()).getToken());
    final Call<ResponseBody> mappingFileUploadCall = symbolCollectorCommunicator
            .uploadMappingFile(token, buildCode, versionName, requestFile);

    logger.info("Starting to upload mapping file {} for variant {}.", name, getVariant().getName());
    final Response<ResponseBody> response = mappingFileUploadCall.execute();

    if (response.isSuccessful()) {
      logger.info("Successfully finished uploading mapping file {} for variant {}.",
          name, getVariant().getName());
    } else {
      throw new GradleException(
          String.format("Failed to upload mapping file %s for variant %s. network code %s.",
              name, getVariant().getName(), response.code()));
    }
  }
}
