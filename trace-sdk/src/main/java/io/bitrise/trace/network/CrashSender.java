package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        crashReport.getThrowableClassName(),
        crashReport.getDescription(),
        TraceClock.createCrashRequestFormat(millisecondTimestamp, TimeZone.getDefault()),
        this.uuid,
        "",
        "",
        crashReport.getAllExceptionNames()
    );

    final CrashRequest request = new CrashRequest(resource, crashReport, metadata);

    final ExecutorService service = Executors.newFixedThreadPool(1);
    Future<?> future = service.submit(() -> {

      try {

        final Response<Void> response =
            NetworkClient.getCommunicator().sendCrash(request).execute();

        if (response.isSuccessful()) {
          TraceLog.d("Crash report sent successfully: " + response.headers().get("correlation-id"));
        } else {
          TraceLog.e("Crash report failed to send: " + response.code());
        }

      } catch (IOException e) {
        TraceLog.e("Crash report failed to send: " + e.getLocalizedMessage());
      }

    });

    try {
      future.get();
    } catch (ExecutionException | InterruptedException e) {
      // nop
    }
  }
}
