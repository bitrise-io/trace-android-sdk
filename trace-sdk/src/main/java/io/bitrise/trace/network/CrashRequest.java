package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.CrashReport;
import io.opencensus.proto.resource.v1.Resource;
import java.util.List;

public class CrashRequest extends NetworkRequest {

  @NonNull
  private final List<CrashReport.Thread> crash;

  @NonNull private final Metadata metadata;

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


  public static class Metadata {

    @NonNull final String title;
    @NonNull final String description;
    @NonNull final String timestamp;
    @NonNull final String uuid;
    @NonNull final String traceid;
    @NonNull final String spanid;

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
