package io.bitrise.trace;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.network.urlconnection.TraceURLStreamHandlerFactory;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.SessionManager;
import io.bitrise.trace.utils.TraceException;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import java.net.URL;
import java.util.List;
import javax.inject.Singleton;

/**
 * The core class of the Trace SDK.
 */
@Singleton
public class TraceSdk {

  /**
   * The owner of this SDK.
   */
  public static final String COMPANY = "Bitrise";
  /**
   * The name for the product.
   */
  public static final String NAME = "Trace Android";

  @VisibleForTesting
  @Nullable
  static volatile TraceSdk traceSdk;
  /**
   * The TraceSdk has a debug mode - currently this will mean more debug level log messages.
   *
   * <p>Please note if you are not using a debug build, and or minify is enabled it can affect
   * these logs, and they can be stripped out depending on your configuration. You also need to
   * ensure that the TraceSdk has been initialised before setting the debug enabled mode.
   */
  private static boolean DEBUG_ENABLED = false;


  static boolean isNetworkTracingEnabled = false;

  private TraceSdk() {
    // nop
  }

  /**
   * Initializes the Trace SDK.
   *
   * @param context the Android Application context.
   */
  public static synchronized void init(@NonNull final Context context) {
    init(context, null);
  }

  /**
   * Initializes the Trace SDK with customizable options.
   *
   * @param context the Android Application context.
   * @param options an optional list of {@link TraceOption} objects, providing null means no
   *                special options.
   */
  public static synchronized void init(@NonNull final Context context,
                                       @Nullable final List<TraceOption> options) {
    if (isInitialised()) {
      return;
    }

    initConfigurations(context);

    if (ConfigurationManager.isInitialised()) {

      initLogger();

      TraceLog.i(LogMessageConstants.INITIALISING_SDK);
      traceSdk = new TraceSdk();

      TraceLog.i(LogMessageConstants.TRACE_SDK_SUCCESSFULLY_INITIALISED);
      initSessionManager();
      initDataCollection(context);
      initLifeCycleListener(context);
      initNetworkTracing(options);
      TraceLog.i(String.format(LogMessageConstants.TRACE_DEBUG_FLAG_STATUS, DEBUG_ENABLED));
    } else {
      TraceLog.e(new TraceException.TraceConfigNotInitialisedException());
    }
  }

  /**
   * Check if the SDK is in debug mode or not.
   *
   * @return whether the sdk is in debug mode.
   */
  public static boolean isDebugEnabled() {
    return DEBUG_ENABLED;
  }

  /**
   * Flag to enable debug mode - currently this will mean more debug level log messages.
   *
   * <p>Please note if you are not using a debug build, and or minify is enabled it can affect
   * these logs, and they can be stripped out depending on your configuration. You also need to
   * ensure that the TraceSdk has been initialised before setting the debug enabled mode.
   *
   * @param debugEnabled boolean value to enable or disable debug mode in the TraceSdk.
   */
  public static synchronized void setDebugEnabled(final boolean debugEnabled) {
    DEBUG_ENABLED = debugEnabled;
    TraceLog.i(String.format(LogMessageConstants.TRACE_DEBUG_FLAG_STATUS, DEBUG_ENABLED));
  }

  /**
   * Initialises the custom trace logger based on the current build type. Note: In future we
   * would like a way for this to be customisable by developers.
   */
  @VisibleForTesting
  static void initLogger() {
    if (ConfigurationManager.getInstance().isAppDebugBuild()) {
      TraceLog.makeAndroidLogger();
    } else {
      TraceLog.makeErrorOnlyLogger();
    }
  }

  /**
   * Check if the Trace SDK has been initialised (the {@link #init(Context)} method has been
   * called or not.
   *
   * @return {@code true} if yes, {@code false} otherwise.
   */
  public static synchronized boolean isInitialised() {
    return traceSdk != null;
  }

  /**
   * Resets the {@link #traceSdk} member variable to {@code null}. This method does not resets
   */
  @VisibleForTesting
  public static synchronized void reset() {
    traceSdk = null;
    DataManager.reset();
    ApplicationSessionManager.reset();
    TraceActivityLifecycleTracker.reset();
    ApplicationTraceManager.reset();
    ConfigurationManager.reset();
    isNetworkTracingEnabled = false;
  }

  /**
   * Initializes the build configuration for the plugin.
   *
   * @param context the Android Application's context.
   */
  public static void initConfigurations(@NonNull final Context context) {
    ConfigurationManager.init(context);
  }

  /**
   * Initializes the {@link SessionManager} and starts the session.
   */
  public static void initSessionManager() {
    final SessionManager sessionManager = ApplicationSessionManager.getInstance();
    sessionManager.startSession();
  }

  /**
   * Initializes the {@link DataManager} and start the data collection. Also initializes the
   * sending of the Data.
   *
   * @param context the Android Context.
   */
  public static void initDataCollection(@NonNull final Context context) {
    final DataManager dataManager = DataManager.getInstance(context);
    dataManager.startCollection(context);
    dataManager.startSending(context);
  }

  /**
   * Initializes the {@link TraceActivityLifecycleTracker} to track when the application enters
   * or leaves the foreground and background.
   *
   * @param applicationContext the Android Application Context.
   */
  public static void initLifeCycleListener(@NonNull final Context applicationContext) {
    if (applicationContext instanceof Application) {
      initLifeCycleListener((Application) applicationContext);
    } else if (applicationContext.getApplicationContext() instanceof Application) {
      initLifeCycleListener((Application) applicationContext.getApplicationContext());
    } else {
      TraceLog.e(
          new TraceException.ActivityContextNotFoundException(),
          LogMessageConstants.ACTIVITY_LIFECYCLE_COULD_NOT_BE_STARTED);
    }
  }

  /**
   * Initializes the {@link TraceActivityLifecycleTracker} to track when the activity lifecycle
   * changes.
   *
   * @param application the Android Application.
   */
  public static void initLifeCycleListener(@NonNull final Application application) {
    final TraceActivityLifecycleTracker traceActivityLifecycleTracker =
        TraceActivityLifecycleTracker.getInstance(application);
    application.registerActivityLifecycleCallbacks(traceActivityLifecycleTracker);
  }

  /**
   * Initialises the network tracing, currently initialising {@link java.net.URLConnection}
   * type network requests to use our {@link TraceURLStreamHandlerFactory} instead.
   */
  @VisibleForTesting
  static void initNetworkTracing(@Nullable final List<TraceOption> options) {

    if (! TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(options)) {
      TraceLog.w("Network tracing for URL Connection disabled.");
      return;
    }

    try {
      URL.setURLStreamHandlerFactory(new TraceURLStreamHandlerFactory());
      TraceLog.d(LogMessageConstants.URL_CONNECTION_REQUESTS_SUCCESS);
      isNetworkTracingEnabled = true;
    } catch (final Error e) {
      // this will catch the java.lang.Error: factory already defined error which should
      // only be caused from the integration tests calling init multiple times.
      TraceLog.w(
          e, LogMessageConstants.SET_URL_STREAM_HANDLER_FACTORY_FAILED);
    }
  }
}