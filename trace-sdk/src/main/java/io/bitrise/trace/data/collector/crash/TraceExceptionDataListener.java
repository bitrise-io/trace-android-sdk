package io.bitrise.trace.data.collector.crash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.management.formatter.crash.ExceptionDataFormatter;

/**
 * Listens for any unhandled exceptions that get thrown and passes them to the
 * {@link ExceptionDataFormatter}.
 */
public class TraceExceptionDataListener extends BaseDataListener
    implements Thread.UncaughtExceptionHandler {

  @VisibleForTesting
  @Nullable
  Thread.UncaughtExceptionHandler previousHandler;

  @Override
  public void uncaughtException(@NonNull Thread t,
                                @NonNull Throwable e) {

    onDataCollected(new CrashData(e, Thread.getAllStackTraces()));

    // if there is another handler - we should be good citizens and pass it down to them too.
    if (previousHandler != null) {
      previousHandler.uncaughtException(t, e);
    }
  }

  public void onDataCollected(final @NonNull CrashData crashData) {
    dataManager.handleReceivedCrash(crashData);
  }

  @Override
  public void startCollecting() {
    previousHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
    setActive(true);
  }

  @Override
  public void stopCollecting() {
    Thread.setDefaultUncaughtExceptionHandler(previousHandler);
    setActive(false);
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }
}
