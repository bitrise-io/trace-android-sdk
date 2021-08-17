package io.bitrise.trace.data.storage.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import io.bitrise.trace.data.storage.converters.CrashRequestConverter;
import io.bitrise.trace.data.trace.TraceConverter;
import io.bitrise.trace.network.CrashRequest;

@Entity
public class CrashEntity {

  @PrimaryKey
  @NonNull
  private final String id;

  @TypeConverters(CrashRequestConverter.class)
  @NonNull
  private final CrashRequest crashRequest;

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
