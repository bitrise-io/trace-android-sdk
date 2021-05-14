package io.bitrise.trace.data.collector.network;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TraceNetworkListener} class.
 */
public class TraceNetworkListenerTest {

  private static final Request DUMMY_REQUEST =
      new Request.Builder().url("https://bitrise.io").build();
  private static final Response DUMMY_RESPONSE = new Response.Builder().request(DUMMY_REQUEST)
                                                                       .protocol(Protocol.HTTP_1_0)
                                                                       .code(200)
                                                                       .message("Dummy message")
                                                                       .build();
  private static final long DUMMY_START_TIME = 12345678;
  private static final long DUMMY_END_TIME = 23456789;

  private final TraceNetworkListener traceNetworkListener = TraceNetworkListener.getInstance();
  private OkHttpCallProcessor mockOkHttpCallProcessor;

  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUp() {
    TraceNetworkListener.reset();
    mockOkHttpCallProcessor = mock(OkHttpCallProcessor.class);
  }

  /**
   * Check that a call to {@link  TraceNetworkListener#getInstance()} should return the same
   * instance.
   */
  @Test
  public void getInstance_shouldBeSingleton() {
    assertEquals(TraceNetworkListener.getInstance(), TraceNetworkListener.getInstance());
  }

  /**
   * By default no {@link OkHttpCallProcessor} should be registered in the
   * {@link TraceNetworkListener}.
   */
  @Test
  public void registerOkHttpCallProcessor_ShouldNotBeRegistered() {
    traceNetworkListener
        .processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE, DUMMY_START_TIME, DUMMY_END_TIME);
    verify(mockOkHttpCallProcessor, times(0)).processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE,
        DUMMY_START_TIME, DUMMY_END_TIME);
  }

  /**
   * After registering a {@link OkHttpCallProcessor} it's
   * {@link OkHttpCallProcessor#processOkHttpCall(Request, Response, long, long)} should be
   * called when the {@link TraceNetworkListener} receives a network call.
   */
  @Test
  public void registerOkHttpCallProcessor_ShouldGetCalls() {
    traceNetworkListener.registerOkHttpCallProcessor(mockOkHttpCallProcessor);
    traceNetworkListener
        .processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE, DUMMY_START_TIME, DUMMY_END_TIME);
    verify(mockOkHttpCallProcessor, times(1)).processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE,
        DUMMY_START_TIME, DUMMY_END_TIME);
  }

  /**
   * After unregistering a {@link OkHttpCallProcessor} it's
   * {@link OkHttpCallProcessor#processOkHttpCall(Request, Response, long, long)} should not be
   * called when the {@link TraceNetworkListener} receives a network call.
   */
  @Test
  public void unregisterOkHttpCallProcessor_ShouldNotGetCalls() {
    traceNetworkListener.registerOkHttpCallProcessor(mockOkHttpCallProcessor);
    traceNetworkListener.unregisterOkHttpCallProcessor(mockOkHttpCallProcessor);
    traceNetworkListener
        .processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE, DUMMY_START_TIME, DUMMY_END_TIME);
    verify(mockOkHttpCallProcessor, times(0)).processOkHttpCall(DUMMY_REQUEST, DUMMY_RESPONSE,
        DUMMY_START_TIME, DUMMY_END_TIME);
  }
}
