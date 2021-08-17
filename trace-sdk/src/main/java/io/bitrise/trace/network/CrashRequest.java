package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.utils.TraceClock;
import io.opencensus.proto.resource.v1.Resource;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The complete object that forms the payload to the backend for a crash report.
 */
public class CrashRequest extends NetworkRequest {

  @NonNull
  private final List<CrashReport.Thread> crash;

  @NonNull private final Metadata metadata;

  /**
   * Creates the crash request body.
   *
   * @param resource the current session's resources.
   * @param
   *
   */
  public CrashRequest(@NonNull final Resource resource,
                      @NonNull final CrashReport crashReport,
                      final long millisecondTimestamp,
                      @NonNull final String uuid,
                      @NonNull final String traceId,
                      @NonNull final String spanId) {
    this.resource = resource;
    this.crash = crashReport.getThreads();
    this.metadata = new CrashRequest.Metadata(
        crashReport.getThrowableClassName(),
        crashReport.getDescription(),
        TraceClock.createCrashRequestFormat(millisecondTimestamp, TimeZone.getDefault()),
        uuid,
        traceId,
        spanId,
        crashReport.getAllExceptionNames()
    );
  }

  @NonNull
  public List<CrashReport.Thread> getCrash() {
    return crash;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof CrashRequest)) {
      return false;
    }

    final CrashRequest request = (CrashRequest) obj;

    return request.crash.equals(this.crash)
        && request.metadata.equals(this.metadata)
        && request.resource.equals(this.resource);
  }

  /**
   * An object that is sent with a crash report that contains any additional information that
   * did not originally come with the crash report, or resources e.g. the crash timestamp.
   */
  public static class Metadata {

    @SerializedName("throwableClassName")
    @NonNull final String throwableClassName;
    @NonNull final String description;
    @NonNull final String timestamp;
    @NonNull final String uuid;
    @SerializedName("traceId")
    @NonNull final String traceId;
    @SerializedName("spanId")
    @NonNull final String spanId;
    @SerializedName("allExceptionNames")
    @Nullable final String allExceptionNames;

    /**
     * Creates a {@link Metadata} object.
     *
     * @param throwableClassName the class name of the throwable that caused the crash.
     * @param description the description of the crash to be displayed in the dashboard.
     * @param timestamp the timestamp the crash occurred, in the specific format.
     * @param uuid a unique identifier for the crash report.
     * @param traceId the current trace id (if possible).
     * @param spanId the current span id (if possible).
     * @param allExceptionNames a list of all the exception class names if it was a nested
     *                          exception.
     */
    public Metadata(@NonNull String throwableClassName, @NonNull String description,
                    @NonNull String timestamp, @NonNull String uuid, @NonNull String traceId,
                    @NonNull String spanId, @Nullable String allExceptionNames) {
      this.throwableClassName = throwableClassName;
      this.description = description;
      this.timestamp = timestamp;
      this.uuid = uuid;
      this.traceId = traceId;
      this.spanId = spanId;
      this.allExceptionNames = allExceptionNames;
    }

    @NonNull
    public String getUuid() {
      return uuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof CrashRequest.Metadata)) {
        return false;
      }
      final Metadata metadata = (Metadata) obj;

      return metadata.description.equals(this.description)
          && metadata.spanId.equals(this.spanId)
          && metadata.throwableClassName.equals(this.throwableClassName)
          && metadata.timestamp.equals(this.timestamp)
          && metadata.traceId.equals(this.traceId)
          && metadata.uuid.equals(this.uuid)
          && Objects.equals(metadata.allExceptionNames, this.allExceptionNames);
    }
  }

  @NonNull
  public Metadata getMetadata() {
    return metadata;
  }
}
