package io.bitrise.trace.data.collector.application;

import android.content.Context;
import androidx.annotation.NonNull;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects version code of the Application.
 */
public class ApplicationVersionCodeDataCollector extends ApplicationDataCollector {

  @NonNull
  private final Context context;

  /**
   * Constructor for class.
   *
   * @param context the Android Context.
   */
  public ApplicationVersionCodeDataCollector(@NonNull final Context context) {
    this.context = context;
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
    if (!ConfigurationManager.isInitialised()) {
      ConfigurationManager.init(context);
    }
    return String.valueOf(ConfigurationManager.getInstance().getConfigItem("VERSION_CODE"));
  }
}
