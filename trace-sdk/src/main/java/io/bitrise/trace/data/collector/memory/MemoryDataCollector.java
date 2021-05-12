package io.bitrise.trace.data.collector.memory;

import io.bitrise.trace.data.collector.DataCollector;

/**
 * Base abstract class for memory related {@link DataCollector}s.
 */
public abstract class MemoryDataCollector implements DataCollector {

    @Override
    public long getIntervalMs() {
        return 15000;
    }
}
