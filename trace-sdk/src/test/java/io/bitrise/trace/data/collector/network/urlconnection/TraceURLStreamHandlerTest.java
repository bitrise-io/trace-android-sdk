package io.bitrise.trace.data.collector.network.urlconnection;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link TraceURLStreamHandler}.
 */
public class TraceURLStreamHandlerTest {

    @Test
    public void openConnection() throws IOException {
        final TraceURLStreamHandler traceURLStreamHandler = Mockito
                .mock(TraceURLStreamHandler.class, Mockito.CALLS_REAL_METHODS );
        final URLConnection connection = traceURLStreamHandler
                .openConnection(new URL("http://example.com"));
        assertTrue(connection instanceof TraceHttpURLConnection);
    }

    @Test
    public void openConnectionWithProxy() throws IOException {
        final TraceURLStreamHandler traceURLStreamHandler = Mockito
                .mock(TraceURLStreamHandler.class, Mockito.CALLS_REAL_METHODS);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 3128));
        final URLConnection connection = traceURLStreamHandler
                .openConnection(new URL("http://example.com"), proxy);
        assertTrue(connection instanceof TraceHttpURLConnection);
    }

}