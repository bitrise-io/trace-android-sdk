package io.bitrise.trace.data.collector.network.urlconnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;
import org.junit.Test;

/**
 * Tests for {@link TraceURLConnectionUtils}.
 */
public class TraceURLConnectionUtilsTest {

  @Test
  public void getHeaders_twoHeaders() {
    final Map<String, List<String>> input = new HashMap<>();
    input.put("Keep-Alive", Collections.singletonList("300"));
    input.put("Connection", Collections.singletonList("keep-alive"));
    final Headers output = TraceURLConnectionUtils.getHeaders(input);
    assertNotNull(output);
    assertEquals(2, output.size());
    assertEquals("Keep-Alive", output.name(0));
    assertEquals("300", output.get("Keep-Alive"));
    assertEquals("Connection", output.name(1));
    assertEquals("keep-alive", output.get("Connection"));
  }

  @Test
  public void getHeaders_multipleItemsForHeader() {
    final Map<String, List<String>> input = new HashMap<>();
    input.put("Accept-Language", Arrays.asList("en-us", "en", "q=0.5"));
    final Headers output = TraceURLConnectionUtils.getHeaders(input);
    assertNotNull(output);
    assertEquals(1, output.size());
    assertEquals("Accept-Language", output.name(0));
    assertEquals("en-us,en,q=0.5", output.get("Accept-Language"));
  }

  @Test
  public void getHeaders_empty() {
    final Map<String, List<String>> input = new HashMap<>();
    final Headers output = TraceURLConnectionUtils.getHeaders(input);
    assertNotNull(output);
    assertEquals(0, output.size());
  }

  @Test
  public void getHeaders_null() {
    final Headers output = TraceURLConnectionUtils.getHeaders(null);
    assertNotNull(output);
    assertEquals(0, output.size());
  }
}