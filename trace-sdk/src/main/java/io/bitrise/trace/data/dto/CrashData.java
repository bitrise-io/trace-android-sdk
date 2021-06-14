package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * Data object that contains the original information from a crash.
 */
public class CrashData {

  @NonNull
  private final Throwable throwable;

  @NonNull
  private final Map<Thread, StackTraceElement[]> allStackTraces;

  public CrashData(@NonNull Throwable throwable, @NonNull
      Map<Thread, StackTraceElement[]> allStackTraces) {
    this.throwable = throwable;
    this.allStackTraces = allStackTraces;
  }

  @NonNull
  public Throwable getThrowable() {
    return throwable;
  }

  @NonNull
  public Map<Thread, StackTraceElement[]> getAllStackTraces() {
    return allStackTraces;
  }
}
