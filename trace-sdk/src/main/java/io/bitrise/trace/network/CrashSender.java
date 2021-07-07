package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Responsible for sending a {@link CrashRequest} to the server.
 */
public class CrashSender {

  @NonNull final CrashReport crashReport;
  final long millisecondTimestamp;
  @NonNull final String uuid;
  @NonNull final Resource resource;

  /**
   * Creates an object that can send crash reports.
   *
   * @param crashReport the {@link CrashReport} to send.
   */
  public CrashSender(@NonNull final CrashReport crashReport,
                     @NonNull final Resource resource) {

    this.crashReport = crashReport;
    this.millisecondTimestamp = TraceClock.getCurrentTimeMillis();
    this.uuid = UniqueIdGenerator.makeCrashReportId();
    this.resource = resource;
  }

  /**
   * Send the {@link CrashRequest} to the backend server.
   */
  public void send() {

    final CrashRequest.Metadata metadata = new CrashRequest.Metadata(
        crashReport.getTitle(),
        crashReport.getDescription(),
        TraceClock.createCrashRequestFormat(millisecondTimestamp),
        this.uuid,
        "",
        ""
    );

    final CrashRequest request = new CrashRequest(resource, crashReport, metadata);

    NetworkClient.getCommunicator().sendCrash(request).enqueue(new Callback<Void>() {
      @Override
      public void onResponse(@NonNull final Call<Void> call,
                             @NonNull final Response<Void> response) {

        if (response.isSuccessful()) {
          TraceLog.d("Crash report sent successfully: " + response.headers().get("correlation-id"));
        } else {
          TraceLog.e("Crash report failed to send: " + response.code());
        }
      }

      @Override
      public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
        TraceLog.e("Crash report failed to send: " + t.getMessage());
      }
    });

  }
}
