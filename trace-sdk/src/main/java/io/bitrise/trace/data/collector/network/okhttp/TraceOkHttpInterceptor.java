package io.bitrise.trace.data.collector.network.okhttp;

import androidx.annotation.NonNull;

import java.io.IOException;

import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link Interceptor} implementation for interception of okHttp calls.
 */
public class TraceOkHttpInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final long start = TraceClock.getCurrentTimeMillis();
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        final long end = TraceClock.getCurrentTimeMillis();
        TraceNetworkListener.getInstance().processOkHttpCall(request, response, start, end);
        return response;
    }
}
