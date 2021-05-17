package io.bitrise.trace.data.collector.network.okhttp;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import okhttp3.Interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TraceOkHttpInterceptor}.
 */
public class TraceOkHttpInterceptorTest {

    @Test
    public void intercept() throws IOException {
        final Interceptor.Chain mockChain = Mockito.mock(Interceptor.Chain.class);
        final TraceOkHttpInterceptor interceptor = new TraceOkHttpInterceptor();
        final TraceNetworkListener mockNetworkListener = Mockito.mock(TraceNetworkListener.class, Mockito.CALLS_REAL_METHODS);

        TraceNetworkListener.setTestInstance(mockNetworkListener);
        interceptor.intercept(mockChain);

        verify(mockChain, times(1))
                .proceed(any());
        verify(mockNetworkListener, times(1))
                .processOkHttpCall(any(), any(), anyLong(), anyLong());
    }
}
