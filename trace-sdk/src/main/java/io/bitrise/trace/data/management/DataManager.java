package io.bitrise.trace.data.management;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.DataSource;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceManager;
import io.bitrise.trace.network.DataSender;
import io.bitrise.trace.network.MetricSender;
import io.bitrise.trace.network.TraceSender;
import io.bitrise.trace.scheduler.ExecutorScheduler;
import io.bitrise.trace.scheduler.ServiceScheduler;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;

/**
 * Manages the {@link Data} collection. Starts and stops the {@link DataSource}s.
 */
@Singleton
public class DataManager {

    /**
     * The instance for the {@link DataManager}.
     */
    @Nullable
    private volatile static DataManager dataManager;

    /**
     * The {@link ExecutorScheduler} to be able to schedule recurring events.
     */
    @Nullable
    private ExecutorScheduler executorScheduler;

    /**
     * The {@link DataStorage} for the formatter.
     */
    @NonNull
    private DataStorage dataStorage;

    /**
     * The Set of active {@link DataListener}s.
     */
    @NonNull
    private final Set<DataListener> activeDataListeners = new HashSet<>();

    /**
     * The Set of active {@link DataCollector}s.
     */
    @NonNull
    private final Set<DataCollector> activeDataCollectors = new HashSet<>();

    /**
     * The {@link ServiceScheduler} to schedule Service runs for sending {@link Metric}s.
     */
    @Nullable
    ServiceScheduler metricServiceScheduler;

    /**
     * The {@link ServiceScheduler} to schedule Service runs for sending {@link Trace}s.
     */
    @Nullable
    ServiceScheduler traceServiceScheduler;

    /**
     * The {@link ConfigurationManager} of the SDK. Required to get the configuration which {@link DataCollector}s
     * and {@link DataListener}s will be used.
     */
    @VisibleForTesting
    @NonNull
    ConfigurationManager configurationManager;

    /**
     * The {@link DataFormatterDelegator} that is responsible for converting the Data to the required format and push
     * it to the {@link DataStorage}.
     */
    @NonNull
    private final DataFormatterDelegator dataFormatterDelegator;

    /**
     * The {@link TraceManager} that will handle the received Spans.
     */
    @NonNull
    private final TraceManager traceManager;

    /**
     * A lock object for preventing concurrency issues for the {@link #dataManager}. E.g: when {@link #reset()} and
     * {@link #getInstance(Context)} is called parallel.
     */
    @NonNull
    private static final Object dataManagerLock = new Object();

    /**
     * A lock object for preventing concurrency issues for the {@link #activeDataListeners}. E.g: when
     * {@link #stopCollection()} ()} and {@link #startCollection(Context)} is called parallel.
     */
    @NonNull
    private static final Object activeDataListenerLock = new Object();

    /**
     * A lock object for preventing concurrency issues for the {@link #activeDataCollectors}. E.g: when
     * {@link #stopCollection()} ()} and {@link #startCollection(Context)} is called parallel.
     */
    @NonNull
    private static final Object activeDataCollectorLock = new Object();

    /**
     * Constructor to prevent instantiation outside of the class.
     *
     * @param context the Android Context.
     */
    private DataManager(@NonNull final Context context) {
        this.configurationManager = ConfigurationManager.getInstance();
        this.dataStorage = TraceDataStorage.getInstance(context);
        this.dataFormatterDelegator = DataFormatterDelegator.getInstance();
        this.traceManager = ApplicationTraceManager.getInstance(context);
    }

    /**
     * Gets the instance of the {@link DataManager} class. Singleton item.
     *
     * @param context the Android Context.
     * @return the DataManager.
     */
    @NonNull
    public static synchronized DataManager getInstance(@NonNull final Context context) {
        synchronized (dataManagerLock) {
            if (dataManager == null) {
                dataManager = new DataManager(context);
                TraceLog.d(LogMessageConstants.DATA_MANAGER_INITIALISED);
            }
            return dataManager;
        }
    }

