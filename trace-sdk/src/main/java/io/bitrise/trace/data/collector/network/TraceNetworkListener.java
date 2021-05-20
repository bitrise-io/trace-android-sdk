package io.bitrise.trace.data.collector.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.bitrise.trace.TraceSdk;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class used by the {@link TraceSdk} that is the single point for receiving network calls from the application.
 */
@Singleton
public class TraceNetworkListener implements OkHttpCallProcessor {

    @Nullable
    private static volatile TraceNetworkListener traceNetworkListener;
    /**
     * A lock object for preventing concurrency issues for the {@link #traceNetworkListener}. E.g: when
     * {@link #reset()} and {@link #getInstance()} is called parallel.
     */
    @NonNull
    private static final Object traceNetworkListenerLock = new Object();

    private TraceNetworkListener() {
        // nop
    }

    /**
     * Gets an instance of the class.
     *
     * @return an instance of the class.
     */
    @NonNull
    public static synchronized TraceNetworkListener getInstance() {
        synchronized (traceNetworkListenerLock) {
            if (traceNetworkListener == null) {
                traceNetworkListener = new TraceNetworkListener();
            }
            return traceNetworkListener;
        }
    }

    /**
     * Sets the TraceNetworkListener to a debug instance. Used only for testing.
     * @param listener the TraceNetworkListener to use for the getInstance().
     */
    @VisibleForTesting
    public static synchronized void setDebugTraceNetworkListener(
            @NonNull final TraceNetworkListener listener) {
        synchronized (traceNetworkListenerLock) {
            traceNetworkListener = listener;
        }
    }

    //region OkHttp related members
    @NonNull
    private static final List<OkHttpCallProcessor> OK_HTTP_CALL_PROCESSORS = new ArrayList<>();
    /**
     * A lock object for preventing concurrency issues for OkHttp related methods. E.g: when
     * {@link #processOkHttpCall(Request, Response, long, long)} and
     * {@link #unregisterOkHttpCallProcessor(OkHttpCallProcessor)} is called parallel.
     */
    @NonNull
    private static final Object okHttpProcessorsLock = new Object();

    /**
     * Resets the state of the TraceNetworkListener.
     */
    @VisibleForTesting
    public static synchronized void reset() {
        synchronized (traceNetworkListenerLock) {
            if (traceNetworkListener == null) {
                return;
            }

            synchronized (okHttpProcessorsLock) {
                OK_HTTP_CALL_PROCESSORS.clear();
            }
            traceNetworkListener = null;
        }
    }

    /**
     * Registers the given {@link OkHttpCallProcessor} to start receiving OkHttp calls.
     *
     * @param okHttpCallProcessor the given OkHttpCallProcessor.
     */
    public void registerOkHttpCallProcessor(@NonNull final OkHttpCallProcessor okHttpCallProcessor) {
        synchronized (okHttpProcessorsLock) {
            OK_HTTP_CALL_PROCESSORS.add(okHttpCallProcessor);
        }
    }

    /**
     * Unregisters the given {@link OkHttpCallProcessor} to stop receiving OkHttp calls.
     *
     * @param okHttpCallProcessor the given OkHttpCallProcessor.
     */
    public void unregisterOkHttpCallProcessor(@NonNull final OkHttpCallProcessor okHttpCallProcessor) {
        synchronized (okHttpProcessorsLock) {
            OK_HTTP_CALL_PROCESSORS.remove(okHttpCallProcessor);
        }
    }

    @Override
    public void processOkHttpCall(@NonNull final Request request,
                                  @NonNull final Response response,
                                  final long start,
                                  final long end) {
        synchronized (okHttpProcessorsLock) {
            for (@NonNull final OkHttpCallProcessor okHttpCallProcessor : OK_HTTP_CALL_PROCESSORS) {
                okHttpCallProcessor.processOkHttpCall(request, response, start, end);
            }
        }
    }
    //endregion
}
