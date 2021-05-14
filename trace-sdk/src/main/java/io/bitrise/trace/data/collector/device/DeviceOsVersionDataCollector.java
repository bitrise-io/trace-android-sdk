package io.bitrise.trace.data.collector.device;

import android.os.Build;
import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects the OS version of the device.
 */
public class DeviceOsVersionDataCollector extends DeviceDataCollector {

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getDeviceOsVersion());
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  /**
   * Gets the OS version of the device.
   *
   * @return the OS version of the device.
   */
  @NonNull
  private String getDeviceOsVersion() {
    return String.valueOf(Build.VERSION.SDK_INT);
  }
}
