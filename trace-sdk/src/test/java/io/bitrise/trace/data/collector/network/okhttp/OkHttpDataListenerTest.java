package io.bitrise.trace.data.collector.network.okhttp;


import android.content.Context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.NetworkData;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link OkHttpDataListener}.
 */
public class OkHttpDataListenerTest {

  private static final String DUMMY_URL = "https://bitrise.io";
  private static final MediaType DUMMY_MEDIA_TYPE = MediaType.get("text/plain");
  private static final String DUMMY_VALUE = "dummy";
  private static final Request DUMMY_REQUEST =
      new Request.Builder().url(DUMMY_URL).addHeader(DUMMY_VALUE, DUMMY_VALUE).build();
  private static final Request DUMMY_REQUEST_WITH_BODY =
      DUMMY_REQUEST.newBuilder().post(RequestBody.create(DUMMY_MEDIA_TYPE, DUMMY_VALUE)).build();
  private static final Response DUMMY_RESPONSE =
      new Response.Builder().request(DUMMY_REQUEST)
                            .protocol(Protocol.HTTP_1_0)
                            .code(200)
                            .message(DUMMY_VALUE)
                            .build();
  private static final Response DUMMY_RESPONSE_WITH_BODY =
      DUMMY_RESPONSE.newBuilder().body(ResponseBody.create(DUMMY_MEDIA_TYPE, DUMMY_VALUE)).build();
  private static final String HTTPS_SCHEME = "https";
  private static final String HOST = "bitrise.io";
  private static final long DUMMY_START_TIME = 12345678;
  private static final long DUMMY_END_TIME = 23456789;
  private static final String DUMMY_SPAN_ID = "1234567890ABCDEF";
  private static final String DUMMY_ROOT_SPAN_ID = "ABCDEF1234567890";
    final Context mockContext = Mockito.mock(Context.class);

