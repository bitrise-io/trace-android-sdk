package io.bitrise.trace.data.collector.cpu;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataCollector;

/**
 * Base abstract class for CPU data collection.
 */
public abstract class CpuUsageDataCollector implements DataCollector {

  @Override
  public long getIntervalMs() {
    return 15000;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }
}
