package io.bitrise.trace.data.collector.network.urlconnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;

import io.bitrise.trace.data.collector.network.TraceNetworkListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TraceHttpsURLConnection}.
 */
public class TraceHttpsURLConnectionTest {

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
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.disconnect();
        assertNull(traceHttpsURLConnection.okHttpClient);
    }

    @Test
    public void connectAfterDisconnect() throws IOException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.disconnect();
        traceHttpsURLConnection.connect();
        assertNull(traceHttpsURLConnection.okHttpClient);
    }

    @Test
    public void connect() throws IOException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        final OkHttpClient mockOkHttpClient = Mockito.mock(OkHttpClient.class);
        traceHttpsURLConnection.okHttpClient = mockOkHttpClient;

        final Call mockCall = Mockito.mock(Call.class);
        final Response response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        when(mockOkHttpClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);
        traceHttpsURLConnection.connect();

        final ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
        verify(mockOkHttpClient).newCall(argument.capture());
        assertEquals(argument.getValue().toString(), response.request().toString());
        verify(mockOkHttpClient).newCall(any());
    }

    @Test
    public void getProxy_isSet() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl, dummyProxy);
        assertTrue(traceHttpsURLConnection.usingProxy());
    }

    @Test
    public void getProxy_isNotSet() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        assertFalse(traceHttpsURLConnection.usingProxy());
    }

    @Test
    public void getInputStream_validResponse() throws IOException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        final InputStream inputStream = traceHttpsURLConnection.getInputStream();
        final String result =  TraceHttpURLConnectionTestUtils.inputStreamToString(inputStream);
        assertEquals("{'hello':'world'}", result);
    }

    @Test
    public void getInputStream_nullResponse() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = null;
        try {
            traceHttpsURLConnection.getInputStream();
            fail("should not be able to get input stream");
        } catch (IOException e) {
            assertEquals("must call .connect() on HttpURLConnection.", e.getMessage());
        }
    }

    @Test
    public void getInputStream_nullResponseBody() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getResponseWithNullBody(dummyUrl);
        try {
            traceHttpsURLConnection.getInputStream();
            fail("should not be able to get input stream");
        } catch (IOException e) {
            assertNotNull(e);
            assertEquals("response.body() was null.", e.getMessage());
        }
    }

    @Test
    public void getResponseCode_validResponse() throws IOException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        int responseCode = traceHttpsURLConnection.getResponseCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void getResponseCode_nullResponse() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = null;
        try {
            traceHttpsURLConnection.getResponseCode();
            fail("should not be able to get response code.");
        } catch (IOException e) {
            assertNotNull(e);
            assertEquals("must call .connect() on HttpURLConnection.", e.getMessage());
        }
    }

    @Test
    public void getCipherSuite_validCipherSuite() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        final String cipherSuite = traceHttpsURLConnection.getCipherSuite();
        assertEquals("TLS_AES_128_CCM_8_SHA256", cipherSuite);
    }

    @Test
    public void getCipherSuite_nullHandshake() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getResponseWithNullHandshake(dummyUrl);
        final String cipherSuite = traceHttpsURLConnection.getCipherSuite();
        assertNull(cipherSuite);
    }

    @Test
    public void getLocalCertificates_validLocalCertificates() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        final Certificate[] certificates = traceHttpsURLConnection.getLocalCertificates();
        assertEquals(1, certificates.length);
    }

    @Test
    public void getLocalCertificates_nullHandshake() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getResponseWithNullHandshake(dummyUrl);
        final Certificate[] certificates = traceHttpsURLConnection.getLocalCertificates();
        assertNull(certificates);
    }

    @Test
    public void getServerCertificates_validServerCertificates() throws SSLPeerUnverifiedException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getValidResponse(dummyUrl);
        final Certificate[] certificates = traceHttpsURLConnection.getServerCertificates();
        assertEquals(1, certificates.length);
    }

    @Test
    public void getServerCertificates_nullHandshake() throws SSLPeerUnverifiedException {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.response = TraceHttpURLConnectionTestUtils.getResponseWithNullHandshake(dummyUrl);
        final Certificate[] certificates = traceHttpsURLConnection.getServerCertificates();
        assertNull(certificates);
    }

    @Test
    public void setInstanceFollowRedirects() {
        final TraceHttpsURLConnection traceHttpsURLConnection = new TraceHttpsURLConnection(dummyUrl);
        traceHttpsURLConnection.setInstanceFollowRedirects(true);
        assertTrue(traceHttpsURLConnection.getInstanceFollowRedirects());
    }
}