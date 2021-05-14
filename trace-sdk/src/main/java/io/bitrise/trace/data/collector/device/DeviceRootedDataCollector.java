package io.bitrise.trace.data.collector.device;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.scottyab.rootbeer.RootBeer;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects whether the device is rooted.
 * Note, we are using @see <a href="https://github.com/scottyab/rootbeer">rootbeer</a>
 * for this detection.
 */
public class DeviceRootedDataCollector extends DeviceDataCollector {

  @VisibleForTesting
  RootBeer rootBeer;

  /**
   * Constructor for class.
   *
   * @param context the Android Context.
   */
  public DeviceRootedDataCollector(@NonNull final Context context) {
    this.rootBeer = new RootBeer(context);
  }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(isRooted());
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  /**
   * Uses RootBeer to determine if the device is currently rooted.
   *
   * @return whether the device is rooted or not.
   */
  private boolean isRooted() {
    return rootBeer.isRooted();
  }
}
