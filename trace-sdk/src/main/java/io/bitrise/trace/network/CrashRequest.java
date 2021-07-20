package io.bitrise.trace.network;

import androidx.annotation.NonNull;
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

    @NonNull final String title;
    @NonNull final String description;
    @NonNull final String timestamp;
    @NonNull final String uuid;
    @NonNull final String traceid;
    @NonNull final String spanid;

    /**
     * Creates a {@link Metadata} object.
     *
     * @param title the title for the crash to be displayed in the dashboard.
     * @param description the description of the crash to be displayed in the dashboard.
     * @param timestamp the timestamp the crash occurred, in the specific format.
     * @param uuid a unique identifier for the crash report.
     * @param traceid the current trace id (if possible).
     * @param spanid the current span id (if possible).
     */
    public Metadata(@NonNull String title, @NonNull String description,
                    @NonNull String timestamp, @NonNull String uuid, @NonNull String traceid,
                    @NonNull String spanid) {
      this.title = title;
      this.description = description;
      this.timestamp = timestamp;
      this.uuid = uuid;
      this.traceid = traceid;
      this.spanid = spanid;
    }
  }
}
