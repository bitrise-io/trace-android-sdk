package io.bitrise.trace.data.collector.network.urlconnection;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import okhttp3.OkHttpClient;

/**
 * A singleton {@link OkHttpClient} to prevent creating multiple clients at any given time.
 */
@Singleton
public class TraceOkHttpClient {

    @VisibleForTesting
    static Map<String, OkHttpClient> okHttpClients = new HashMap<>();
    private static final Object lock = new Object();

    private TraceOkHttpClient() {
        // nop
    }

    /**
     * Returns an {@link OkHttpClient} for the requested Proxy and connectionTimeout, if one
     * hasn't been created, it creates it and saves it for re-use later.
     * @param proxy the requested {@link Proxy}.
     * @param connectionTimeout the requested timeout in milliseconds.
     * @return an {@link OkHttpClient}.
     */
    public static synchronized OkHttpClient getOkHttpClient(@Nullable Proxy proxy,
                                                            int connectionTimeout)  {
       return getOkHttpClient(proxy, connectionTimeout, true);
    }

    /**
     * Returns an {@link OkHttpClient} for the requested Proxy and connectionTimeout, if one
     * hasn't been created, it creates it and saves it for re-use later.
     * @param proxy the requested {@link Proxy}.
     * @param connectionTimeout the requested timeout in milliseconds.
     * @param followRedirects whether the client should follow redirects
     * @return an {@link OkHttpClient}.
     */
    public static synchronized OkHttpClient getOkHttpClient(
            @Nullable Proxy proxy,
            int connectionTimeout,
            boolean followRedirects) {

        synchronized (lock) {

            String requestedOkHttpClientKey = generateOkHttpKey(proxy, connectionTimeout, followRedirects);

            // if we already have one that matches the requested type
            if (okHttpClients.containsKey(requestedOkHttpClientKey)) {
                return okHttpClients.get(requestedOkHttpClientKey);
            }

            // we didn't have one that matched the requested options - we need to build one
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
            if (proxy != null) {
                builder.proxy(proxy);
            }

            // by default okhttp redirects
            if (! followRedirects) {
                builder.followRedirects(false);
            }

            OkHttpClient okHttpClient = builder.build();
            okHttpClients.put(requestedOkHttpClientKey, okHttpClient);
            return okHttpClient;
        }
    }

    /**
     * Generates a unique label for the passed configuration.
     * @param proxy the requested {@link Proxy}.
     * @param connectionTimeout the requested timeout in milliseconds.
     * @return a unique string key.
     */
    @SuppressLint("DefaultLocale")
    @VisibleForTesting
    @NonNull
    static String generateOkHttpKey(@Nullable Proxy proxy, int connectionTimeout, boolean followRedirects) {
        if (proxy != null) {
            return String.format("%s_%d_%b", proxy, connectionTimeout, followRedirects);
        } else {
            return String.format("no_proxy_%d_%b", connectionTimeout, followRedirects);
        }
    }

    /**
     * Resets the current okHttpClient - required only for testing purposes.
     */
    @VisibleForTesting
    public static void reset() {
        synchronized (lock) {
            okHttpClients.clear();
        }
    }
}
