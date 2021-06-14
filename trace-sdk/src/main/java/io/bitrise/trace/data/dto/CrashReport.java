package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * Data object that contains a formatted crash report to send to the backend.
 */
public class CrashReport {

  @NonNull
  final String throwable;

  @NonNull
  final Map<String, String> threads;


  public CrashReport(@NonNull final String throwable,
                     @NonNull final Map<String, String> threads) {
    this.throwable = throwable;
    this.threads = threads;
  }

  @NonNull
  public String getThrowable() {
    return throwable;
  }

  @NonNull
  public Map<String, String> getThreads() {
    return threads;
  }
}
