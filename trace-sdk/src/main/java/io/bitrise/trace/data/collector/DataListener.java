package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.Data;

/**
 * Collects {@link Data} from a given event-driven source.
 */
public interface DataListener extends DataSource {

  /**
   * Starts the collection of the Data.
   */
  void startCollecting();

  /**
   * Stops the collection of the Data.
   */
  void stopCollecting();

  /**
   * Called when a Data collection happens.
   *
   * @param data the collected Data.
   */
  void onDataCollected(@NonNull Data data);

  /**
   * Determines if the collection has been started with {@link #startCollecting()} or not.
   *
   * @return {@code true} if yes, {@code false} otherwise.
   */
  boolean isActive();
}
