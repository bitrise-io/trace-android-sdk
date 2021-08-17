package io.bitrise.trace.data.storage.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import io.bitrise.trace.data.storage.converters.CrashRequestConverter;
import io.bitrise.trace.network.CrashRequest;

/**
 * An entity for saving {@link CrashRequest} objects to the database.
 */
@Entity
public class CrashEntity {

  @PrimaryKey
  @NonNull
  private final String id;

  @TypeConverters(CrashRequestConverter.class)
  @NonNull
  private final CrashRequest crashRequest;

  /**
   * Create a {@link CrashEntity} to save to storage.
   *
   * @param id the uuid of the {@link CrashRequest}.
   * @param crashRequest the request object to save.
   */
  public CrashEntity(@NonNull String id, @NonNull CrashRequest crashRequest) {
    this.id = id;
    this.crashRequest = crashRequest;
  }

  @Ignore
  public CrashEntity(@NonNull CrashRequest crashRequest) {
    this.id = crashRequest.getMetadata().getUuid();
    this.crashRequest = crashRequest;
  }

  @NonNull
  public String getId() {
    return id;
  }

  @NonNull
  public CrashRequest getCrashRequest() {
    return crashRequest;
  }
}
