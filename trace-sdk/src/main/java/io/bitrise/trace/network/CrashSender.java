package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrashSender {

  @NonNull final CrashReport crashReport;
  final long millisecondTimestamp;
  @NonNull final String uuid;
  @NonNull final DataStorage dataStorage;
  @Nullable final String sessionId;
  
  public CrashSender(@NonNull final CrashReport crashReport,
                     @NonNull final DataStorage dataStorage) {

    this.crashReport = crashReport;
    this.millisecondTimestamp = TraceClock.getCurrentTimeMillis();
    this.uuid = UniqueIdGenerator.makeCrashReportId();
    this.dataStorage = dataStorage;

    if (ApplicationSessionManager.getInstance().getActiveSession() != null) {
      this.sessionId = ApplicationSessionManager.getInstance().getActiveSession().getUlid();
    } else {
      this.sessionId = null;
    }
  }

  @Nullable
  @WorkerThread
  Resource getSessionResources(@NonNull final String sessionId) {
    final Resource.Builder resource = Resource.newBuilder();
    resource.setType("mobile");

    final List<ResourceEntity> storedResources =
        this.dataStorage.getResourcesWithSessionId(sessionId);

    // not a valid set of resources, we should fail
    if (storedResources.size() == 0) {
      return null;
    }

    for (@NonNull final ResourceEntity resourceEntity : storedResources) {
      resource.putLabels(resourceEntity.getLabel(), resourceEntity.getValue());
    }

    resource.putLabels(ResourceLabel.APPLICATION_PLATFORM.getName(), "android");
    resource.putLabels(ResourceLabel.SESSION_ID.getName(), sessionId);

    return resource.build();
  }

  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  public void send() {

    if (sessionId == null) {
      TraceLog.e("Crash report not sent because session id was null.");
      return;
    }

    executorService.execute(() -> {

      final Resource resource = getSessionResources(this.sessionId);
      if (resource == null) {
        TraceLog.e("Crash report not sent because resources were null.");
        return;
      }

      final CrashRequest.Metadata metadata = new CrashRequest.Metadata(
          crashReport.getTitle(),
          crashReport.getDescription(),
          TraceClock.createCrashRequestFormat(this.millisecondTimestamp),
          this.uuid,
          "",
          ""
      );

      final CrashRequest request = new CrashRequest(resource, crashReport, metadata);

      NetworkClient.getCommunicator().sendCrash(request).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {

          if (response.isSuccessful()) {
            TraceLog.d("Crash report sent successfully: "
                + response.headers().get("correlation-id"));
          } else {
            TraceLog.e("Crash report failed to send: " + response.code());
          }
        }

        @Override
        public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
          TraceLog.e("Crash report failed to send: " + t.getMessage());
        }
      });

    });

  }
}
