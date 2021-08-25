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

  private int sentAttempts;

  /**
   * Create a {@link CrashEntity} to save to storage.
   *
   * @param id the uuid of the {@link CrashRequest}.
   * @param crashRequest the request object to save.
   */
  public CrashEntity(@NonNull String id, @NonNull CrashRequest crashRequest) {
    this.id = id;
    this.crashRequest = crashRequest;
    sentAttempts = 0;
  }

  /**
   * Creates a {@link CrashEntity} to save to storage.
   *
   * @param crashRequest the {@link CrashRequest} object to hold.
   */
  @Ignore
  public CrashEntity(@NonNull CrashRequest crashRequest) {
    this.id = crashRequest.getMetadata().getUuid();
    this.crashRequest = crashRequest;
    sentAttempts = 0;
  }

  @NonNull
  public String getId() {
    return id;
  }

  @NonNull
  public CrashRequest getCrashRequest() {
    return crashRequest;
  }

  /**
   * Update the counter for the number of attempts this request has tried to send.
   */
  public void updateSentAttempts() {
    this.sentAttempts++;
  }

  /**
   * Gets the current number of attempts to send the request.
   *
   * @return the number of attempts to send the request.
   */
  public int getSentAttempts() {
    return sentAttempts;
  }

  /**
   * Sets the number of attempts to send a request.
   *
   * @param sentAttempts the number of attempts so far.
   */
  public void setSentAttempts(int sentAttempts) {
    this.sentAttempts = sentAttempts;
  }
}
