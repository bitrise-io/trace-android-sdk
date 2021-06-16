package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrashSender {

  @NonNull final CrashReport crashReport;
  final long millisecondTimestamp;
  @NonNull final String uuid;
  @NonNull final File cacheDirectory;
  @NonNull final DataStorage dataStorage;
  @Nullable final String sessionId;

  
  public CrashSender(@NonNull final CrashReport crashReport,
                     @NonNull final File cacheDirectory,
                     @NonNull final DataStorage dataStorage) {

    this.crashReport = crashReport;
    this.millisecondTimestamp = TraceClock.getCurrentTimeMillis();
    this.uuid = UniqueIdGenerator.makeCrashReportId();
    this.cacheDirectory = cacheDirectory;
    this.dataStorage = dataStorage;

    if (ApplicationSessionManager.getInstance().getActiveSession() != null) {
      this.sessionId = ApplicationSessionManager.getInstance().getActiveSession().getUlid();
    } else {
      this.sessionId = null;
    }

    TraceLog.d("finished creating crash sender");
  }

  public void send() {
    TraceLog.d("called send for the crash sender.");

    final Map<String, RequestBody> fields = prepareAllFields();
    final File crashReportFile = prepareCrashReportFile();

    //todo what if this is null
    RequestBody filePart = RequestBody.create(MediaType.parse("text/plain"), crashReportFile);

    NetworkClient.getCommunicator().sendCrashes(fields, filePart).enqueue(new Callback<Void>() {
      @Override
      public void onResponse(@NonNull final Call call,
                             @NonNull final Response response) {
        if (response.isSuccessful()) {
          TraceLog.d("Crash report sent successfully: " +
              response.headers().get("correlation-id"));
        } else {
          TraceLog.e("Crash report failed to send: " + response.code());
        }
      }

      @Override
      public void onFailure(@NonNull final Call call, @NonNull final Throwable t) {
        TraceLog.e("Crash report failed to send: " + t.getMessage());
      }
    });
  }

  protected Map<String, RequestBody> prepareAllFields() {
    Map<String, RequestBody> fieldData = new HashMap<>();

    // add the timestamp
    fieldData.put("timestamp", RequestBody.create(MediaType.parse("text/plain"),
            TraceClock.createCrashRequestFormat(millisecondTimestamp)));

    // add the uuid
    fieldData.put("sdk_event_identifier", RequestBody.create(MediaType.parse("text/plain"), uuid));

    fieldData.put("platform", RequestBody.create(MediaType.parse("text/plain"), "android"));

    // encode all the resource labels
//    fieldData.putAll(convertResourceToFields(resource));

    //todo we need to collect proper resources - this is temporary!
    fieldData.put("app_version", RequestBody.create(MediaType.parse("text/plain"), "1.2.3"));
    fieldData.put("build_id", RequestBody.create(MediaType.parse("text/plain"), "123"));
    fieldData.put("os_version", RequestBody.create(MediaType.parse("text/plain"), "10"));
    fieldData.put("device_type", RequestBody.create(MediaType.parse("text/plain"), "pixel"));


    // add session info
    if (this.sessionId != null) {
      fieldData.put("crashed_without_session",
          RequestBody.create(MediaType.parse("text/plain"),"false"));
      fieldData.put("session_id", RequestBody.create(MediaType.parse("text/plain"), sessionId));
    } else {
      fieldData.put("crashed_without_session",
          RequestBody.create(MediaType.parse("text/plain"), "true"));
    }

    return fieldData;
  }

  protected Map<String, String> convertResourceToFields(@NonNull final Resource resource ) {

    final Map<String, String> resourceFields = new HashMap<>();

    if (resource.containsLabels(ResourceLabel.APPLICATION_VERSION_NAME.getName())) {
      resourceFields.put("app_version", resource
              .getLabelsOrDefault(ResourceLabel.APPLICATION_VERSION_NAME.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.APPLICATION_VERSION_CODE.getName())) {
      resourceFields.put("build_id", resource
          .getLabelsOrDefault(ResourceLabel.APPLICATION_VERSION_CODE.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.DEVICE_OS.getName())) {
      resourceFields.put("os_version", resource
          .getLabelsOrDefault(ResourceLabel.DEVICE_OS.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.DEVICE_TYPE.getName())) {
      resourceFields.put("device_type", resource
          .getLabelsOrDefault(ResourceLabel.DEVICE_TYPE.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.DEVICE_LOCALE.getName())) {
      resourceFields.put("device_country", resource
          .getLabelsOrDefault(ResourceLabel.DEVICE_LOCALE.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.DEVICE_NETWORK.getName())) {
      resourceFields.put("device_network", resource
          .getLabelsOrDefault(ResourceLabel.DEVICE_NETWORK.getName(), ""));
    }

    if (resource.containsLabels(ResourceLabel.DEVICE_CARRIER.getName())) {
      resourceFields.put("device_carrier", resource
          .getLabelsOrDefault(ResourceLabel.DEVICE_CARRIER.getName(), ""));
    }

    return resourceFields;
  }

  @Nullable
  protected File prepareCrashReportFile() {
    // todo check if we already had an old report - we should remove that.

    try {
      File reportFile = File.createTempFile("traceCrashReport", ".txt", cacheDirectory);

      FileWriter fw = new FileWriter(reportFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(convertCrashReportToJson());
      bw.close();

      TraceLog.d("Successfully created crash report txt file.");
      return reportFile;
    } catch (IOException e) {
      TraceLog.e(e, "Failed to create crash report txt file.");
      return null;
    }
  }

  @NonNull
  protected String convertCrashReportToJson() {
    return NetworkClient.getGson().toJson(crashReport);
  }

}
