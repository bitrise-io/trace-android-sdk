package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import io.bitrise.trace.data.dto.CrashReport;
import io.opencensus.proto.resource.v1.Resource;
import java.util.List;

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
   * @param crash the {@link CrashReport}.
   * @param metadata any additional metadata that can be supplied.
   */
  public CrashRequest(@NonNull final Resource resource,
                      @NonNull CrashReport crash,
                      @NonNull Metadata metadata) {
    this.resource = resource;
    this.crash = crash.getThreads();
    this.metadata = metadata;
  }

  @NonNull
  public List<CrashReport.Thread> getCrash() {
    return crash;
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

    /**
     * Creates a {@link Metadata} object.
     *
     * @param throwableClassName the class name of the throwable that caused the crash.
     * @param description the description of the crash to be displayed in the dashboard.
     * @param timestamp the timestamp the crash occurred, in the specific format.
     * @param uuid a unique identifier for the crash report.
     * @param traceId the current trace id (if possible).
     * @param spanId the current span id (if possible).
     */
    public Metadata(@NonNull String throwableClassName, @NonNull String description,
                    @NonNull String timestamp, @NonNull String uuid, @NonNull String traceId,
                    @NonNull String spanId) {
      this.throwableClassName = throwableClassName;
      this.description = description;
      this.timestamp = timestamp;
      this.uuid = uuid;
      this.traceId = traceId;
      this.spanId = spanId;
    }
  }
}
