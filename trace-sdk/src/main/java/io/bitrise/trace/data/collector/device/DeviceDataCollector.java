package io.bitrise.trace.data.collector.device;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.DataCollector;

/**
 * Base abstract class for device related {@link Data} collection.
 */
public abstract class DeviceDataCollector implements DataCollector {

    /**
     * Value when no network is available.
     */
    static final String NO_NETWORK = "NO_NETWORK";

    @Override
    public long getIntervalMs() {
        return 0;
    }
}
