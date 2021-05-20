package io.bitrise.trace.data.collector.network.urlconnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests for {@link TraceHttpURLConnection}.
 */
@SuppressWarnings("checkstyle:abbreviation")
public class TraceHttpURLConnectionTest {

  private final Proxy dummyProxy = new Proxy(Proxy.Type.HTTP,
      new InetSocketAddress("127.0.0.1", 3128));
  private URL dummyUrl;

  @Before
  public void setup() throws MalformedURLException {
    dummyUrl = new URL("http://example.com");
    TraceNetworkListener.reset();
  }

  @After
  public void tearDown() {
    TraceNetworkListener.reset();
  }

  @Test
  public void disconnect() {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.disconnect();
    assertNull(traceHttpURLConnection.okHttpClient);
  }

  @Test
  public void connectAfterDisconnect() throws IOException {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.disconnect();
    traceHttpURLConnection.connect();
    assertNull(traceHttpURLConnection.okHttpClient);
  }

  @Test
  public void connect() throws IOException {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    final OkHttpClient mockOkHttpClient = Mockito.mock(OkHttpClient.class);
    traceHttpURLConnection.okHttpClient = mockOkHttpClient;

    final Call mockCall = Mockito.mock(Call.class);
    final Response response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
    when(mockOkHttpClient.newCall(any())).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(response);
    traceHttpURLConnection.connect();

    final ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
    verify(mockOkHttpClient).newCall(argument.capture());
    assertEquals(argument.getValue().toString(), response.request().toString());
    verify(mockOkHttpClient).newCall(any());
  }

  @Test
  public void getProxy_isSet() {
    final TraceHttpURLConnection traceHttpURLConnection =
        new TraceHttpURLConnection(dummyUrl, dummyProxy);
    assertTrue(traceHttpURLConnection.usingProxy());
  }

  @Test
  public void getProxy_isNotSet() {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    assertFalse(traceHttpURLConnection.usingProxy());
  }

  @Test
  public void getInputStream_validResponse() throws IOException {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
    final InputStream inputStream = traceHttpURLConnection.getInputStream();
    final String result = TraceHttpURLConnectionTestUtils.inputStreamToString(inputStream);
    assertEquals("{'hello':'world'}", result);
  }

  @Test
  public void getInputStream_nullResponse() {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.response = null;
    try {
      traceHttpURLConnection.getInputStream();
      fail("should not be able to get input stream");
    } catch (IOException e) {
      assertEquals("must call .connect() on HttpURLConnection.", e.getMessage());
    }
  }

  @Test
  public void getInputStream_nullResponseBody() {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.response =
        TraceHttpURLConnectionTestUtils.getResponseWithNullBody(dummyUrl);
    try {
      traceHttpURLConnection.getInputStream();
      fail("should not be able to get input stream");
    } catch (IOException e) {
      assertNotNull(e);
      assertEquals("response.body() was null.", e.getMessage());
    }
  }

  @Test
  public void getResponseCode_validResponse() throws IOException {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
    int responseCode = traceHttpURLConnection.getResponseCode();
    assertEquals(200, responseCode);
  }

  @Test
  public void getResponseCode_nullResponse() {
    final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
    traceHttpURLConnection.response = null;
    try {
      traceHttpURLConnection.getResponseCode();
      fail("should not be able to get response code.");
    } catch (IOException e) {
      assertNotNull(e);
      assertEquals("must call .connect() on HttpURLConnection.", e.getMessage());
    }
  }

    @Test
    public void setInstanceFollowRedirects() {
        final TraceHttpURLConnection traceHttpURLConnection = new TraceHttpURLConnection(dummyUrl);
        traceHttpURLConnection.setInstanceFollowRedirects(true);
        assertTrue(traceHttpURLConnection.getInstanceFollowRedirects());
    }

}