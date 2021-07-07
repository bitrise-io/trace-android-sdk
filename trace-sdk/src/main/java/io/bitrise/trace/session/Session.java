package io.bitrise.trace.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.azam.ulidj.ULID;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class for sessions.
 */
public class Session {

  /**
   * The {@link ULID} - A Universally Unique Lexicographically Sortable Identifier.
   * Similar to a {@link java.util.UUID} but it can be sorted alphabetically and in a time manner.
   */
  @NonNull
  private final String ulid;

  /**
   * Constructor for class.
   */
  public Session() {
    this.ulid = ULID.random(new SecureRandom());
  }

  /**
   * Constructor with the sessionId to use - used only for testing purposes.
   *
   * @param sessionId a ulid to use for the session id.
   */
  @VisibleForTesting
  public Session(@NonNull final String sessionId) {
    this.ulid = sessionId;
  }

  /**
   * Gets the ULID of the Session.
   *
   * @return the ULID.
   */
  @NonNull
  public String getUlid() {
    return ulid;
  }

  @NonNull
  private final List<ResourceEntity> storedResources = new ArrayList<>();


  @Nullable
  public Resource getResources() {
    // not a valid set of resources, we should fail
    if (storedResources.size() == 0) {
      TraceLog.d("session manager - stored resources size is 0");
      return null;
    }

    final Resource.Builder resource = Resource.newBuilder();
    resource.setType("mobile");

    for (@NonNull final ResourceEntity resourceEntity : storedResources) {
      resource.putLabels(resourceEntity.getLabel(), resourceEntity.getValue());
    }

    resource.putLabels(ResourceLabel.APPLICATION_PLATFORM.getName(), "android");
    resource.putLabels(ResourceLabel.SESSION_ID.getName(), ulid);

    return resource.build();
  }

  public void addResourceEntity(@NonNull final ResourceEntity resourceEntity) {
    this.storedResources.add(resourceEntity);
  }
}
