package io.bitrise.trace.network;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import com.google.common.util.concurrent.SettableFuture;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Base abstract class responsible for sending the Data to the server.
 */
public abstract class DataSender extends JobService {

  /**
   * The thread pool size of the {@link #executor}.
   */
  private static final int EXECUTOR_POOL_SIZE = 2;

  /**
   * The future of the {@link Result} of this service.
   */
  @VisibleForTesting
  @Nullable
  SettableFuture<Result> resultSettableFuture;

  /**
   * The {@link DataStorage} to get the data to send.
   */
  @Nullable
  private DataStorage dataStorage;

  /**
   * The {@link NetworkCommunicator} for the networking interface.
   */
  @VisibleForTesting
  @Nullable
  NetworkCommunicator networkCommunicator;

  /**
   * The {@link ExecutorService} for asynchronous operations.
   */
  @VisibleForTesting
  @Nullable
  ExecutorService executor;

  /**
   * The {@link DataManager} for checking the active data collectors.
   */
  @Nullable
  private DataManager dataManager;

  /**
   * Indicates that the DataSender has been stopped.
   */
  private boolean stopped;

  /**
   * The Android Context.
   */
  @VisibleForTesting
  @Nullable
  Context context;

  /**
   * Validates the given {@link NetworkRequest} if it contains all the required
   * {@link Resource} labels or not.
   *
   * @param networkRequest the given NetworkRequest.
   * @return {@code true} if it is valid, {@code false} otherwise.
   */
  static boolean validateNetworkRequest(@Nullable final NetworkRequest networkRequest) {
    if (networkRequest == null) {
      return false;
    }
    final Set<ResourceLabel> requiredResourceLabels =
        ConfigurationManager.getInstance().getRequiredResourceLabels();

    final Set<String> requiredLabels = new HashSet<>();
    for (@NonNull final ResourceLabel resourceLabel : requiredResourceLabels) {
      requiredLabels.add(resourceLabel.getName());
    }

    final Set<String> resourceLabels =
        new HashSet<>(networkRequest.getResource().getLabels().keySet());
    return resourceLabels.containsAll(requiredLabels);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (!ConfigurationManager.isInitialised()) {
      ConfigurationManager.init(getContext());
    }
  }

  /**
   * Sends the data to the server.
   *
   * @param params the {@link JobParameters} of this {@link JobService}.
   * @return a Future with the {@link Result} of the sending.
   */
  public abstract Future<Result> send(@NonNull final JobParameters params);

  /**
   * Callback when the sending was successful.
   */
  public abstract void onSuccess();

  public boolean isStopped() {
    return stopped;
  }

  public void setStopped(final boolean stopped) {
    this.stopped = stopped;
  }

  @Override
  public boolean onStopJob(final JobParameters params) {
    setStopped(true);
    final Future<Boolean> future =
        Executors.newSingleThreadExecutor().submit(this::isRescheduleNeeded);
    try {
      return future.get();
    } catch (final ExecutionException | InterruptedException e) {
      // nop
    }
    return true;
  }

  /**
   * Gets the {@link Session} {@link Resource}.
   *
   * <p>Should not be called on the main thread.
   *
   * @param sessionId the ID of the Session to add as a label, can be {@code null} when used for
   *                  {@link Metric}s.
   * @return the Resource.
   */
  @NonNull
  @WorkerThread
  Resource getSessionResources(@NonNull final String sessionId) {
    final Resource.Builder resource = Resource.newBuilder();
    resource.setType("mobile");

    final List<ResourceEntity> storedResources =
        getDataStorage().getResourcesWithSessionId(sessionId);
    for (@NonNull final ResourceEntity resourceEntity : storedResources) {
      resource.putLabels(resourceEntity.getLabel(), resourceEntity.getValue());
    }

    resource.putLabels(ResourceLabel.APPLICATION_PLATFORM.getName(), "android");
    resource.putLabels(ResourceLabel.SESSION_ID.getName(), sessionId);

    return resource.build();
  }

