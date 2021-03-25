package io.bitrise.trace.data.collector.network.okhttp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;

import io.bitrise.trace.data.collector.network.NetworkDataListener;
import io.bitrise.trace.data.collector.network.OkHttpCallProcessor;
import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.trace.TraceManager;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * DataListener implementation for listening for OkHttp network calls. Must register to the
 * {@link TraceNetworkListener} to receive the calls.
 */
public class OkHttpDataListener extends NetworkDataListener implements OkHttpCallProcessor {

    public OkHttpDataListener(@NonNull final Context context) {
        super(context);
    }

    @Override
    public void startCollecting() {
        TraceNetworkListener.getInstance().registerOkHttpCallProcessor(this);
        active = true;
    }

    @Override
    public void stopCollecting() {
        TraceNetworkListener.getInstance().unregisterOkHttpCallProcessor(this);
        active = false;
    }

    @Override
    public void onDataCollected(@NonNull final Data data) {
        dataManager.handleReceivedData(data);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    /**
     * Gets the size of the given {@link Request} in bytes.
     *
     * @param request the given Request.
     * @return the size in bytes.
     */
    @VisibleForTesting
    static long getRequestSize(@NonNull final Request request) {
        final Headers headers = request.headers();
        long size = headers.byteCount();
        final RequestBody requestBody = request.body();
        if (requestBody != null) {
            try {
                size += requestBody.contentLength();
            } catch (final IOException e) {
                TraceLog.w(
                        e,
                        LogMessageConstants.COULD_NOT_DETERMINE_HEADER_SIZE);
            }
        }
        return size;
    }

    /**
     * Gets the size of the given {@link Response} in bytes.
     *
     * @param response the given Response.
     * @return the size in bytes.
     */
    @VisibleForTesting
    static long getResponseSize(@NonNull final Response response) {
        final Headers headers = response.headers();
        long size = headers.byteCount();
        final ResponseBody responseBody = response.body();
        if (responseBody != null) {
            size += responseBody.contentLength();
        }
        return size;
    }

    /**
     * Formats the given {@link HttpUrl} and gets the String value of it. It should be the concatenation of the scheme,
     * host and path segment values.
     *
     * @param httpUrl the given HttpUrl.
     * @return the formatted url.
     */
    @VisibleForTesting
    @NonNull
    static String formatUrl(@NonNull final HttpUrl httpUrl) {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(httpUrl.scheme()).append("://").append(httpUrl.host());
        for (@NonNull final String pathSegment : httpUrl.pathSegments()) {
            if (pathSegment.isEmpty()) {
                continue;
            }
            urlBuilder.append("/").append(pathSegment);
        }
        return urlBuilder.toString();
    }

    /**
     * Creates a {@link NetworkData} from the given arguments.
     *
     * @param request    the {@link Request}.
     * @param response   the {@link Response}.
     * @param start      the start time of the network call in milliseconds.
     * @param end        the end time of the network call in milliseconds.
     * @param spanId     the Span ID.
     * @param rootSpanId the Span ID of the root Span.
     * @return the created NetworkData.
     */
    @VisibleForTesting
    @NonNull
    static NetworkData createNetworkData(@NonNull final Request request,
                                         @NonNull final Response response,
                                         final long start,
                                         final long end,
                                         @NonNull final String spanId,
                                         @NonNull final String rootSpanId) {
        return new NetworkData(spanId, rootSpanId).setMethod(request.method())
                                                  .setRequestSize(getRequestSize(request))
                                                  .setResponseSize(getResponseSize(response))
                                                  .setUrl(formatUrl(request.url()))
                                                  .setStatusCode(response.code())
                                                  .setStart(start).setEnd(end);
    }

    /**
     * Creates a {@link Data} object from a {@link NetworkData} object.
     *
     * @param networkData the given NetworkData.
     * @return the created Data.
     */
    @VisibleForTesting
    @NonNull
    Data createData(@NonNull final NetworkData networkData) {
        final Data data = new Data(this);
        data.setContent(networkData);
        return data;
    }

    @Override
    public void processOkHttpCall(@NonNull final Request request,
                                  @NonNull final Response response,
                                  final long start,
                                  final long end) {
        final NetworkData networkData = createNetworkData(request, response, start, end,
                traceManager.createSpanId(false), getRootSpanId());

        onDataCollected(createData(networkData));
    }

    @VisibleForTesting
    void setTraceManager(@NonNull final TraceManager traceManager) {
        this.traceManager = traceManager;
    }
}