package io.bitrise.trace.data.collector.network.okhttp;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.network.OkHttpCallProcessor;
import io.bitrise.trace.data.trace.ApplicationTraceManager;
import io.bitrise.trace.session.ApplicationSessionManager;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Instrumented tests for {@link OkHttpCallProcessor}.
 */
public class OkHttpDataListenerInstrumentedTest {

    private static final String DUMMY_URL = "https://bitrise.io";
    private static final String DUMMY_VALUE = "dummy";
    private static final Request DUMMY_REQUEST =
            new Request.Builder().url(DUMMY_URL).addHeader(DUMMY_VALUE, DUMMY_VALUE).build();
    private static final Response DUMMY_RESPONSE =
            new Response.Builder().request(DUMMY_REQUEST)
                                  .protocol(Protocol.HTTP_1_0)
                                  .code(200)
                                  .message(DUMMY_VALUE)
                                  .build();
    private static final long DUMMY_START_TIME = 12345678;
    private static final long DUMMY_END_TIME = 23456789;

    /**
     * Checks that {@link OkHttpDataListener#processOkHttpCall(Request, Response, long, long)} should call
     * {@link OkHttpDataListener#onDataCollected(Data)}.
     */
    @Test
    public void processOkHttpCall_ShouldCallOnDataCollected() {
        final OkHttpDataListener okHttpDataListener = mock(OkHttpDataListener.class);
        when(okHttpDataListener.createData(any())).thenReturn(mock(Data.class));
        doCallRealMethod().when(okHttpDataListener).processOkHttpCall(any(), any(), anyLong(), anyLong());
        doCallRealMethod().when(okHttpDataListener).setTraceManager(any());

        ApplicationSessionManager.getInstance().startSession();
        final ApplicationTraceManager mockApplicationTraceManager = mock(ApplicationTraceManager.class,
                Mockito.CALLS_REAL_METHODS);
        mockApplicationTraceManager.createSpanId(true);
        okHttpDataListener.setTraceManager(mockApplicationTraceManager);

        okHttpDataListener.processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE, DUMMY_START_TIME, DUMMY_END_TIME);
        verify(okHttpDataListener, times(1)).onDataCollected(ArgumentMatchers.any(Data.class));
    }
}