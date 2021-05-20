package io.bitrise.trace.data.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Room;
import io.bitrise.trace.data.trace.TraceEntity;
import io.opencensus.proto.metrics.v1.Metric;
import javax.inject.Singleton;

/**
 * Responsible for {@link Metric} and {@link TraceEntity} storing on the device for the Trace SDK.
 * Recommendation: CRUD methods should not be called on the main thread.
 * <br><br>
 * IMPORTANT: To inspect the Room database file use the Database Inspector tool in Android
 * Studio, it is available from version 4.1. Below this version you must extract all 3 files from
 * the device, go to <pre>/data/data/io.bitrise.trace/databases/trace-database*</pre> then rename
 * them to have the correct file extensions like this: "trace-database.db", "trace-database
 * .db-shm" and "trace-database.db-wal". Then open the *.db file in any SQLite browser.
 * <br><br>
 */
@Singleton
public class TraceDataStorage extends DataStorage {

  /**
   * Constructor for class. Use {@link #getInstance(Context)} to get an instance.
   *
   * @param context the Android Context.
   */
  private TraceDataStorage(@NonNull final Context context) {
    this.traceDatabase =
        Room.databaseBuilder(context, TraceDatabase.class, "trace-database").build();
  }

  /**
   * Gets an instance of the class. Use to prevent having multiple instances.
   *
   * @param context the Android Context.
   * @return the DataStorage.
   */
  @NonNull
  public static synchronized DataStorage getInstance(@NonNull final Context context) {
    if (dataStorage == null) {
      dataStorage = new TraceDataStorage(context);
    }
    return dataStorage;
  }
}
