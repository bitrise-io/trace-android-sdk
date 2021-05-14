package io.bitrise.trace.data.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Room;
import io.bitrise.trace.data.trace.TraceEntity;
import io.opencensus.proto.metrics.v1.Metric;

/**
 * Responsible for {@link Metric} and {@link TraceEntity} storing in memory. Use for testing
 * purposes.
 */
public class TestDataStorage extends DataStorage {

  /**
   * Constructor for class. Use {@link #getInstance(Context)} to get an instance.
   *
   * @param context the Android Context.
   */
  private TestDataStorage(@NonNull final Context context) {
    this.traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
  }

  /**
   * Gets an instance of the class. Use to prevent having multiple instances.
   *
   * @param context the Android Context.
   * @return the DataStorage.
   */
  @NonNull
  public static synchronized DataStorage getInstance(@NonNull final Context context) {
    dataStorage = new TestDataStorage(context);
    return dataStorage;
  }
}