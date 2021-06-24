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

  @NonNull final String title;
  @NonNull final String description;

  public CrashReport(
      @NonNull List<Thread> threads,
      @NonNull final String title,
      @NonNull final String description) {
    this.threads = threads;
    this.title = title;
    this.description = description;
  }

  @NonNull
  public List<Thread> getThreads() {
    return threads;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  @NonNull
  public String getDescription() {
    return description;
  }

  public static class Thread {
    @SerializedName("threadId")
    final long threadId;

    @SerializedName("isRequesting")
    final boolean wasThreadThatCrashed;

    @NonNull List<Frame> frames;

    public Thread(final long threadId,
                  final boolean wasThreadThatCrashed,
                  @NonNull final List<Frame> stackTrace) {
      this.threadId = threadId;
      this.wasThreadThatCrashed = wasThreadThatCrashed;
      this.frames = stackTrace;
    }

    @Nullable
    public Frame getFirstFrame() {
      if (this.frames.size() == 0){
        return null;
      }
      return this.frames.get(0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof Thread) {
        final Thread thread = (Thread) obj;

        return  thread.frames.equals(this.frames) &&
            thread.threadId == this.threadId &&
            thread.wasThreadThatCrashed == this.wasThreadThatCrashed;
      }
      return false;
    }
  }

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
        final Frame frame = (Frame)obj;

        return frame.packageName.equals(this.packageName) &&
            frame.functionName.equals(this.functionName) &&
            frame.fileName.equals(this.fileName) &&
            frame.lineNo == this.lineNo &&
            frame.sequenceNumber == this.sequenceNumber;
      }
      return false;
    }
  }
}
