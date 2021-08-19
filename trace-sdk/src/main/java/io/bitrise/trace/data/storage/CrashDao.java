package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.bitrise.trace.data.storage.entities.CrashEntity;
import java.util.List;

/**
 * DAO interface for {@link CrashEntity} used by {@link TraceDatabase}.
 */
@Dao
public interface CrashDao  {

  /**
   * Gets all the {@link CrashEntity}s from the database.
   *
   * @return the List of CrashRequests.
   */
  @NonNull
  @Query("SELECT * FROM CrashEntity")
  List<CrashEntity> getAll();

  /**
   * Inserts the given {@link CrashEntity}s to the database. Replaces if there is a conflict.
   *
   * @param crashEntity the CrashEntity object to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(@NonNull CrashEntity crashEntity);

  /**
   * Deletes the given {@link CrashEntity} from the database.
   *
   * @param id the ID of the CrashEntity to delete.
   */
  @Query("DELETE FROM CrashEntity WHERE id = :id")
  void deleteById(@NonNull String id);

}
