package io.bitrise.trace.data.collector.network.urlconnection;

import io.bitrise.trace.utils.log.TraceLog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Custom implementation of {@link URLStreamHandlerFactory} so that we can override http and https
 * connections with our {@link TraceURLStreamHandler}.
 */
@SuppressWarnings("checkstyle:abbreviation")
public class TraceURLStreamHandlerFactory implements URLStreamHandlerFactory {

  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) {
    URLStreamHandler originalStreamHandler = getOriginalStreamHandler(protocol);

    // intercept only http and https connections
    if (protocol.equals("http") || protocol.equals("https")) {
      return new TraceURLStreamHandler();
    }

    return originalStreamHandler;
  }

  /**
   * Returns the original stream handler for the given protocol.
   *
   * @param protocol - the protocol for the url requested e.g. http.
   * @return the original stream handler or null if one cannot be found.
   */
  private URLStreamHandler getOriginalStreamHandler(String protocol) {
    try {
      Method method = URL.class.getDeclaredMethod("getURLStreamHandler", String.class);
      method.setAccessible(true);
      Object object = method.invoke(null, protocol);
      return (URLStreamHandler) object;
    } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      TraceLog.w(e);
      return null;
    }
  }
}
