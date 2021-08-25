package io.bitrise.trace.data.management;

import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.network.CrashSender;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;

/**
 * Object that can check if there is any data in the data storage that needs to be actioned on
 * application start up.
 * e.g. sending any crashes that were previously saved.
 */
public abstract class StartupMonitor {

  /**
   * Checks if there are any saved crash requests and sends them.
   *
   * @param dataStorage the current trace data storage.
   */
  public static void checkSavedCrashes(@Nonnull final DataStorage dataStorage) {

    final ExecutorService service = Executors.newFixedThreadPool(1);
    service.submit(() -> {
      final List<CrashRequest> savedRequests = dataStorage.getAllCrashRequests();

      if (savedRequests.isEmpty()) {
        TraceLog.d("No saved crash requests to send.");
        return;
      }

      TraceLog.d("Number of crash requests saved: " + savedRequests.size());

      for (CrashRequest request : savedRequests) {
        new CrashSender(request, dataStorage).send();
      }
    });
  }
}
