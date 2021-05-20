package io.bitrise.trace.data.management.formatter.memory;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;

/**
 * {@link Formatter} implementation, to handle formatting for
 * {@link DataSourceType#SYSTEM_USED_MEMORY}.
 */
public class SystemMemoryDataFormatter extends MemoryDataFormatter {

  @NonNull
  @Override
  public FormattedData[] formatData(@NonNull final Data data) {
    if (data.getContent() == null) {
      return new FormattedData[] {};
    }

    return handleMemoryFormatting((Long) data.getContent(), MemoryType.SYSTEM);
  }
}
