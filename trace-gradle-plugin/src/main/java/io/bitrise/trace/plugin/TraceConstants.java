package io.bitrise.trace.plugin;

import io.bitrise.trace.plugin.configuration.Version;

/**
 * Holds the SDK level constants.
 */
public final class TraceConstants {

  /**
   * The name of the dependency for the plugin.
   */
  public static final String PLUGIN_DEPENDENCY_GROUP = TraceConstants.class.getPackage().getName();

  /**
   * The name of the dependency for the plugin.
   */
  public static final String PLUGIN_DEPENDENCY_NAME = "trace-gradle-plugin";

  /**
   * The name of this plugin.
   */
  public static final String PLUGIN_ID = PLUGIN_DEPENDENCY_GROUP;

  /**
   * The name of the dependency for the 'trace-sdk'.
   */
  public static final String TRACE_SDK_DEPENDENCY_NAME = "trace-sdk";

  /**
   * The group of the dependency for {@link #TRACE_SDK_DEPENDENCY_NAME}.
   */
  public static final String TRACE_SDK_DEPENDENCY_GROUP_NAME = "io.bitrise.trace";

  /**
   * The name of the configuration file that is used to configure the given application.
   */
  public static final String BITRISE_ADDONS_CONFIGURATION_FILE =
      "bitrise-addons-configuration.json";

  /**
   * The name of the class that extends the Android Application class.
   */
  public static final String TRACE_APPLICATION_CLASS_NAME = "TraceApplication";

  /**
   * The name of the package where the Trace Application class should be located.
   */
  public static final String TRACE_APPLICATION_PACKAGE_NAME = "io.bitrise.trace";

  /**
   * The name of the class that sends the Metrics.
   */
  public static final String TRACE_METRIC_SENDER_CLASS_NAME = "MetricSender";

  /**
   * The name of the class that sends the Traces.
   */
  public static final String TRACE_TRACE_SENDER_CLASS_NAME = "TraceSender";

  /**
   * The fully qualified name of the class that sends the Traces.
   */
  public static final String TRACE_TRACE_SENDER_CLASS_FULL_NAME =
      TRACE_APPLICATION_PACKAGE_NAME + ".network." + TRACE_TRACE_SENDER_CLASS_NAME;

  /**
   * The fully qualified name of the class that sends the Metrics.
   */
  public static final String TRACE_METRIC_SENDER_CLASS_FULL_NAME =
      TRACE_APPLICATION_PACKAGE_NAME + ".network." + TRACE_METRIC_SENDER_CLASS_NAME;

  /**
   * The name of the class with the package name that extends the Android Application class.
   */
  public static final String TRACE_APPLICATION_CLASS_FULL_NAME =
      TRACE_APPLICATION_PACKAGE_NAME + "." + TRACE_APPLICATION_CLASS_NAME;

  /**
   * The version the config file that is supported by the plugin.
   */
  public static final String TRACE_CONFIGURATION_SUPPORTED_FILE_VERSION = "1.0.0";

  /**
   * The smallest compatible version of the Android Gradle Plugin. Lower versions used in
   * applications using the SDK may have runtime or build errors, so they require different code.
   */
  public static final Version TRACE_ANDROID_GRADLE_PLUGIN_COMPATIBLE_VERSION = new Version("3.5.0");

  /**
   * The key for the package name of the BuildConfig file.
   */
  public static final String BITRISE_BC_PACKAGE_NAME_KEY = "bitrise_package_name";

  private TraceConstants() {
    throw new IllegalStateException(
        "Should not be instantiated, used only for storing static members!");
  }
}
