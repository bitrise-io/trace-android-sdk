package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.session.Session;
import java.util.List;

/**
 * DAO interface for {@link ResourceEntity} used by {@link TraceDatabase}.
 */
@Dao
public interface ResourceDao {

  /**
   * Gets all the {@link ResourceEntity}s from the database.
   *
   * @return the List of ResourceEntities.
   */
  @NonNull
  @Query("SELECT * FROM ResourceEntity")
  List<ResourceEntity> getAll();

  /**
   * Gets the {@link ResourceEntity}s with the given {@link Session} ID from the database.
   *
   * @param sessionId the Session ID to search for.
   * @return the List of ResourceEntities.
   */
  @NonNull
  @Query("SELECT * FROM ResourceEntity WHERE sessionId IN (:sessionId)")
  List<ResourceEntity> getAllWithSessionId(@NonNull String sessionId);

  /**
   * Inserts the given {@link ResourceEntity}s to the database. Replaces if there is a conflict.
   *
   * @param resourceEntities the ResourceEntities to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(@NonNull ResourceEntity... resourceEntities);

  /**
   * Deletes the given {@link ResourceEntity}s from the database.
   *
   * @param sessionId the {@link Session} ID of the ResourceEntities to delete.
   */
  @Query("DELETE FROM ResourceEntity WHERE sessionId = :sessionId")
  void deleteBySessionId(@NonNull String sessionId);

  /**
   * Deletes the given {@link ResourceEntity} from the database.
   *
   * @param id the ID of the ResourceEntity to delete.
   */
  @Query("DELETE FROM ResourceEntity WHERE id = :id")
  void deleteById(@NonNull String id);

  /**
   * Deletes all the {@link ResourceEntity}s from the database.
   */
  @Query("DELETE FROM ResourceEntity")
  void deleteAll();
}
