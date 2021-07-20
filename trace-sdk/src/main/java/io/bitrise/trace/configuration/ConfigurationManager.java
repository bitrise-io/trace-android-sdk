package io.bitrise.trace.configuration;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.application.ApplicationVersionCodeDataCollector;
import io.bitrise.trace.data.collector.application.ApplicationVersionNameDataCollector;
import io.bitrise.trace.data.collector.cpu.ApplicationCpuUsageDataCollector;
import io.bitrise.trace.data.collector.cpu.SystemCpuUsageDataCollector;
import io.bitrise.trace.data.collector.crash.TraceCrashDataListener;
import io.bitrise.trace.data.collector.device.DeviceCarrierDataCollector;
import io.bitrise.trace.data.collector.device.DeviceIdDataCollector;
import io.bitrise.trace.data.collector.device.DeviceLocaleDataCollector;
import io.bitrise.trace.data.collector.device.DeviceModelDataCollector;
import io.bitrise.trace.data.collector.device.DeviceNetworkTypeDataCollector;
import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.collector.memory.ApplicationUsedMemoryDataCollector;
import io.bitrise.trace.data.collector.memory.SystemMemoryDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.collector.view.ActivityStateDataListener;
import io.bitrise.trace.data.collector.view.ApplicationForegroundStateDataListener;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.collector.view.FragmentStateDataListener;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Singleton;

/**
 * Manages the configuration for the SDK.
 */
@Singleton
public class ConfigurationManager {

  @VisibleForTesting
  static volatile boolean initialised;
  @Nullable
  @VisibleForTesting
  static Map<String, Object> configurationMap;
  @Nullable
  private static volatile ConfigurationManager configurationManager;

  private ConfigurationManager() {
    // nop
  }

  /**
   * Gets an instance of the {@link ConfigurationManager}. Should be initialised with
   * {@link #init(Context)} before using any method.
   *
   * @return the ConfigurationManager.
   */
  @NonNull
  public static synchronized ConfigurationManager getInstance() {
    if (configurationManager == null) {
      configurationManager = new ConfigurationManager();
    }
    return configurationManager;
  }

  /**
   * Creates an instance of the Configuration Manager for debugging uses only. You can pass a
   * token that should be used for the trace addon configuration.
   *
   * @param token  the token that should be used to work with trace.
   * @param config items to add to the configurationMap e.g. VERSION_CODE.
   * @return the ConfigurationManager instance.
   */
  @NonNull
  public static synchronized ConfigurationManager getDebugInstance(@NonNull final String token,
                                                   @NonNull final Map<String, Object> config) {
    if (configurationManager == null) {
      configurationManager = new ConfigurationManager();
    }
    configurationMap = new HashMap<>();
    configurationMap.put(ConfigurationConstants.BITRISE_BC_TOKEN_KEY, token);
    configurationMap.putAll(config);
    initialised = true;

    return configurationManager;
  }

  /**
   * Resets the state of the ConfigurationManager.
   */
  public static synchronized void reset() {
    initialised = false;
    configurationMap = null;
    configurationManager = null;
  }

  /**
   * Initialises the {@link ConfigurationManager}.
   *
   * @param context the Android Context.
   */
  public static synchronized void init(@NonNull final Context context) {
    if (initialised) {
      return;
    }

    getInstance();
    initialised = true;
    configurationMap = new HashMap<>();

    try {
      importConfigurationFromBuildConfig(context);
      importConfigurationFromResValues(context);
    } catch (Resources.NotFoundException exception) {
      TraceLog.e(LogMessageConstants.CONFIGURATION_MANAGER_COULD_NOT_FIND_RESOURCES);
    }

  }

