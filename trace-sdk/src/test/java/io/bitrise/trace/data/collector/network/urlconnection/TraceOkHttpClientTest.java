package io.bitrise.trace.data.collector.network.urlconnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import okhttp3.OkHttpClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link TraceOkHttpClient}.
 */
public class TraceOkHttpClientTest {

  private final Proxy dummyProxy =
      new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 3128));

  @AfterClass
  public static void tearDownClass() {
    TraceOkHttpClient.reset();
  }

  @Before
  public void setUp() {
    TraceOkHttpClient.reset();
  }

  @Test
  public void generateOkHttpKey_withProxy_and_withConnectionTimeout() {
    final String key = TraceOkHttpClient.generateOkHttpKey(dummyProxy, 32, true);
    assertEquals("HTTP @ /127.0.0.1:3128_32_true", key);
  }

  @Test
  public void generateOkHttpKey_nullProxy_and_withConnectionTimeout() {
    final String key = TraceOkHttpClient.generateOkHttpKey(null, 32, true);
    assertEquals("no_proxy_32_true", key);
  }

  @Test
  public void generateOkHttpKey_nullProxy_and_withConnectionTimeout_and_noFollowRedirects() {
    final String key = TraceOkHttpClient.generateOkHttpKey(null, 32, false);
    assertEquals("no_proxy_32_false", key);
  }

  @Test
  public void getOkHttpClient_newClientWithProxy() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    assertEquals(1, TraceOkHttpClient.okHttpClients.size());

    assertEquals(client.proxy(), dummyProxy);
    assertEquals(32, client.connectTimeoutMillis());
  }

  @Test
  public void getOkHttpClient_newClientWithNullProxy() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client = TraceOkHttpClient.getOkHttpClient(null, 21);
    assertEquals(1, TraceOkHttpClient.okHttpClients.size());

    assertNull(client.proxy());
    assertEquals(21, client.connectTimeoutMillis());
  }

  @Test
  public void getOkHttpClient_clientExists() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client1 = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    final OkHttpClient client2 = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    assertEquals(1, TraceOkHttpClient.okHttpClients.size());
  }

  @Test
  public void getOkHttpClient_sameProxyDifferentTimeouts() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client1 = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    final OkHttpClient client2 = TraceOkHttpClient.getOkHttpClient(dummyProxy, 64);
    assertEquals(2, TraceOkHttpClient.okHttpClients.size());
  }

  @Test
  public void getOkHttpClient_differentProxySameTimeouts() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client1 = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    final OkHttpClient client2 = TraceOkHttpClient.getOkHttpClient(null, 32);
    assertEquals(2, TraceOkHttpClient.okHttpClients.size());
  }

  @Test
    public void getOkHttpClient_differentRedirects() {
        assertEquals(0, TraceOkHttpClient.okHttpClients.size());
        final OkHttpClient client1 = TraceOkHttpClient.getOkHttpClient(null, 32, false);
        final OkHttpClient client2 = TraceOkHttpClient.getOkHttpClient(null, 32, true);
        assertEquals(2, TraceOkHttpClient.okHttpClients.size());
    }

    @Test
  public void reset() {
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
    final OkHttpClient client = TraceOkHttpClient.getOkHttpClient(dummyProxy, 32);
    assertEquals(1, TraceOkHttpClient.okHttpClients.size());
    TraceOkHttpClient.reset();
    assertEquals(0, TraceOkHttpClient.okHttpClients.size());
  }

}