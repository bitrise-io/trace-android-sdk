package io.bitrise.trace.data.management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.collector.DataSource;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.application.ApplicationVersionCodeDataFormatter;
import io.bitrise.trace.data.management.formatter.application.ApplicationVersionNameDataFormatter;
import io.bitrise.trace.data.management.formatter.cpu.ApplicationCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.cpu.SystemCpuDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceCarrierDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceIdDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceLocaleDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceModelDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceNetworkTypeDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceOsDataFormatter;
import io.bitrise.trace.data.management.formatter.device.DeviceRootedDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.ApplicationUsedMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.memory.SystemMemoryDataFormatter;
import io.bitrise.trace.data.management.formatter.network.NetworkDataFormatter;
import io.bitrise.trace.data.management.formatter.view.ActivityStateDataFormatter;
import io.bitrise.trace.data.management.formatter.view.ApplicationStartUpDataFormatter;
import io.bitrise.trace.data.management.formatter.view.FragmentStateDataFormatter;
import io.bitrise.trace.data.trace.Trace;
import io.opencensus.proto.metrics.v1.Metric;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 * Delegates the formatting of the different {@link Data} inputs to {@link Metric}s and
 * {@link Trace}s to the proper {@link Formatter} instances. This enables us, that a given
 * {@link DataSource} is independent of the format of the produced Data will be saved in.
 */
@Singleton
public class DataFormatterDelegator {

  @Nullable
  private static volatile DataFormatterDelegator dataFormatterDelegator;
  @NonNull
  private final Map<DataSourceType, Formatter> formatterMap;

  /**
   * Constructor for class.
   */
  private DataFormatterDelegator() {
    this.formatterMap = new HashMap<>();
  }

  /**
   * Gets an instance of the class.
   *
   * @return the DataFormatterDelegator.
   */
  public static synchronized DataFormatterDelegator getInstance() {
    if (dataFormatterDelegator == null) {
      dataFormatterDelegator = new DataFormatterDelegator();
    }
    return dataFormatterDelegator;
  }

  /**
   * Delegates to a {@link Formatter} instance the given {@link Data}, which formats and returns a
   * {@link FormattedData} instance.
   *
   * @param data the Data to format.
   * @return the FormattedData or an empty array when the content is empty.
   */
  @NonNull
  public FormattedData[] formatData(@NonNull final Data data) {
    final Formatter formatter = getFormatter(data.getDataSourceType());
    return formatter.formatData(data);
  }

  /**
   * Reads the {@link DataSourceType} of the Data and based on DataSourceType returns the
   * proper {@link Formatter}.
   *
   * @param dataSourceType the DataSourceType.
   * @return the Formatter instance.
   */
  @NonNull
  private synchronized Formatter getFormatter(@NonNull final DataSourceType dataSourceType) {
    if (formatterMap.containsKey(dataSourceType)) {
      return formatterMap.get(dataSourceType);
    }

    Formatter formatter;
    switch (dataSourceType) {
      case ACTIVITY_STATE:
        formatter = new ActivityStateDataFormatter();
        break;
      case APP_CPU_USAGE:
        formatter = new ApplicationCpuDataFormatter();
        break;
      case APP_START:
        formatter = new ApplicationStartUpDataFormatter();
        break;
      case APP_USED_MEMORY:
        formatter = new ApplicationUsedMemoryDataFormatter();
        break;
      case APP_VERSION_NAME:
        formatter = new ApplicationVersionNameDataFormatter();
        break;
      case APP_VERSION_CODE:
        formatter = new ApplicationVersionCodeDataFormatter();
        break;
      case DEVICE_CARRIER:
        formatter = new DeviceCarrierDataFormatter();
        break;
      case DEVICE_ID:
        formatter = new DeviceIdDataFormatter();
        break;
      case DEVICE_LOCALE:
        formatter = new DeviceLocaleDataFormatter();
        break;
      case DEVICE_MODEL:
        formatter = new DeviceModelDataFormatter();
        break;
      case DEVICE_OS:
        formatter = new DeviceOsDataFormatter();
        break;
      case DEVICE_ROOTED:
        formatter = new DeviceRootedDataFormatter();
        break;
      case FRAGMENT_STATE:
        formatter = new FragmentStateDataFormatter();
        break;
      case NETWORK_CALL_OKHTTP:
        formatter = new NetworkDataFormatter();
        break;
      case NETWORK_TYPE:
        formatter = new DeviceNetworkTypeDataFormatter();
        break;
      case SYSTEM_CPU_USAGE:
        formatter = new SystemCpuDataFormatter();
        break;
      case SYSTEM_USED_MEMORY:
        formatter = new SystemMemoryDataFormatter();
        break;
      default:
        throw new IllegalArgumentException(String.format("Unknown DataSourceType %1$s",
            dataSourceType.className));
    }
    formatterMap.put(dataSourceType, formatter);
    return formatter;
  }
}