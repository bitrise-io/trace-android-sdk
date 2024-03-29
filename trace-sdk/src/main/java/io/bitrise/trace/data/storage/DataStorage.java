package io.bitrise.trace.data.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.storage.entities.CrashEntity;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceEntity;
import io.bitrise.trace.data.trace.TraceUtils;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base abstract class responsible for {@link Metric} and {@link TraceEntity} storing on the device.
 */
public abstract class DataStorage {

  static volatile DataStorage dataStorage;
  TraceDatabase traceDatabase;

  /**
   * Check if the {@link #dataStorage} is already instantiated or not.
   *
   * @return {@code true} is yes, {@code false} otherwise.
   */
  @VisibleForTesting
  public static boolean isInitialised() {
    return dataStorage != null;
  }

  /**
   * Saves the given {@link MetricEntity} to the storage.
   *
   * <p>Should not be called on the main thread.
   *
   * @param metricEntity the MetricEntity.
   */
  @WorkerThread
  public void saveMetric(@NonNull final MetricEntity metricEntity) {
    traceDatabase.getMetricDao().insertAll(metricEntity);
    TraceLog.d(LogMessageConstants.SAVE_METRIC);
  }

  /**
   * Saves the given {@link Trace}s to the storage.
   *
   * <p>Should not be called on the main thread.
   *
   * @param traces the Traces.
   */
  @WorkerThread
  public void saveTraces(@NonNull final Trace... traces) {
    for (Trace trace : traces) {
      TraceLog.debugV(trace.getDebugLoggingInfo());
      traceDatabase.getTraceDao().insertAll(new TraceEntity(trace));
    }

    TraceLog.d(LogMessageConstants.SAVE_TRACE);
  }

  /**
   * Saves the given {@link ResourceEntity} to the storage.
   *
   * <p>Should not be called on the main thread.
   *
   * @param resourceEntity the ResourceEntity.
   */
  @WorkerThread
  public void saveResourceEntity(@NonNull final ResourceEntity resourceEntity) {
    traceDatabase.getResourceDao().insertAll(resourceEntity);
    TraceLog.d(LogMessageConstants.SAVE_RESOURCE_ENTITY);
  }

  /**
   * Saves the given {@link Resource} to the storage.
   *
   * <p>Should not be called on the main thread.
   *
   * @param resource the Resource.
   */
  @WorkerThread
  public void saveResource(@NonNull final Resource resource) {
    for (@NonNull final Map.Entry<String, String> entry : resource.getLabelsMap().entrySet()) {
      saveResourceEntity(new ResourceEntity(entry.getKey(), entry.getValue()));
    }
    TraceLog.d(LogMessageConstants.SAVE_RESOURCE);
  }

  /**
   * Deletes the {@link MetricEntity} with the given ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @param metricEntity the MetricEntity to delete.
   */
  @WorkerThread
  public void deleteMetric(@NonNull final MetricEntity metricEntity) {
    traceDatabase.getMetricDao().deleteById(metricEntity.getMetricId());
    TraceLog.d(LogMessageConstants.DELETE_METRIC);
  }

  /**
   * Deletes the {@link Trace} with the given ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @param trace the Trace to delete.
   */
  @WorkerThread
  public void deleteTrace(@NonNull final Trace trace) {
    traceDatabase.getTraceDao().deleteById(trace.getTraceId());
    TraceLog.d(LogMessageConstants.DELETE_TRACE);
  }

  /**
   * Deletes the {@link ResourceEntity} with the given label.
   *
   * <p>Should not be called on the main thread.
   *
   * @param sessionId the ResourceEntity to delete.
   */
  @WorkerThread
  public void deleteResourcesWithSessionId(@NonNull final String sessionId) {
    traceDatabase.getResourceDao().deleteBySessionId(sessionId);
    TraceLog.d(String.format(LogMessageConstants.DELETE_RESOURCE_WITH_SESSION_ID, sessionId));
  }

  /**
   * Gets the {@link  MetricEntity} with the given ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @param metricId the ID of the MetricEntity.
   * @return the MetricEntity, or {@code null} when not found.
   */
  @WorkerThread
  @Nullable
  public MetricEntity getMetricById(@NonNull final String metricId) {
    return traceDatabase.getMetricDao().getById(metricId);
  }

