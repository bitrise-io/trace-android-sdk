package io.bitrise.trace.data.collector.network.urlconnection;

import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Custom implementation of the {@link HttpURLConnection}
 */
public class TraceHttpURLConnection extends HttpURLConnection {

    private URL url;
    private Proxy proxy;
    @VisibleForTesting
    Response response;
    @VisibleForTesting
    OkHttpClient okHttpClient;

    protected TraceHttpURLConnection(URL url) {
        this(url, null);
    }

    public TraceHttpURLConnection(URL url, Proxy proxy) {
        super(url);
        this.url = url;
        this.proxy = proxy;

        // OkHttp became the engine that powers HttpUrlConnection as of Android 4.4 (sdk 20)
        okHttpClient = TraceOkHttpClient.getOkHttpClient(proxy, getConnectTimeout(), getInstanceFollowRedirects());
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        super.setInstanceFollowRedirects(followRedirects);
        okHttpClient = TraceOkHttpClient.getOkHttpClient(proxy, getConnectTimeout(), getInstanceFollowRedirects());
    }

    @Override
    public void disconnect() {
        okHttpClient = null;
    }

    @Override
    public boolean usingProxy() {
        return proxy != null;
    }

    @Override
    public void connect() throws IOException {
        if (okHttpClient != null) {

            Request.Builder requestBuilder = new Request.Builder()
                    .headers(TraceURLConnectionUtils.getHeaders(getHeaderFields()))
                    .url(url);
            Request request = requestBuilder.build();

            // we're calling this synchronously as it's expected others called this in another thread
            // and we want to ensure that the getResponseCode and getInputStream() work as expected.
            response = okHttpClient.newCall(request).execute();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (response == null) {
            throw new IOException("must call .connect() on HttpURLConnection.");
        }
        if (response.body() == null) {
            throw new IOException("response.body() was null.");
        }
        return response.body().byteStream();
    }

    @Override
    public int getResponseCode() throws IOException {
        if (response == null) {
            throw new IOException("must call .connect() on HttpURLConnection.");
        }
        return response.code();
    }
}
