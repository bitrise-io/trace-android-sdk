package io.bitrise.trace.data.collector.network.urlconnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.stream.Collectors;
import okhttp3.CipherSuite;
import okhttp3.Handshake;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import org.mockito.Mockito;

/**
 * Utility methods for use in the URLConnection tests.
 */
public class TraceHttpURLConnectionTestUtils {

  private TraceHttpURLConnectionTestUtils() {
    //nop
  }

  public static String inputStreamToString(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines().collect(Collectors.joining("\n"));
  }

  private static Handshake mockHandshake() {
    final Certificate mockCertificate = Mockito.mock(Certificate.class);

    return Handshake.get(
        TlsVersion.TLS_1_0,
        CipherSuite.TLS_AES_128_CCM_8_SHA256,
        Collections.singletonList(mockCertificate),
        Collections.singletonList(mockCertificate));
  }

  /**
   * Gets a valid response.
   *
   * @param url the url
   * @return the response.
   */
  public static Response getValidResponse(URL url) {
    final Request mockRequest = new Request.Builder()
        .url(url)
        .build();
    final Response.Builder responseBuilder = new Response.Builder()
        .request(mockRequest)
        .protocol(Protocol.HTTP_2)
        .code(200)
        .message("")
        .handshake(mockHandshake())
        .body(ResponseBody.create(
            MediaType.get("application/json; charset=utf-8"),
            "{'hello':'world'}"
        ));
    return responseBuilder.build();
  }

  /**
   * Gets a response with null body.
   *
   * @param url the url.
   * @return the response.
   */
  public static Response getResponseWithNullBody(URL url) {
    final Request mockRequest = new Request.Builder()
        .url(url)
        .build();
    final Response.Builder responseBuilder = new Response.Builder()
        .request(mockRequest)
        .protocol(Protocol.HTTP_2)
        .code(200)
        .message("")
        .body(null);
    return responseBuilder.build();
  }

  /**
   * Gets a response with null handshake.
   *
   * @param url the url.
   * @return the response.
   */
  public static Response getResponseWithNullHandshake(URL url) {
    final Request mockRequest = new Request.Builder()
        .url(url)
        .build();
    final Response.Builder responseBuilder = new Response.Builder()
        .request(mockRequest)
        .protocol(Protocol.HTTP_2)
        .code(200)
        .message("")
        .handshake(null)
        .body(ResponseBody.create(
            MediaType.get("application/json; charset=utf-8"),
            "{'hello':'world'}"
        ));
    return responseBuilder.build();
  }

}
