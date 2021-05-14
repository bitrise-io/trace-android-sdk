package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.Data;
import java.util.Objects;

/**
 * Dummy implementation of DataCollector for testing purposes.
 */
public class DummyDataCollector implements DataCollector {

  /**
   * A name for the DummyDataCollector. Used to test different and same type behaviours.
   */
  @NonNull
  private final String name;

  /**
   * Constructor for class.
   *
   * @param name the name for this instance.
   */
  public DummyDataCollector(@NonNull final String name) {
    this.name = name;
  }

  @NonNull
  @Override
  public Data collectData() {
    return new Data(this);
  }

  @Override
  public long getIntervalMs() {
    return 5 * 1000 * 60;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DummyDataCollector)) {
      return false;
    }
    final DummyDataCollector dummyDataCollector = (DummyDataCollector) o;
    return Objects.equals(name, dummyDataCollector.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