    @VisibleForTesting
    public void setDataStorage(@NonNull final DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Check if the DataManager has been initialised or not.
     *
     * @return {@code true} when yes, {@code false} otherwise.
     */
    public static synchronized boolean isInitialised() {
        return dataManager != null;
    }

    /**
     * Resets the state of the DataManager.
     */
    public static synchronized void reset() {
        synchronized (dataManagerLock) {
            if (dataManager == null) {
                return;
            }

            dataManager.stopCollection();
            if (dataManager.executorScheduler != null) {
                dataManager.executorScheduler.cancelAll();
                dataManager.executorScheduler = null;
            }

            if (dataManager.metricServiceScheduler != null) {
                dataManager.metricServiceScheduler.cancelAll();
                dataManager.metricServiceScheduler = null;
            }

            if (dataManager.traceServiceScheduler != null) {
                dataManager.traceServiceScheduler.cancelAll();
                dataManager.traceServiceScheduler = null;
            }

            dataManager = null;
        }
    }

    /**
     * Starts the Data collection. Does nothing if the collection already started.
     *
     * @param context the Android Context.
     */
    public void startCollection(@NonNull final Context context) {
        TraceLog.d(LogMessageConstants.DATA_MANAGER_START_COLLECTING);
        startEventDrivenDataCollection(context);
        startRecurringDataCollection(context);
    }

    /**
     * Stops the Data collection.
     */
    public void stopCollection() {
        TraceLog.d(LogMessageConstants.DATA_MANAGER_STOP_COLLECTING);
        stopEventDrivenDataCollection();
        stopRecurringDataCollection();
    }

    /**
     * Starts the recurring collection of non-event driven data.
     *
     * @param context the Android Context.
     */
    private void startRecurringDataCollection(@NonNull final Context context) {
        synchronized (activeDataCollectorLock) {
            if (!activeDataCollectors.isEmpty()) {
                return;
            }

            activeDataCollectors.addAll(configurationManager.getDataCollectors(context));
            for (@NonNull final DataCollector dataCollector : activeDataCollectors) {
                final Runnable collectDataRunnable = () -> handleReceivedData(dataCollector.collectData());
                executorScheduler = new ExecutorScheduler(context, collectDataRunnable, 0,
                        dataCollector.getIntervalMs());
                executorScheduler.schedule();
            }
        }
    }

    /**
     * Starts the collection of event driven data.
     *
     * @param context the Android Context.
     */
    private void startEventDrivenDataCollection(@NonNull final Context context) {
        synchronized (activeDataListenerLock) {
            if (!activeDataListeners.isEmpty()) {
                return;
            }
            activeDataListeners.addAll(configurationManager.getDataListeners(context));
            for (@NonNull final DataListener dataListener : activeDataListeners) {
                dataListener.startCollecting();
            }
        }
    }

    /**
     * Stops the collection of non-event driven data.
     */
    private void stopRecurringDataCollection() {
        synchronized (activeDataCollectorLock) {
            if (executorScheduler != null) {
                executorScheduler.cancelAll();
            }

            activeDataCollectors.clear();
        }
    }

    /**
     * Stops the collection of event driven data.
     */
    private void stopEventDrivenDataCollection() {
        synchronized (activeDataListenerLock) {
            for (@NonNull final DataListener dataListener : activeDataListeners) {
                dataListener.stopCollecting();
            }
            activeDataListeners.clear();
        }
    }

    /**
     * Starts the sending of Data to the server with the {@link DataSender}.
     *
     * @param context the Android Context.
     */
    public void startSending(@NonNull final Context context) {
        stopSending(context);
        TraceLog.d(LogMessageConstants.DATA_MANAGER_START_SENDING);
        if (metricServiceScheduler == null) {
            metricServiceScheduler = new ServiceScheduler(context, MetricSender.class,
                    ExecutorScheduler.DEFAULT_SCHEDULE_INITIAL_DELAY_MS);
        }
        metricServiceScheduler.schedule();

        if (traceServiceScheduler == null) {
            traceServiceScheduler = new ServiceScheduler(context, TraceSender.class,
                    ExecutorScheduler.DEFAULT_SCHEDULE_INITIAL_DELAY_MS);
        }
        traceServiceScheduler.schedule();
    }

    /**
     * Stops the sending of any pending requests to the server. These request will be dismissed and will not be sent.
     * No future requests will be sent unless {@link #startSending} is called again.
     *
     * @param context the Android Context.
     */
    public void stopSending(@NonNull final Context context) {
        TraceLog.d(LogMessageConstants.DATA_MANAGER_STOP_SENDING);
        if (metricServiceScheduler == null) {
            metricServiceScheduler = new ServiceScheduler(context, MetricSender.class);
        }
        metricServiceScheduler.cancelAll();

        if (traceServiceScheduler == null) {
            traceServiceScheduler = new ServiceScheduler(context, TraceSender.class);
        }
        traceServiceScheduler.cancelAll();
    }

    /**
     * Gets the Set of {@link DataListener}s which are currently active. Returns an empty list if none is active.
     *
     * @return the active DataListeners.
     */
    @NonNull
    public Set<DataListener> getActiveDataListeners() {
        return activeDataListeners;
    }

    /**
     * Gets the Set of {@link DataCollector}s which are currently active. Returns an empty list if none is active.
     *
     * @return the active DataCollectors.
     */
    @NonNull
    public Set<DataCollector> getActiveDataCollectors() {
        return activeDataCollectors;
    }

    /**
     * Handles the received {@link Data}. Formats it using the {@link DataFormatterDelegator}, then {@link Metric}s and
     * {@link Resource}s are forwarded to the to the {@link DataStorage}, Spans to the {@link TraceManager}.
     *
     * @param data the given Data.
     */
    public void handleReceivedData(@NonNull final Data data) {
        final FormattedData[] formattedDataArray = dataFormatterDelegator.formatData(data);
        for (@Nullable final FormattedData formattedData : formattedDataArray) {
            if (formattedData == null) {
                TraceLog.d("Formatted data, but result content was null: " + data);
                return;
            }

            if (formattedData.getSpan() != null) {
                traceManager.addSpanToActiveTrace(formattedData.getSpan());
            } else {
                Executors.newSingleThreadExecutor().execute(() -> dataStorage.saveFormattedData(formattedData));
            }
        }
    }
}
