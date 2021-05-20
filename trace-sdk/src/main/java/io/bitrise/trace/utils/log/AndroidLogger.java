package io.bitrise.trace.utils.log;

import android.util.Log;
import androidx.annotation.NonNull;

/**
 * An android flavour logger, that will call the android.util.Log methods and display to Logcat.
 */
public class AndroidLogger implements Logger {

  @Override
  public void d(@NonNull String tag, @NonNull String message) {
    Log.d(tag, message);
  }

  @Override
  public void e(@NonNull String tag, @NonNull String message) {
    Log.e(tag, message);
  }

  @Override
  public void i(@NonNull String tag, @NonNull String message) {
    Log.i(tag, message);
  }

  @Override
  public void v(@NonNull String tag, @NonNull String message) {
    Log.v(tag, message);
  }

  @Override
  public void w(@NonNull String tag, @NonNull String message) {
    Log.w(tag, message);
  }
}
