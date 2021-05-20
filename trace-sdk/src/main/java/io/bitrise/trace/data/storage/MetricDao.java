package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.session.Session;
import java.util.List;

/**
 * DAO interface for {@link MetricEntity} used by {@link TraceDatabase}.
 */
@Dao
public interface MetricDao {

  /**
   * Gets all the {@link MetricEntity}s from the database.
   *
   * @return the List of MetricEntities.
   */
  @NonNull
  @Query("SELECT * FROM MetricEntity")
  List<MetricEntity> getAll();

  /**
   * Gets all the IDs of the {@link Session}s of the {@link MetricEntity}s.
   *
   * @return the List of Session IDs.
   */
  @NonNull
  @Query("SELECT sessionId FROM MetricEntity")
  List<String> getSessionIds();

  /**
   * Gets all the {@link MetricEntity}s with the given {@link Session} ID.
   *
   * @param sessionId the given Session ID.
   * @return the MetricEntities with the given Session ID.
   */
  @NonNull
  @Query("SELECT * FROM MetricEntity WHERE sessionId IN (:sessionId)")
  List<MetricEntity> getBySessionId(@NonNull String sessionId);

  /**
   * Gets the {@link MetricEntity} from the database.
   *
   * @param metricId the ID of the MetricEntity to search for.
   * @return the MetricEntity, or {@code null} when not found.
   */
  @Nullable
  @Query("SELECT * FROM MetricEntity WHERE metricId IN (:metricId)")
  MetricEntity getById(@NonNull String metricId);

  /**
   * Inserts the given {@link MetricEntity}s to the database. Replaces if there is a conflict.
   *
   * @param metricEntities the MetricEntities to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(@NonNull MetricEntity... metricEntities);

  /**
   * Deletes the given {@link MetricEntity} from the database.
   *
   * @param metricId the ID of the {@link MetricEntity} to delete.
   */
  @Query("DELETE FROM MetricEntity WHERE metricId IN (:metricId)")
  void deleteById(@NonNull String metricId);

  /**
   * Deletes all the {@link MetricEntity}s from the database.
   */
  @Query("DELETE FROM MetricEntity")
  void deleteAll();
}
