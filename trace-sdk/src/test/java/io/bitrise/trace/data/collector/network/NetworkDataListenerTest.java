package io.bitrise.trace.data.collector.network;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.trace.TraceManager;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link NetworkDataListener}.
 */
public class NetworkDataListenerTest {

  private static final String DUMMY_SPAN_ID = "0123456789ABCDEF";
  private final TraceManager mockTraceManager = mock(TraceManager.class);
  private final NetworkDataListener mockNetworkDataListener = mock(NetworkDataListener.class,
      Mockito.CALLS_REAL_METHODS);

  /**
   * When the {@link TraceManager} in the {@link NetworkDataListener} returns {@code null} for
   * the parent Span ID, method {@link NetworkDataListener#getRootSpanId()} should return an
   * empty String.
   */
  @Test
  public void getRootSpanId_ShouldReturnEmpty() {
    when(mockTraceManager.getRootSpanId()).thenReturn(null);
    mockNetworkDataListener.traceManager = mockTraceManager;
    assertThat(mockNetworkDataListener.getRootSpanId(), is(""));
  }

  /**
   * When the {@link TraceManager} in the {@link NetworkDataListener} returns a non null value
   * for the parent Span ID, method {@link NetworkDataListener#getRootSpanId()} should return
   * that ID.
   */
  @Test
  public void getRootSpanId_ShouldReturnId() {
    when(mockTraceManager.getRootSpanId()).thenReturn(DUMMY_SPAN_ID);
    mockNetworkDataListener.traceManager = mockTraceManager;
    assertThat(mockNetworkDataListener.getRootSpanId(), is(DUMMY_SPAN_ID));
  }
}