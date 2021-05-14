package io.bitrise.trace.data.collector.network.urlconnection;

import androidx.annotation.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Custom implementation of the {@link HttpsURLConnection}.
 */
public class TraceHttpsURLConnection extends HttpsURLConnection {

  @VisibleForTesting
  Response response;
  @VisibleForTesting
  OkHttpClient okHttpClient;
  private URL url;
  private Proxy proxy;

  /**
   * Constructor for class.
   *
   * @param url the url of the connection.
   */
  protected TraceHttpsURLConnection(URL url) {
    this(url, null);
  }

  /**
   * Constructor for class.
   *
   * @param url   the url of the connection.
   * @param proxy the proxy for the connection.
   */
  public TraceHttpsURLConnection(URL url, Proxy proxy) {
    super(url);
    this.url = url;
    this.proxy = proxy;

    // OkHttp became the engine that powers HttpUrlConnection as of Android 4.4 (sdk 20)
    okHttpClient =
        TraceOkHttpClient.getOkHttpClient(proxy, getConnectTimeout(), getInstanceFollowRedirects());
  }

  @Override
  public void setInstanceFollowRedirects(boolean followRedirects) {
    super.setInstanceFollowRedirects(followRedirects);
    okHttpClient =
        TraceOkHttpClient.getOkHttpClient(proxy, getConnectTimeout(), getInstanceFollowRedirects());
  }

  @Override
  public String getCipherSuite() {
    return response.handshake() != null ?
        response.handshake().cipherSuite().toString() : null;
  }

  @Override
  public Certificate[] getLocalCertificates() {
    return response.handshake() != null ?
        response.handshake().localCertificates().toArray(new Certificate[0]) : null;
  }

  @Override
  public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
    return response.handshake() != null ?
        response.handshake().peerCertificates().toArray(new Certificate[0]) : null;
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
