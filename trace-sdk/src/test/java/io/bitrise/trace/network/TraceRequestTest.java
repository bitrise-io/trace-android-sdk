package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.internal.TestUtils;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.TraceTestProvider;
import io.opencensus.proto.trace.v1.Span;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Test cases for {@link TraceRequest}.
 */
public class TraceRequestTest {

  /**
   * Create a TraceRequest with as close to real data as possible and ensure it matches
   * the expected json request.
   *
   * @throws IOException if there is an issue with expected json file.
   */
  @Test
  public void traceRequestConstructor() throws IOException {

    // given some spans
    final List<Span> spanList = new ArrayList<>();
    spanList.add(TraceTestProvider.createActivityViewSpan());
    spanList.add(TraceTestProvider.createNetworkSpan());

    // when I get a trace request using the NetworkClient gson
    final TraceRequest traceRequest = new TraceRequest(
        DataTestUtils.getSampleResource("session-id"), spanList);
    final String requestJson = NetworkClient.getGson().toJson(traceRequest);

    // it should match the expected json
    final String filePath = "src/test/resources/io/bitrise/trace/network/trace_request.json";
    final String expectedJson = TestUtils.getJsonContentRemovingWhitespace(filePath);

    assertEquals(expectedJson, requestJson);
  }

}
