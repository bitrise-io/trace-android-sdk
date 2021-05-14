package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.Data;

/**
 * Collects {@link Data} from a given non event-driven source.
 */
public interface DataCollector extends DataSource {

  /**
   * Gets the Data from the source.
   *
   * @return the Data.
   */
  @NonNull
  Data collectData();

  /**
   * Gets the interval of the collection for the given DataSource.
   *
   * @return the interval in milliseconds.
   */
  long getIntervalMs();
}