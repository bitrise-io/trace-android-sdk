package io.bitrise.trace.data.collector.network.urlconnection;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Custom implementation of {@link URLStreamHandler} so that we can use our custom
 * {@link TraceHttpURLConnection} or {@link TraceHttpsURLConnection}.
 */
@SuppressWarnings("checkstyle:abbreviation")
public class TraceURLStreamHandler extends URLStreamHandler {

  @Override
  protected URLConnection openConnection(URL url) throws IOException {
    if (url.getProtocol().equals("https")) {
      return new TraceHttpsURLConnection(url);
    }
    return new TraceHttpURLConnection(url);
  }

  @Override
  protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
    if (url.getProtocol().equals("https")) {
      return new TraceHttpsURLConnection(url);
    }
    return new TraceHttpURLConnection(url, proxy);
  }
}
