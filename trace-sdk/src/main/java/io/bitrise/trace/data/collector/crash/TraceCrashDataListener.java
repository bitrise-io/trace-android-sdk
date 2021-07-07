package io.bitrise.trace.data.collector.crash;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.data.collector.BaseDataListener;
import io.bitrise.trace.data.dto.CrashData;
import io.bitrise.trace.data.management.DataManager;

/**
 * Listens for any unhandled exceptions that get thrown.
 */
public class TraceCrashDataListener extends BaseDataListener
    implements Thread.UncaughtExceptionHandler {

  @VisibleForTesting
  @Nullable
  Thread.UncaughtExceptionHandler previousHandler;

  public TraceCrashDataListener(@NonNull final Context context) {
    this.dataManager = DataManager.getInstance(context);
  }

  @Override
  public void uncaughtException(@NonNull Thread t,
                                @NonNull Throwable e) {
    // todo: can we get the thread id that crashed
    //  calling t.getId returns this thread id, as does Thread.currentThread.getId
    onDataCollected(new CrashData(e, 0, Thread.getAllStackTraces()));

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