  /**
   * Gets the {@link Trace}.
   *
   * <p>Should not be called on the main thread.
   *
   * @param traceEntity the TraceEntity.
   * @return the Trace, or {@code null} when not found.
   */
  @WorkerThread
  @Nullable
  public Trace getTrace(@NonNull final TraceEntity traceEntity) {
    return getTraceById(traceEntity.getTraceId());
  }

  /**
   * Gets the {@link Trace} with the given ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @param id the ID of the Trace.
   * @return the Trace, or {@code null} when not found.
   */
  @WorkerThread
  @Nullable
  public Trace getTraceById(@NonNull final String id) {
    final TraceEntity traceEntity = traceDatabase.getTraceDao().getById(id);
    if (traceEntity == null) {
      return null;
    }
    return traceEntity.getTrace();
  }

  /**
   * Gets all the {@link ResourceEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of ResourceEntities.
   */
  @WorkerThread
  @NonNull
  public List<ResourceEntity> getResourcesWithSessionId(@NonNull final String sessionId) {
    return traceDatabase.getResourceDao().getAllWithSessionId(sessionId);
  }

  /**
   * Gets all the {@link MetricEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of MetricEntities.
   */
  @WorkerThread
  @NonNull
  public List<MetricEntity> getAllMetrics() {
    return traceDatabase.getMetricDao().getAll();
  }

  /**
   * Gets all the {@link Trace}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of Traces.
   */
  @WorkerThread
  @NonNull
  public List<Trace> getAllTraces() {
    return TraceUtils.toTraceList(traceDatabase.getTraceDao().getAll());
  }

  /**
   * Gets the list of {@link Trace}s with the same {@link Session} ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of Traces.
   */
  @WorkerThread
  @NonNull
  public List<Trace> getFirstTraceGroup() {
    final List<String> sessionIds = traceDatabase.getTraceDao().getSessionIds();
    if (sessionIds.isEmpty()) {
      return Collections.emptyList();
    }
    return TraceUtils
        .toTraceList(traceDatabase.getTraceDao().getBySessionId(sessionIds.iterator().next()));
  }

  /**
   * Gets the list of {@link Trace}s with the same {@link Session} ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of Traces.
   */
  @WorkerThread
  @NonNull
  public List<MetricEntity> getFirstMetricGroup() {
    final List<String> sessionIds = traceDatabase.getMetricDao().getSessionIds();
    if (sessionIds.isEmpty()) {
      return Collections.emptyList();
    }
    return traceDatabase.getMetricDao().getBySessionId(sessionIds.iterator().next());
  }

  /**
   * Gets all the {@link ResourceEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the List of ResourceEntities.
   */
  @WorkerThread
  @NonNull
  public List<ResourceEntity> getAllResources() {
    return traceDatabase.getResourceDao().getAll();
  }

  /**
   * Deletes all the given {@link MetricEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @param metricEntityList the List to delete.
   */
  @WorkerThread
  public void deleteMetrics(@NonNull final List<MetricEntity> metricEntityList) {
    for (@NonNull final MetricEntity metricEntity : metricEntityList) {
      deleteMetric(metricEntity);
    }
    TraceLog.d(LogMessageConstants.DELETE_METRICS);
  }

  /**
   * Deletes all the given {@link Trace}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @param traceList the List to delete.
   */
  @WorkerThread
  public void deleteTraces(@NonNull final List<Trace> traceList) {
    for (@NonNull final Trace trace : traceList) {
      traceDatabase.getTraceDao().deleteById(trace.getTraceId());
    }
    TraceLog.d(LogMessageConstants.DELETE_TRACES);
  }

  /**
   * Deletes all the given {@link ResourceEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   *
   * @param resourceEntities the List to delete.
   */
  @WorkerThread
  public void deleteResources(@NonNull final List<ResourceEntity> resourceEntities) {
    for (@NonNull final ResourceEntity resourceEntity : resourceEntities) {
      traceDatabase.getResourceDao().deleteById(resourceEntity.getId());
    }
    TraceLog.d(LogMessageConstants.DELETE_RESOURCE_ENTITIES);
  }

