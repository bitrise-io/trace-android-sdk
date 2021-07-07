package io.bitrise.trace.data.collector.application;

import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects version code of the Application.
 */
public class ApplicationVersionCodeDataCollector extends ApplicationDataCollector {

  @NonNull private final PackageManager packageManager;
  @NonNull private final String packageName;

  /**
   * Constructor for class.
   *
   * @param packageManager - the customer application {@link PackageManager}.
   * @param packageName - the customer application package name.
   */
  public ApplicationVersionCodeDataCollector(@NonNull final PackageManager packageManager,
                                             @NonNull final String packageName) {
    this.packageManager = packageManager;
    this.packageName = packageName;
  }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getAppVersionCode());
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  /**
   * Gets the version code of the Application.
   *
   * @return the version code.
   */
  @NonNull
  private String getAppVersionCode() {
    try {
      return String.valueOf(packageManager.getPackageInfo(packageName, 0).versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      return "unknown";
    }
  }
}
