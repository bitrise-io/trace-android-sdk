package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;

/**
 * Abstract class for common functions for {@link DataListener}s.
 */
public abstract class BaseDataListener implements DataListener {

    /**
     * Indicates that this {@link DataListener} is active or not.
     */
    private boolean active;

    /**
     * The {@link DataManager} to handle the collected {@link Data}.
     */
    @NonNull
    protected DataManager dataManager;

    @Override
    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onDataCollected(@NonNull final Data data) {
        dataManager.handleReceivedData(data);
    }

    @VisibleForTesting
    public void setDataManager(@NonNull final DataManager dataManager) {
        this.dataManager = dataManager;
    }
}
