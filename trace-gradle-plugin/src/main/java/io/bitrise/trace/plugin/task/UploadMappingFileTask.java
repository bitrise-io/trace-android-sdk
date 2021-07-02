package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.plugin.TraceGradlePlugin;
import io.bitrise.trace.plugin.configuration.BuildConfigurationManager;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import io.bitrise.trace.plugin.network.SymbolCollectorCommunicator;
import io.bitrise.trace.plugin.network.SymbolCollectorNetworkClient;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
    final MultipartBody.Part body =
        MultipartBody.Part.createFormData("mapping file", file.getName(), requestFile);
    final String buildId = GenerateBuildIdTask.readBuildIdFromFile(project.getBuildDir(),
        getVariant().getName());
    uploadFile(requestFile, body, file.getName(), buildId);
  }

  /**
   * Uploads the given File to the backend, so later an obfuscated crash report can be retraced.
   *
   * @param requestFile the file itself as a multipart body.
   * @param body        the body of he request.
   * @param name        the name of the file.
   * @param buildId     the build ID for the file.
   * @throws IOException if any I/O exception occurs.
   */
  private void uploadFile(@NonNull final RequestBody requestFile,
                          @NonNull final MultipartBody.Part body, @NonNull final String name,
                          @NonNull final String buildId) throws IOException {
    final SymbolCollectorCommunicator symbolCollectorCommunicator =
        SymbolCollectorNetworkClient.getCommunicator();
    final String token = String.format("Bearer %1$s",
        BuildConfigurationManager.getInstance(project.getRootDir().getAbsolutePath()).getToken());
    final Call<ResponseBody> mappingFileUploadCall =
        symbolCollectorCommunicator.uploadMappingFile(token, buildId, requestFile, body);
    logger.info("Starting to upload mapping file {} for variant {}.", name, getVariant().getName());
    final Response<ResponseBody> response = mappingFileUploadCall.execute();
    if (response.isSuccessful()) {
      logger.info("Successfully finished uploading mapping file {} for variant {}.",
          name, getVariant().getName());
    } else {
      throw new GradleException(
          String.format("Failed to upload mapping file %s for variant %s.", name,
              getVariant().getName()));
    }
  }
}
