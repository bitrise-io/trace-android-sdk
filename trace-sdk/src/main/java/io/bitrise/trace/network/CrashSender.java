package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.utils.log.TraceLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Responsible for sending a {@link CrashRequest} to the server.
 */
public class CrashSender {

  @NonNull final CrashRequest request;

  /**
   * Creates an object that can send crash reports.
   *
   * @param request - the {@link CrashRequest} object to send.
   */
  public CrashSender(@NonNull final CrashRequest request) {
    this.request = request;
  }

  /**
   * Send the {@link CrashRequest} to the backend server.
   */
  public void send() {
    NetworkClient.getCommunicator().sendCrash(request).enqueue(new Callback<Void>() {
      @Override
      public void onResponse(@NonNull final Call<Void> call,
                             @NonNull final Response<Void> response) {
        if (response.isSuccessful()) {
          TraceLog.d("Crash report sent successfully: "
              + response.headers().get("correlation-id"));
        } else {
          TraceLog.e("Crash report failed to send: " + response.code());
        }
      }

      @Override
      public void onFailure(@NonNull final Call<Void> call,
                            @NonNull final Throwable t) {
        TraceLog.e("Crash report failed to send: " + t.getLocalizedMessage());
      }
    });
  }
}
