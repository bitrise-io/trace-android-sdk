package io.bitrise.trace.data.collector.crash;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.utils.log.TraceLog;

/**
 * Listens for any unhandled exceptions that get thrown.
 */
public class TraceExceptionDataListener extends BaseDataListener
    implements Thread.UncaughtExceptionHandler {

  @VisibleForTesting
  @Nullable
  Thread.UncaughtExceptionHandler previousHandler;

  public TraceExceptionDataListener(@NonNull final Context context) {
    this.dataManager = DataManager.getInstance(context);
  }

  @Override
  public void uncaughtException(@NonNull Thread t,
                                @NonNull Throwable e) {
    TraceLog.d("found a crash!");

    onDataCollected(new CrashData(e, Thread.getAllStackTraces()));

    // if there is another handler - we should be good citizens and pass it down to them too.
    if (previousHandler != null) {
      previousHandler.uncaughtException(t, e);
      TraceLog.d("passed the crash down to the next handler");
    }
  }

  public void onDataCollected(final @NonNull CrashData crashData) {
    TraceLog.d("collected crash");
    dataManager.handleReceivedCrash(crashData);
  }

  @Override
  public void startCollecting() {
    previousHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
    TraceLog.d("start listening for crashes");
    setActive(true);
  }

  @Override
  public void stopCollecting() {
    Thread.setDefaultUncaughtExceptionHandler(previousHandler);
    TraceLog.d("stop listening for crashes");
    setActive(false);
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }
}
