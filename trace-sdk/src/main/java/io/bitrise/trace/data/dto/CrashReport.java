package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Data object that contains a formatted crash report to send to the backend.
 */
public class CrashReport {

  @NonNull
  final List<Thread> threads;

  @NonNull final String throwableClassName;
  @NonNull final String description;
  @Nullable final String allExceptionNames;

  /**
   * Creates a CrashReport object with the following data.
   *
   * @param threads the {@link CrashReport.Thread} threads that were running.
   * @param throwableClassName the class name of the throwable that caused the crash.
   * @param description the description for the report.
   * @param allExceptionNames a list of all the exception class names if it was a nested exception.
   */
  public CrashReport(
      @NonNull List<Thread> threads,
      @NonNull final String throwableClassName,
      @NonNull final String description,
      @Nullable final String allExceptionNames) {
    this.threads = threads;
    this.throwableClassName = throwableClassName;
    this.description = description;
    this.allExceptionNames = allExceptionNames;
  }

  @NonNull
  public List<Thread> getThreads() {
    return threads;
  }

  @NonNull
  public String getThrowableClassName() {
    return throwableClassName;
  }

  @NonNull
  public String getDescription() {
    return description;
  }

  @Nullable
  public String getAllExceptionNames() {
    return allExceptionNames;
  }

  /**
   * Data object for a Thread. This matches what the backend expects to receive.
   */
  public static class Thread {
    @SerializedName("threadId")
    final long threadId;

    @SerializedName("isRequesting")
    final boolean wasThreadThatCrashed;

    @SerializedName("frames")
    @NonNull List<Frame> frames;

    /**
     * Creates a Thread object with the following data.
     *
     * @param threadId the id of the thread that we're capturing.
     * @param wasThreadThatCrashed true if this was the original thread that crashed.
     * @param stackTrace the list of {@link Frame}'s that make up the stacktrace.
     */
    public Thread(final long threadId,
                  final boolean wasThreadThatCrashed,
                  @NonNull final List<Frame> stackTrace) {
      this.threadId = threadId;
      this.wasThreadThatCrashed = wasThreadThatCrashed;
      this.frames = stackTrace;
    }

    /**
     * Get the first {@link Frame} of the stacktrace frames. This can be used to create the
     * crash title.
     *
     * @return the first {@link Frame} of the stacktrace frames. Returns null if there was no
     *     initial stacktrace frame.
     */
    @Nullable
    public Frame getFirstFrame() {
      if (this.frames.size() == 0) {
        return null;
      }
      return this.frames.get(0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof Thread) {
        final Thread thread = (Thread) obj;

        return  thread.frames.equals(this.frames)
            && thread.threadId == this.threadId
            && thread.wasThreadThatCrashed == this.wasThreadThatCrashed;
      }
      return false;
    }
  }

  /**
   * Data object for a stacktrace frame. This matches what the backend expects to receive.
   */
  public static class Frame {
    @SerializedName("package")
    @NonNull final String packageName;

    @SerializedName("function")
    @NonNull final String functionName;

    @SerializedName("filename")
    @NonNull final String fileName;

    @SerializedName("lineno")
    final int lineNo;

    @SerializedName("sequencenumber")
    final int sequenceNumber;

    /**
     * Creates a Frame object with the following data.
     *
     * @param packageName the stacktrace element's package name.
     * @param functionName the stacktrace element's package name.
     * @param fileName the stacktrace element's file name.
     * @param lineNo the stacktrace element's package name.
     * @param sequenceNumber the index for which this stacktrace occurred e.g. 0 is the first item.
     */
    public Frame(@NonNull String packageName,
                 @NonNull String functionName,
                 @NonNull String fileName,
                 int lineNo,
                 int sequenceNumber) {
      this.packageName = packageName;
      this.functionName = functionName;
      this.fileName = fileName;
      this.lineNo = lineNo;
      this.sequenceNumber = sequenceNumber;
    }

    @NonNull
    public String getPackageName() {
      return packageName;
    }

    @NonNull
    public String getFunctionName() {
      return functionName;
    }

    @NonNull
    public String getFileName() {
      return fileName;
    }

    public int getLineNo() {
      return lineNo;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof Frame) {
        final Frame frame = (Frame) obj;

        return frame.packageName.equals(this.packageName)
            && frame.functionName.equals(this.functionName)
            && frame.fileName.equals(this.fileName)
            && frame.lineNo == this.lineNo
            && frame.sequenceNumber == this.sequenceNumber;
      }
      return false;
    }
  }
}
