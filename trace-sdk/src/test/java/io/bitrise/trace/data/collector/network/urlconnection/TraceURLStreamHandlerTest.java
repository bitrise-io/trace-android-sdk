package io.bitrise.trace.data.collector.network.urlconnection;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link TraceURLStreamHandler}.
 */
@SuppressWarnings("checkstyle:abbreviation")
public class TraceURLStreamHandlerTest {
    private final TraceURLStreamHandler traceURLStreamHandler = Mockito
            .mock(TraceURLStreamHandler.class, Mockito.CALLS_REAL_METHODS );
    private final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 3128));
    private final String httpUrl = "http://example.com";
    private final String httpsUrl = "https://example.com";
    @Test
    public void openConnection() throws IOException {
        final URLConnection connection = traceURLStreamHandler
                .openConnection(new URL(httpUrl));
    assertTrue(connection instanceof TraceHttpURLConnection);
  }

  @Test
    public void openConnection_https() throws IOException {
        final URLConnection connection = traceURLStreamHandler
                .openConnection(new URL(httpsUrl));
        assertTrue(connection instanceof TraceHttpsURLConnection);
    }

    @Test
  public void openConnectionWithProxy() throws IOException {
    final URLConnection connection = traceURLStreamHandler
                .openConnection(new URL(httpUrl), proxy);
        assertTrue(connection instanceof TraceHttpURLConnection);
    }

    @Test
    public void openConnectionWithProxy_https() throws IOException {
    final URLConnection connection = traceURLStreamHandler
        .openConnection(new URL(httpsUrl), proxy);
    assertTrue(connection instanceof TraceHttpsURLConnection);
  }
}