  private OkHttpDataListener okHttpDataListener;

  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUp() {
    okHttpDataListener = mock(OkHttpDataListener.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  public void isActive_ShouldBeTrueWhenStarted() {
    okHttpDataListener.startCollecting();
    assertTrue(okHttpDataListener.isActive());
  }

  @Test
  public void isActive_ShouldBeFalseWhenStopped() {
    okHttpDataListener.startCollecting();
    okHttpDataListener.stopCollecting();
    assertFalse(okHttpDataListener.isActive());
  }

  /**
   * When there is no body for the given {@link Request}, method
   * {@link OkHttpDataListener#getRequestSize(Request)} should return the size of headers.
   */
  @Test
  public void getRequestSize_ShouldBeReturnHeaderSize() {
    final long size = DUMMY_REQUEST.headers().byteCount();
    assertThat(OkHttpDataListener.getRequestSize(DUMMY_REQUEST), is(size));
  }

  /**
   * When there is body for the given {@link Request}, method
   * {@link OkHttpDataListener#getRequestSize(Request)} should return the size of headers and the
   * body.
   *
   * @throws IOException if any I/O error occurs.
   */
  @Test
  public void getRequestSize_ShouldBeReturnBodyAndHeaderSize() throws IOException {
    final long size = DUMMY_REQUEST_WITH_BODY.body().contentLength()
        + DUMMY_REQUEST_WITH_BODY.headers().byteCount();
    assertThat(OkHttpDataListener.getRequestSize(DUMMY_REQUEST_WITH_BODY), is(size));
  }

  /**
   * When there is no body for the given {@link Response}, method
   * {@link OkHttpDataListener#getResponseSize(Response)} should return the size of headers.
   */
  @Test
  public void getResponseSize_ShouldBeReturnHeaderSize() {
    final long size = DUMMY_RESPONSE.headers().byteCount();
    assertThat(OkHttpDataListener.getResponseSize(DUMMY_RESPONSE), is(size));
  }

  /**
   * When there is body for the given {@link Response}, method
   * {@link OkHttpDataListener#getResponseSize(Response)} should return the size of headers and
   * the body.
   */
  @Test
  public void getResponseSize_ShouldBeReturnBodyAndHeaderSize() {
    final long size = DUMMY_RESPONSE_WITH_BODY.body().contentLength()
        + DUMMY_RESPONSE_WITH_BODY.headers().byteCount();
    assertThat(OkHttpDataListener.getResponseSize(DUMMY_RESPONSE_WITH_BODY), is(size));
  }

  @Test
  public void formatUrl_ShouldFormatCorrectly() {
    final HttpUrl httpUrl = new HttpUrl.Builder().scheme(HTTPS_SCHEME).host(HOST).build();
    assertThat(OkHttpDataListener.formatUrl(httpUrl), is("https://bitrise.io"));
  }

  @Test
  public void formatUrl_ShouldAddSingleSegment() {
    final HttpUrl httpUrl =
        new HttpUrl.Builder().scheme(HTTPS_SCHEME).host(HOST).addPathSegment("segment").build();
    assertThat(OkHttpDataListener.formatUrl(httpUrl), is("https://bitrise.io/segment"));
  }

  @Test
  public void formatUrl_ShouldAddMultipleSegments() {
    final HttpUrl httpUrl = new HttpUrl.Builder().scheme(HTTPS_SCHEME)
                                                 .host(HOST)
                                                 .addPathSegment("segment")
                                                 .addPathSegment("other")
                                                 .build();
    assertThat(OkHttpDataListener.formatUrl(httpUrl), is("https://bitrise.io/segment/other"));
  }

  @Test
  public void formatUrl_ShouldNotAddFragment() {
    final HttpUrl httpUrl =
        new HttpUrl.Builder().scheme(HTTPS_SCHEME).host(HOST).fragment("fragment").build();
    assertThat(OkHttpDataListener.formatUrl(httpUrl), is("https://bitrise.io"));
  }

  @Test
  public void formatUrl_ShouldNotAddQuery() {
    final HttpUrl httpUrl =
        new HttpUrl.Builder().scheme(HTTPS_SCHEME).host(HOST).query("query").build();
    assertThat(OkHttpDataListener.formatUrl(httpUrl), is("https://bitrise.io"));
  }

  @Test
  public void createNetworkData_ShouldCreateCorrectNetworkData() {
    final NetworkData actualValue =
        OkHttpDataListener.createNetworkData(DUMMY_REQUEST, DUMMY_RESPONSE,
            DUMMY_START_TIME, DUMMY_END_TIME, DUMMY_SPAN_ID, DUMMY_ROOT_SPAN_ID);
    final NetworkData expectedValue =
        new NetworkData(DUMMY_SPAN_ID, DUMMY_ROOT_SPAN_ID).setMethod(DUMMY_REQUEST.method())
                                                          .setRequestSize(
                                                              OkHttpDataListener.getRequestSize(
                                                                  DUMMY_REQUEST))
                                                          .setResponseSize(
                                                              OkHttpDataListener.getResponseSize(
                                                                  DUMMY_RESPONSE))
                                                          .setUrl(OkHttpDataListener.formatUrl(
                                                              DUMMY_REQUEST.url()))
                                                          .setStatusCode(DUMMY_RESPONSE.code())
                                                          .setStart(DUMMY_START_TIME)
                                                          .setEnd(DUMMY_END_TIME);
    assertThat(actualValue, is(expectedValue));
  }

    @Test
    public void getPermissions() {
        final OkHttpDataListener listener = new OkHttpDataListener(mockContext);
        assertArrayEquals(new String[0], listener.getPermissions());
    }

    @Test
    public void createData() {
        final OkHttpDataListener listener = new OkHttpDataListener(mockContext);
        final NetworkData networkData = new NetworkData("spanId", "parentSpanId");
        networkData.setUrl("https://www.example.com");

        final Data data = listener.createData(networkData);
        final Data expectedData = new Data(OkHttpDataListener.class);
        expectedData.setContent(networkData);

        assertEquals(expectedData, data);
    }

    @Test
    public void onDataCollected() {
        final Data data = new Data(OkHttpDataListener.class);
        final OkHttpDataListener listener = new OkHttpDataListener(mockContext);
        final DataManager mockDataManager = Mockito.mock(DataManager.class);
        listener.setDataManager(mockDataManager);

        listener.onDataCollected(data);

        verify(mockDataManager, times(1)).handleReceivedData(data);
    }
}