package io.bitrise.trace.data.collector.network.urlconnection;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.URLStreamHandler;

import static org.junit.Assert.*;

/**
 * Tests for {@link TraceURLStreamHandler}.
 */
public class TraceURLStreamHandlerFactoryTest {

    private final TraceURLStreamHandlerFactory traceURLStreamHandlerFactory =
            Mockito.mock(TraceURLStreamHandlerFactory.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void createURLStreamHandler_protocolHttp() {
        final URLStreamHandler urlStreamHandler =
                traceURLStreamHandlerFactory.createURLStreamHandler("http");
        assertTrue(urlStreamHandler instanceof TraceURLStreamHandler);
    }

    @Test
    public void createURLStreamHandler_protocolHttps() {
        final URLStreamHandler urlStreamHandler =
                traceURLStreamHandlerFactory.createURLStreamHandler("https");
        assertTrue(urlStreamHandler instanceof TraceURLStreamHandler);
    }

    @Test
    public void createURLStreamHandler_protocolWebsocket() {
        final URLStreamHandler urlStreamHandler =
                traceURLStreamHandlerFactory.createURLStreamHandler("ws");
        assertFalse(urlStreamHandler instanceof TraceURLStreamHandler);
    }

}