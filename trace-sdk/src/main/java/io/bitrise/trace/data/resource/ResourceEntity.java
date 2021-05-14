package io.bitrise.trace.data.resource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import java.util.Objects;

/**
 * {@link androidx.room.Entity} for storing {@link io.opencensus.proto.resource.v1.Resource}
 * labels in {@link DataStorage}.
 */
@Entity
public class ResourceEntity {

  @PrimaryKey
  @NonNull
  private String id;
  @NonNull
  private String label;
  @NonNull
  private String value;
  @NonNull
  private String sessionId;

  /**
   * Constructor for class. Used by Room
   */
  public ResourceEntity() {
    // nop
  }

  /**
   * Constructor for class.
   *
   * @param label the label.
   * @param value the value.
   */
  @Ignore
  public ResourceEntity(@NonNull final String label, @NonNull final String value) {
    this.label = label;
    this.value = value;
    this.sessionId = getCurrentSessionId();
    this.id = sessionId + label;
  }

  /**
   * Gets the active {@link Session}'s ID, or throws IllegalStateException when there is no
   * active Session.
   *
   * @return the Session ID.
   */
  @NonNull
  private String getCurrentSessionId() {
    final Session activeSession = ApplicationSessionManager.getInstance().getActiveSession();
    if (activeSession == null) {
      throw new IllegalStateException("There must be an active Session to create ResourceEntity!");
    }
    return activeSession.getUlid();
  }

  @NonNull
  public String getLabel() {
    return label;
  }

  public void setLabel(@NonNull final String label) {
    this.label = label;
  }

  @NonNull
  public String getValue() {
    return value;
  }

  public void setValue(@NonNull final String value) {
    this.value = value;
  }

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(@NonNull final String id) {
    this.id = id;
  }

  @NonNull
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(@NonNull final String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(@Nullable final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ResourceEntity)) {
      return false;
    }
    final ResourceEntity resourceEntity = (ResourceEntity) o;
    return resourceEntity.label.equals(label)
        && resourceEntity.id.equals(id)
        && resourceEntity.sessionId.equals(sessionId)
        && resourceEntity.value.equals(value);
  }

  @Override
  public String toString() {
    return "ResourceEntity{"
        + "id='" + id + '\''
        + ", label='" + label + '\''
        + ", value='" + value + '\''
        + ", sessionId='" + sessionId + '\''
        + '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, value, sessionId);
  }
}
