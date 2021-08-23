package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Responsible for sending a {@link CrashRequest} to the server.
 */
public class CrashSender {

  @NonNull final CrashRequest request;
  @NonNull final DataStorage dataStorage;

  /**
   * Creates an object that can send crash reports.
   *
   * @param request - the {@link CrashRequest} object to send.
   */
  public CrashSender(@NonNull final CrashRequest request,
                     @Nonnull final DataStorage dataStorage) {
    this.request = request;
    this.dataStorage = dataStorage;
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
          removeCrash();
        } else {
          TraceLog.e("Crash report failed to send: " + response.code());
          updateSentAttemptsCounter();
        }
      }

      @Override
      public void onFailure(@NonNull final Call<Void> call,
                            @NonNull final Throwable t) {
        TraceLog.e("Crash report failed to send: " + t.getLocalizedMessage());
        updateSentAttemptsCounter();
      }
    });
  }

  private void removeCrash() {
    final ExecutorService service = Executors.newFixedThreadPool(1);
    service.submit(() -> {
      dataStorage.deleteCrashRequest(request.getMetadata().getUuid());
    });
  }

  private void updateSentAttemptsCounter() {
    final ExecutorService service = Executors.newFixedThreadPool(1);
    service.submit(() -> {
      dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());
    });
  }
}
