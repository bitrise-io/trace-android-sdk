package io.bitrise.trace;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.net.URL;

import javax.inject.Singleton;

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

/**
 * The core class of the Trace SDK.
 */
@Singleton
public class TraceSdk {

    @Nullable
    private static volatile TraceSdk traceSdk;

    /**
     * The owner of this SDK.
     */
    public static final String COMPANY = "Bitrise";

    /**
     * The name for the product.
     */
    public static final String NAME = "Trace Android";

    private TraceSdk() {
        // nop
    }

    /**
     * Initializes the Trace SDK plugin.
     *
     * @param context the Android Context.
     */
    public synchronized static void init(@NonNull final Context context) {
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
            initNetworkTracing();
        } else {
            TraceLog.e(new TraceException.TraceConfigNotInitialisedException());
        }
    }

    /**
     * Initialises the custom trace logger based on the current build type.
     * Note: In future we would like a way for this to be customisable by developers.
     */
    private static void initLogger() {
        if (ConfigurationManager.getInstance().isAppDebugBuild()) {
            TraceLog.makeAndroidLogger();
        } else {
            TraceLog.makeErrorOnlyLogger();
        }
    }

    /**
     * Check if the Trace SDK has been initialised (the {@link #init(Context)} method has been called or not.
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
     * Initializes the {@link TraceActivityLifecycleTracker} to track when the application enters or leaves the
     * foreground and background.
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
     * Initializes the {@link TraceActivityLifecycleTracker} to track when the activity lifecycle changes.
     *
     * @param application the Android Application.
     */
    public static void initLifeCycleListener(@NonNull final Application application) {
        final TraceActivityLifecycleTracker traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(
                application);
        application.registerActivityLifecycleCallbacks(traceActivityLifecycleTracker);
    }

    /**
     * Initialises the network tracing, currently initialisaing {@link java.net.URLConnection}
     * type network requests to use our {@link TraceURLStreamHandlerFactory) instead.
     */
    private static void initNetworkTracing() {
        try {
            URL.setURLStreamHandlerFactory(new TraceURLStreamHandlerFactory());
            TraceLog.d(LogMessageConstants.URL_CONNECTION_REQUESTS_SUCCESS);
        } catch (Error e) {
            // this will catch the java.lang.Error: factory already defined error which should
            // only be caused from the integration tests calling init multiple times.
            TraceLog.w(
                    e,
                    LogMessageConstants.SET_URL_STREAM_HANDLER_FACTORY_FAILED);
        }
    }
}