  /**
   * Gets a {@link NetworkRequest} that can be sent with this DataSender.
   *
   * <p>Should not be called on the main thread.
   *
   * @return the TraceRequest, or {@code null}, when the required data is not available.
   */
  @Nullable
  @WorkerThread
  abstract NetworkRequest getNetworkRequest();

  /**
   * Removes the {@link Resource}s from the {@link DataStorage}, if they do not have any
   * reference.
   *
   * <p>Should not be called on the main thread.
   *
   * @param sessionId the {@link Session} ID of the Resources to remove.
   * @return {@code true}, if they were removed, {@code false} otherwise.
   */
  @WorkerThread
  synchronized boolean removeResources(@NonNull final String sessionId) {
    final Session activeSession = ApplicationSessionManager.getInstance().getActiveSession();
    final boolean isDifferentSession = activeSession == null
        || !sessionId.equals(activeSession.getUlid());
    if (getDataStorage().hasReference(sessionId) || !isDifferentSession) {
      return false;
    }
    getDataStorage().deleteResourcesWithSessionId(sessionId);
    return true;
  }

  /**
   * Determines if rescheduling of the given DataSender is needed or not.
   *
   * @return {@code true} if needed, {@code false} otherwise.
   */
  @WorkerThread
  boolean isRescheduleNeeded() {
    if (getDataManager().getActiveDataCollectors().size() > 0
        || getDataManager().getActiveDataListeners().size() > 0) {
      return true;
    }

    return hasData();
  }

  /**
   * Called when the sending of the Data is finished.
   *
   * @param jobParameters    the JobParameters.
   * @param shouldReschedule {@code true} if the sending should be rescheduled, {@code false}
   *                         otherwise.
   */
  void onSendingFinished(@Nullable final JobParameters jobParameters,
                         final boolean shouldReschedule) {
    jobFinished(jobParameters, shouldReschedule);
  }

  /**
   * Determines if the given DataSender has any data in the {@link DataStorage} to send, or not.
   *
   * @return {@code true} if has, {@code false} otherwise.
   */
  @WorkerThread
  abstract boolean hasData();

  @NonNull
  SettableFuture<Result> getResultSettableFuture() {
    if (resultSettableFuture == null) {
      resultSettableFuture = SettableFuture.create();
    }
    return resultSettableFuture;
  }

  void setResultSettableFuture(@NonNull final SettableFuture<Result> resultSettableFuture) {
    this.resultSettableFuture = resultSettableFuture;
  }

  @NonNull
  DataStorage getDataStorage() {
    if (dataStorage == null) {
      dataStorage = TraceDataStorage.getInstance(this);
    }
    return dataStorage;
  }

  void setDataStorage(@NonNull final DataStorage dataStorage) {
    this.dataStorage = dataStorage;
  }

  @NonNull
  NetworkCommunicator getNetworkCommunicator() {
    if (networkCommunicator == null) {
      networkCommunicator = NetworkClient.getCommunicator();
    }
    return networkCommunicator;
  }

  void setNetworkCommunicator(@NonNull final NetworkCommunicator networkCommunicator) {
    this.networkCommunicator = networkCommunicator;
  }

  @NonNull
  DataManager getDataManager() {
    if (dataManager == null) {
      dataManager = DataManager.getInstance(this);
    }
    return dataManager;
  }

  void setDataManager(@NonNull final DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @NonNull
  ExecutorService getExecutor() {
    if (executor == null) {
      executor = Executors.newFixedThreadPool(EXECUTOR_POOL_SIZE);
    }
    return executor;
  }

  void setExecutor(@NonNull final ExecutorService executor) {
    this.executor = executor;
  }

  @VisibleForTesting
  @NonNull
  Context getContext() {
    if (context == null) {
      context = this;
    }
    return context;
  }

  @VisibleForTesting
  void setContext(@NonNull final Context context) {
    this.context = context;
  }

  /**
   * The result types for the sending of the data.
   */
  enum Result {
    SUCCESS,
    FAILURE,
  }
}