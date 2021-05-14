package io.bitrise.trace.data.collector.application;

import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * Base abstract class for Application related {@link DataCollector}s. These {@link Data}
 * should be collected once per session.
 */
public abstract class ApplicationDataCollector implements DataCollector {

  @Override
  public long getIntervalMs() {
    return 0;
  }
}
