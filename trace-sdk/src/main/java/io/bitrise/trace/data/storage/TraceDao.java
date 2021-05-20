package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.bitrise.trace.data.trace.TraceEntity;
import io.bitrise.trace.session.Session;
import java.util.List;

/**
 * DAO interface for {@link TraceEntity} used by {@link TraceDatabase}.
 */
@Dao
public interface TraceDao {

  /**
   * Gets all the {@link TraceEntity}s from the database.
   *
   * @return the List of TraceEntities.
   */
  @NonNull
  @Query("SELECT * FROM TraceEntity")
  List<TraceEntity> getAll();

  /**
   * Gets all the IDs of the {@link Session}s of the {@link TraceEntity}s.
   *
   * @return the List of Session IDs.
   */
  @NonNull
  @Query("SELECT sessionId FROM TraceEntity")
  List<String> getSessionIds();

  /**
   * Gets all the {@link TraceEntity}s with the given {@link Session} ID.
   *
   * @param sessionId the given Session ID.
   * @return the TraceEntities with the given Session ID.
   */
  @NonNull
  @Query("SELECT * FROM TraceEntity WHERE sessionId IN (:sessionId)")
  List<TraceEntity> getBySessionId(@NonNull String sessionId);

  /**
   * Gets the {@link TraceEntity} with the given ID from the database.
   *
   * @param traceId the ID to search for.
   * @return the Trace, or {@code null} when not found.
   */
  @Nullable
  @Query("SELECT * FROM TraceEntity WHERE traceId IN (:traceId)")
  TraceEntity getById(@NonNull String traceId);

  /**
   * Inserts the given {@link TraceEntity}s to the database. Replaces if there is a conflict.
   *
   * @param traceEntities the Traces to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(@NonNull TraceEntity... traceEntities);

  /**
   * Deletes the given {@link TraceEntity} from the database.
   *
   * @param traceId the ID of the Trace to delete.
   */
  @Query("DELETE FROM TraceEntity WHERE traceId = :traceId")
  void deleteById(@NonNull String traceId);

  /**
   * Deletes all the {@link TraceEntity}s from the database.
   */
  @Query("DELETE FROM TraceEntity")
  void deleteAll();
}
