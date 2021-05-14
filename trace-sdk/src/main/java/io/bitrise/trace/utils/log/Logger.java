package io.bitrise.trace.utils.log;

import androidx.annotation.NonNull;

/**
 * The interface for which all loggers must conform too, taken from the android.util.Log methods.
 */
public interface Logger {

  void d(@NonNull final String tag, @NonNull final String message);

  void e(@NonNull final String tag, @NonNull final String message);

  void i(@NonNull final String tag, @NonNull final String message);

  void v(@NonNull final String tag, @NonNull final String message);

  void w(@NonNull final String tag, @NonNull final String message);

}
