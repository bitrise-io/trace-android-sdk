package io.bitrise.trace.data.collector.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.data.trace.TraceManager;

/**
 * Base abstract class for all network data related collectors.
 */
public abstract class NetworkDataListener implements DataListener {

    /**
     * The {@link DataManager} to forward the collected {@link Data}.
     */
    @NonNull
    protected DataManager dataManager;
    /**
     * The {@link TraceManager} that will provide the IDs for the {@link NetworkData}.
     */
    @NonNull
    protected TraceManager traceManager;
    /**
     * Indicates that the given NetworkDataListener is active or not.
     */
    protected boolean active;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public NetworkDataListener(@NonNull final Context context) {
        this.dataManager = DataManager.getInstance(context);
        this.traceManager = ApplicationTraceManager.getInstance(context);
    }

    /**
     * Temporary workaround method to provide a non null root ID, when there is none. This is needed,
     * because in some cases there will be no root spans, for example in case of Services.
     *
     * @return the ID of the root Span.
     */
    @NonNull
    protected String getRootSpanId() {
        // TODO remove this, when there will be always a parent span (e.g: services will be spans too).
        final String rootSpanId = traceManager.getRootSpanId();
        return rootSpanId == null ? "" : rootSpanId;
    }

    /**
     * Sets the dataManager to a test dataManager instance. Used only for testing.
     *
     * @param dataManager the dataManager to use for testing.
     */
    @VisibleForTesting
    public void setDataManager(@NonNull final DataManager dataManager) {
        this.dataManager = dataManager;
    }
}
