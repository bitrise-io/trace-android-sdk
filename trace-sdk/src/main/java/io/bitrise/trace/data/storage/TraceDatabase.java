package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.storage.entities.CrashEntity;
import io.bitrise.trace.data.trace.TraceEntity;

/**
 * Database class for storing {@link TraceEntity}s, {@link MetricEntity}s and
 * {@link ResourceEntity}s. Each entity will have it's own table in the database. Each member in
 * the given entities will be the columns in the tables.
 */
@Database(entities = {
    TraceEntity.class,
    MetricEntity.class,
    ResourceEntity.class,
    CrashEntity.class
}, version = 2)
public abstract class TraceDatabase extends RoomDatabase {

  /**
   * Gets the {@link TraceDao} to interact with {@link TraceEntity}s.
   *
   * @return the TraceDao.
   */
  @NonNull
  public abstract TraceDao getTraceDao();

  /**
   * Gets the {@link MetricDao} to interact with {@link io.opencensus.proto.metrics.v1.Metric}s.
   *
   * @return the MetricDao.
   */
  @NonNull
  public abstract MetricDao getMetricDao();

  /**
   * Gets the {@link ResourceDao} to interact with
   * {@link io.bitrise.trace.data.resource.ResourceEntity}s.
   *
   * @return the ResourceDao.
   */
  @NonNull
  public abstract ResourceDao getResourceDao();

  /**
   * Gets the {@link CrashDao} to interact with
   * {@link io.bitrise.trace.data.storage.entities.CrashEntity}s.
   *
   * @return the CrashDao.
   */
  @NonNull
  public abstract CrashDao getCrashDao();
}