  /**
   * Accesses the BuildConfig class is get with reflection and reads out the stored variables
   * to the {@link #configurationMap}.
   *
   * @param context the Android Context.
   * @deprecated as of version 0.0.6, configurations should be passed as resources, like
   *     {@link #importConfigurationFromResValues(Context)}.
   */
  @Deprecated
  private static void importConfigurationFromBuildConfig(@NonNull final Context context) {
    try {
      final Class<?> buildConfigClass = Class.forName(getBuildConfigClassName(context));
      final Field[] declaredFields = buildConfigClass.getDeclaredFields();
      final BuildConfig buildConfig = new BuildConfig();
      for (@NonNull final Field field : declaredFields) {
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
          try {
            configurationMap.put(field.getName(), field.get(buildConfig));
          } catch (final IllegalAccessException ignored) {
            // Left intentionally blank
          }
        }
      }
    } catch (final Exception ignored) {
      // Left intentionally blank
    }
  }

  /**
   * Accesses to resource values that are needed for the configuration. Adds them to the
   * {@link #configurationMap}.
   *
   * @param context the Android Context.
   * @throws Resources.NotFoundException when the token cannot be found.
   */
  private static void importConfigurationFromResValues(@NonNull final Context context)
      throws Resources.NotFoundException {
    if (hasToken()) {
      return;
    }
    if (configurationMap == null) {
      return;
    }

    final int tokenRes =
        context.getResources().getIdentifier(ConfigurationConstants.BITRISE_BC_TOKEN_KEY,
            "string", context.getPackageName());
    configurationMap.put(ConfigurationConstants.BITRISE_BC_TOKEN_KEY,
        context.getString(tokenRes));
  }

  /**
   * Gets the package name of the BuildConfig class, without any suffixes that were added in
   * the build.gradle.
   *
   * @param context the Android Context.
   * @return the package ID.
   */
  @NonNull
  public static String getBuildConfigClassName(@NonNull final Context context) {
    final int resId = context.getResources()
                             .getIdentifier(ConfigurationConstants.BITRISE_BC_PACKAGE_NAME_KEY,
                                 "string",
                                 context.getPackageName());
    return context.getString(resId) + ".BuildConfig";
  }

  /**
   * Returns the state of the {@link ConfigurationManager}.
   *
   * @return {@code true} if initialised, {@code false} otherwise.
   */
  public static boolean isInitialised() {
    return initialised;
  }

  /**
   * Checks if the current configuration has a token or not.
   *
   * @return {@code true} if there is an entry for token, {@code false} otherwise.
   */
  public static boolean hasToken() {
    return configurationMap.containsKey(ConfigurationConstants.BITRISE_BC_TOKEN_KEY);
  }

  /**
   * Gets the token for the communication.
   *
   * @return the token.
   */
  @NonNull
  public String getToken() {
    final Object token = getConfigItem(ConfigurationConstants.BITRISE_BC_TOKEN_KEY);
    if (token instanceof String) {
      return (String) token;
    }
    throw new IllegalStateException("Token should not be empty!");
  }

  /**
   * Gets the config item with the given key.
   *
   * @param key the key.
   * @return the value of the key, or {@code null} when there is no such key.
   */
  @Nullable
  public Object getConfigItem(@NonNull final String key) {
    checkIsInitialisedOrThrowException();

    return configurationMap == null ? null : configurationMap.get(key);
  }

  /**
   * Gets whether the app using our library is in debug or release mode. If it can't find the
   * debug build config value, it defaults to false.
   *
   * @return true if the app is in debug mode, false if it's in release mode.
   */
  public boolean isAppDebugBuild() {
    final Object debugBuildConfigValue = getConfigItem("DEBUG");
    if (debugBuildConfigValue instanceof Boolean) {
      return (Boolean) debugBuildConfigValue;
    }
    return false;
  }

  /**
   * Creates a new set of allowed {@link DataCollector}s.
   *
   * @param context the Android Context.
   * @return the DataCollectors.
   */
  @NonNull
  public Set<DataCollector> getRecurringDataCollectors(@NonNull final Context context) {
    final Set<DataCollector> dataCollectors = new HashSet<>();
    dataCollectors.add(new ApplicationUsedMemoryDataCollector(context));
    dataCollectors.add(new SystemMemoryDataCollector(context));
    dataCollectors.add(new SystemCpuUsageDataCollector());
    dataCollectors.add(new ApplicationCpuUsageDataCollector());
    return dataCollectors;
  }

  /**
   * Creates a new set of {@link DataCollector}s that only need to collect their data once during
   * the lifecycle of the application e.g. Application version codes.
   *
   * @param context the Android Context.
   * @return the DataCollectors.
   */
  @NonNull
  public Set<DataCollector> getSingleDataCollectors(@NonNull final Context context) {
    final Set<DataCollector> dataCollectors = new HashSet<>();
    dataCollectors.add(new ApplicationVersionNameDataCollector(
        context.getPackageManager(),
        context.getPackageName()));
    dataCollectors.add(new ApplicationVersionCodeDataCollector(
        context.getPackageManager(),
        context.getPackageName()));
    dataCollectors.add(new DeviceModelDataCollector());
    dataCollectors.add(new DeviceOsVersionDataCollector());
    dataCollectors.add(new DeviceNetworkTypeDataCollector(context));
    dataCollectors.add(new DeviceCarrierDataCollector(context));
    dataCollectors.add(new DeviceIdDataCollector(context));
    dataCollectors.add(new DeviceLocaleDataCollector(context));
    dataCollectors.add(new DeviceRootedDataCollector(context));
    return dataCollectors;
  }

  /**
   * Creates a new set of allowed {@link DataListener}s.
   *
   * @param context the Android Context.
   * @return the DataListeners.
   */
  @NonNull
  public LinkedHashSet<DataListener> getDataListeners(@NonNull final Context context) {
    checkIsInitialisedOrThrowException();
    final LinkedHashSet<DataListener> dataListeners = new LinkedHashSet<>();
    final ApplicationForegroundStateDataListener applicationForegroundStateDataListener =
        new ApplicationForegroundStateDataListener(context);
    final ActivityStateDataListener activityStateDataListener =
        new ActivityStateDataListener(context);

    dataListeners.add(activityStateDataListener);
    dataListeners
        .add(new ApplicationStartUpDataListener(context, applicationForegroundStateDataListener));
    dataListeners.add(applicationForegroundStateDataListener);
    dataListeners.add(new FragmentStateDataListener(context, activityStateDataListener));
    dataListeners.add(new OkHttpDataListener(context));
    dataListeners.add(new TraceCrashDataListener(context));
    return dataListeners;
  }

  /**
   * Check if the {@link ConfigurationManager} is initialised or not. Throws
   * IllegalStateException when not.
   *
   * @throws IllegalStateException when the ConfigurationManager hasn't been initialised.
   */
  private void checkIsInitialisedOrThrowException() {
    if (initialised) {
      return;
    }
    throw new IllegalStateException(
        "ConfigurationManager should be initialised, before using a method!");
  }

  /**
   * Gets the required {@link ResourceLabel}s for the network communication.
   *
   * @return the Set of required ResourceLabels.
   */
  @NonNull
  public Set<ResourceLabel> getRequiredResourceLabels() {
    return new HashSet<>(Arrays.asList(
        ResourceLabel.APPLICATION_VERSION_NAME,
        ResourceLabel.APPLICATION_VERSION_CODE,
        ResourceLabel.APPLICATION_PLATFORM,
        ResourceLabel.DEVICE_ID));
  }
}
