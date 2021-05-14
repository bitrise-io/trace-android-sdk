package io.bitrise.trace.data.management;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;

/**
 * Interface for all {@link Data} formatting instances.
 */
public interface Formatter {

  /**
   * Formats the given {@link Data} to an array of {@link FormattedData}.
   *
   * @param data the Data to format.
   * @return an Array of FormattedData.
   */
  @NonNull
  FormattedData[] formatData(@NonNull Data data);
}
