package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * Data object that contains the original information from a crash.
 */
public class CrashData {

  @NonNull
  private final Throwable throwable;

  private final long crashedThreadId;

  @NonNull
  private final Map<Thread, StackTraceElement[]> allStackTraces;

  /**
   * Creates a {@link CrashData} object.
   *
   * @param throwable the throwable that got raised.
   * @param crashedThreadId the thread id that the throwable got thrown on.
   * @param allStackTraces a copy of all the threads and stacktraces currently in memory.
   */
  public CrashData(@NonNull Throwable throwable,
                   long crashedThreadId,
                   @NonNull Map<Thread, StackTraceElement[]> allStackTraces) {
    this.throwable = throwable;
    this.crashedThreadId = crashedThreadId;
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

  public long getCrashedThreadId() {
    return crashedThreadId;
  }
}