  /**
   * Check if the {@link TraceDao} or the {@link MetricDao} has any reference to the given
   * {@link Session} ID.
   *
   * <p>Should not be called on the main thread.
   *
   * @param sessionId the given Session ID.
   * @return {@code true} if yes, {@code false} otherwise.
   */
  @WorkerThread
  public boolean hasReference(@NonNull final String sessionId) {
    final int metricReferenceCount =
        traceDatabase.getMetricDao().getBySessionId(sessionId).size();
    final int traceReferenceCount = traceDatabase.getTraceDao().getBySessionId(sessionId).size();
    return metricReferenceCount > 0 || traceReferenceCount > 0;
  }

  /**
   * Deletes all {@link Trace}s from the database.
   *
   * <p>Should not be called on the main thread.
   */
  @WorkerThread
  public void deleteAllTraces() {
    traceDatabase.getTraceDao().deleteAll();
    TraceLog.d(LogMessageConstants.DELETE_ALL_TRACES);
  }

  /**
   * Deletes all {@link Metric}s from the database.
   *
   * <p>Should not be called on the main thread.
   */
  @WorkerThread
  public void deleteAllMetrics() {
    traceDatabase.getMetricDao().deleteAll();
    TraceLog.d(LogMessageConstants.DELETE_ALL_METRICS);
  }

  /**
   * Deletes all {@link ResourceEntity}s from the database.
   *
   * <p>Should not be called on the main thread.
   */
  @WorkerThread
  public void deleteAllResources() {
    traceDatabase.getResourceDao().deleteAll();
    TraceLog.d(LogMessageConstants.DELETE_ALL_RESOURCE_ENTITIES);
  }

  /**
   * Sets the trace database to use.
   *
   * @param traceDatabase the database to use.
   */
  @VisibleForTesting
  public void setTraceDatabase(@NonNull final TraceDatabase traceDatabase) {
    this.traceDatabase = traceDatabase;
  }

  /**
   * Save a {@link CrashRequest} object to the database.
   *
   * @param request - the {@link CrashRequest} object to save.
   */
  @WorkerThread
  public void saveCrashRequest(@NonNull final CrashRequest request) {
    traceDatabase.getCrashDao().insert(new CrashEntity(request));
    TraceLog.d("saved crash request");
  }

  /**
   * Gets all currently saved {@link CrashRequest} objects.
   *
   * @return a list of the saved {@link CrashRequest}'s.
   */
  @WorkerThread
  @NonNull
  public List<CrashRequest> getAllCrashRequests() {
    final List<CrashEntity> entities = traceDatabase.getCrashDao().getAll();

    final List<CrashRequest> requests = new ArrayList<>();
    for (final CrashEntity entity : entities) {
      requests.add(entity.getCrashRequest());
    }

    return requests;
  }

  /**
   * Remove a {@link CrashRequest} object from the database.
   *
   * @param id - the uuid of the {@link CrashRequest} to remove.
   */
  @WorkerThread
  public void deleteCrashRequest(@NonNull final String id) {
    traceDatabase.getCrashDao().deleteById(id);
    TraceLog.d("Deleted crash request");
  }

  /**
   * Update the crash request sent attempt counter - used when a request fails. So we don't
   * repeatedly try to send a failing request.
   *
   * @param id the requests metadata uuid.
   */
  @WorkerThread
  public void updateCrashRequestSentAttemptCounter(@NonNull final String id) {
    final CrashEntity crashEntity = getCrashEntity(id);

    if (crashEntity != null) {
      crashEntity.updateSentAttempts();
      if (crashEntity.getSentAttempts() < 5) {
        TraceLog.d("Updating crash request: failed attempts to send: "
            + crashEntity.getSentAttempts());
        traceDatabase.getCrashDao().insert(crashEntity);
      } else {
        TraceLog.d("Crash request reached maximum attempts to send, will be deleted.");
        deleteCrashRequest(id);
      }
    }
  }

  /**
   * Gets the {@link CrashEntity} for a given id.
   *
   * @param id the requests metadata uuid to find.
   * @return the {@link CrashEntity} object for a given id, or null if it cannot be found.
   */
  @WorkerThread
  public CrashEntity getCrashEntity(@NonNull final String id) {
    return traceDatabase.getCrashDao().getById(id);
  }
}
