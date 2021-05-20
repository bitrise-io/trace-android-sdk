package io.bitrise.trace.data.collector.network.urlconnection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URLStreamHandler;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link TraceURLStreamHandler}.
 */
@SuppressWarnings("checkstyle:abbreviation")
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