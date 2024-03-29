package io.bitrise.trace.data.management.formatter.device;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.ResourceDataFormatter;
import io.bitrise.trace.data.resource.ResourceLabel;

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#DEVICE_OS}.
 */
public class DeviceOsDataFormatter extends ResourceDataFormatter {

  @NonNull
  @Override
  public FormattedData[] formatData(@NonNull final Data data) {
    return formatResource(data, ResourceLabel.DEVICE_OS);
  }
}
