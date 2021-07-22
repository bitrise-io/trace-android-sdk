package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.management.formatter.crash.CrashFormatterTestProvider;
import io.bitrise.trace.internal.TestUtils;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.utils.TraceClock;
import io.opencensus.proto.resource.v1.Resource;
import java.io.IOException;
import java.util.TimeZone;
import org.junit.Test;

/**
 * Test case for {@link CrashRequest}.
 */
public class CrashRequestTest {

  @Test
  public void crashRequest() throws IOException {
    // given a crash report, metadata and resources
    final CrashReport crashReport = CrashFormatterTestProvider.createCrashReport();

    final CrashRequest.Metadata metadata = new CrashRequest.Metadata(
        crashReport.getThrowableClassName(),
        crashReport.getDescription(),
        TraceClock.createCrashRequestFormat(1624544328342L, TimeZone.getTimeZone("Europe/London")),
        "d519fb6e-0edf-42de-8d90-5a59fc41b9a1",
        "6d010d9eb5624242990dac11a048dac4",
        "707ccf317d314af1",
        null
    );

    final Resource resource = DataTestUtils.getSampleResource("session-id");

    // when i make a crash request
    final CrashRequest crashRequest = new CrashRequest(resource, crashReport, metadata);
    final String requestJson = NetworkClient.getGson().toJson(crashRequest);

    // the json should match what i'm expecting
    final String filePath = "src/test/resources/io/bitrise/trace/network/crash_request.json";
    final String expectedJson = TestUtils.getJsonContentRemovingWhitespace(filePath);
    assertEquals(expectedJson, requestJson);
  }
}
