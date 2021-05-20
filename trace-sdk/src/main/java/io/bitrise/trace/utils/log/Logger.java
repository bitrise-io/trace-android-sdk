package io.bitrise.trace.utils.log;

import androidx.annotation.NonNull;

/**
 * The interface for which all loggers must conform too, taken from the android.util.Log methods.
 */
public interface Logger {

  @SuppressWarnings("checkstyle:MethodName")
  void d(@NonNull final String tag, @NonNull final String message);

  @SuppressWarnings("checkstyle:MethodName")
  void e(@NonNull final String tag, @NonNull final String message);

  @SuppressWarnings("checkstyle:MethodName")
  void i(@NonNull final String tag, @NonNull final String message);

  @SuppressWarnings("checkstyle:MethodName")
  void v(@NonNull final String tag, @NonNull final String message);

  @SuppressWarnings("checkstyle:MethodName")
  void w(@NonNull final String tag, @NonNull final String message);

}
