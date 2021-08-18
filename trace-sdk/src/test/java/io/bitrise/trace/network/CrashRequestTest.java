package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.bitrise.trace.data.CrashTestDataProvider;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.internal.TestUtils;
import io.bitrise.trace.test.DataTestUtils;
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
    final CrashReport crashReport = CrashTestDataProvider.createCrashReport();

    final Resource resource = DataTestUtils.getSampleResource("session-id");

    // when i make a crash request
    final CrashRequest crashRequest = new CrashRequest(
        resource,
        crashReport,
        1624544328342L,
        TimeZone.getTimeZone("Europe/London"),
        "d519fb6e-0edf-42de-8d90-5a59fc41b9a1",
        "6d010d9eb5624242990dac11a048dac4",
        "707ccf317d314af1"
        );
    final String requestJson = NetworkClient.getGson().toJson(crashRequest);

    // the json should match what i'm expecting
    final String filePath = "src/test/resources/io/bitrise/trace/network/crash_request.json";
    final String expectedJson = TestUtils.getJsonContentRemovingWhitespace(filePath);
    assertEquals(expectedJson, requestJson);
  }

  @Test
  public void crashRequest_equals_self() {
    final CrashRequest crashRequest = CrashTestDataProvider.createCrashRequest();
    assertTrue(crashRequest.equals(crashRequest));
  }

  @Test
  public void crashRequest_equals_notCrashRequestType() {
    final CrashRequest crashRequest = CrashTestDataProvider.createCrashRequest();
    assertFalse(crashRequest.equals("potato"));
  }

  @Test
  public void crashRequest_equals_differentObjects() {
    final CrashRequest crashRequest = CrashTestDataProvider.createCrashRequest();
    final CrashRequest crashRequest2 = CrashTestDataProvider.createDifferentCrashRequest();
    assertFalse(crashRequest.equals(crashRequest2));
  }

  @Test
  public void crashRequest_equals_isEqual() {
    final CrashRequest crashRequest = CrashTestDataProvider.createCrashRequest();
    final CrashRequest crashRequest2 = CrashTestDataProvider.createCrashRequest();
    assertTrue(crashRequest.equals(crashRequest2));
  }

  @Test
  public void metadata_equals_self() {
    final CrashRequest.Metadata metadata = CrashTestDataProvider.createTestMetadata();
    assertTrue(metadata.equals(metadata));
  }

  @Test
  public void metadata_equals_notMetadataType() {
    final CrashRequest.Metadata metadata = CrashTestDataProvider.createTestMetadata();
    assertFalse(metadata.equals("potato"));
  }

  @Test
  public void metadata_equals_differentObjects() {
    final CrashRequest.Metadata metadata = CrashTestDataProvider.createTestMetadata();
    final CrashRequest.Metadata metadata2 =
        CrashTestDataProvider.createDifferentTestMetadata();
    assertFalse(metadata.equals(metadata2));
  }

  @Test
  public void metadata_equals_isEqual() {
    final CrashRequest.Metadata metadata = CrashTestDataProvider.createTestMetadata();
    final CrashRequest.Metadata metadata2 = CrashTestDataProvider.createTestMetadata();
    assertTrue(metadata.equals(metadata2));
  }
}
