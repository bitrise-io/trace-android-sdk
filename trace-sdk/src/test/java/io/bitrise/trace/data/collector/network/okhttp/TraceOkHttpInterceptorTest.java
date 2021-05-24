package io.bitrise.trace.data.collector.network.okhttp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import java.io.IOException;
import okhttp3.Interceptor;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link TraceOkHttpInterceptor}.
 */
public class TraceOkHttpInterceptorTest {

  @Test
  public void intercept() throws IOException {
    final TraceOkHttpInterceptor interceptor = new TraceOkHttpInterceptor();
    final Interceptor.Chain mockChain = Mockito.mock(Interceptor.Chain.class);
    final TraceNetworkListener traceNetworkListener = Mockito.mock(TraceNetworkListener.class);
    TraceNetworkListener.setDebugTraceNetworkListener(traceNetworkListener);

    interceptor.intercept(mockChain);

    verify(mockChain, times(1)).proceed(any());
    verify(traceNetworkListener, times(1))
        .processOkHttpCall(any(), any(), anyLong(), anyLong());
  }
}
