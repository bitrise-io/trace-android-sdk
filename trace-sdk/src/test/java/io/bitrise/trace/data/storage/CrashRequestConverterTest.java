package io.bitrise.trace.data.storage;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.management.formatter.crash.CrashFormatterTestProvider;
import io.bitrise.trace.data.storage.converters.CrashRequestConverter;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.test.DataTestUtils;
import java.util.TimeZone;
import org.junit.Test;

/**
 * Unit tests for {@link CrashRequestConverter}.
 */
public class CrashRequestConverterTest {

  private static final String JSON_CRASH_REPORT = "{\"crash\":[{\"threadId\":1,"
      + "\"isRequesting\":true,\"frames\":[{\"package\":\"class1\",\"function\":\"method1\","
      + "\"filename\":\"file1\",\"lineno\":1,\"sequencenumber\":0},{\"package\":\"class2\","
      + "\"function\":\"method2\",\"filename\":\"file2\",\"lineno\":2,\"sequencenumber\":1},"
      + "{\"package\":\"class3\",\"function\":\"method3\",\"filename\":\"file3\",\"lineno\":3,"
      + "\"sequencenumber\":2}]},{\"threadId\":12345,\"isRequesting\":false,"
      + "\"frames\":[{\"package\":\"class\",\"function\":\"method\",\"filename\":\"\",\"lineno\":0,"
      + "\"sequencenumber\":0}]}],\"metadata\":{\"throwableClassName\":\"throwable class name\","
      + "\"description\":\"description\",\"timestamp\":\"2021-08-12T15:30:19+01:00\","
      + "\"uuid\":\"uuid\",\"traceId\":\"trace-id\",\"spanId\":\"span-id\"},"
      + "\"resource\":{\"labels\":{\"app.build\":\"21\",\"app.platform\":\"android\",\"app.session"
      + ".id\":\"session-id\",\"app.version\":\"2.1.0\",\"device.carrier\":\"Telenor Hu\",\"device"
      + ".id\":\"01DJ5NXYX23M9SVKJ2WQM4PQ7X\",\"device.locale\":\"en_US\",\"device"
      + ".network\":\"WIFI\",\"device.rooted\":\"false\",\"device.type\":\"sdk_gphone_x86\",\"os"
      + ".version\":\"30\"},\"type\":\"mobile\"}}";

  final CrashRequest crashRequest = new CrashRequest(
      DataTestUtils.getSampleResource("session-id"),
      CrashFormatterTestProvider.createCrashReport(),
      1628778619944L,
      TimeZone.getTimeZone("Europe/London"),
      "uuid",
      "trace-id",
      "span-id"
  );

  @Test
  public void toCrashRequest() {
    assertEquals(crashRequest, CrashRequestConverter.toCrashRequest(JSON_CRASH_REPORT));
  }

  @Test
  public void convertToString() {
    assertEquals(JSON_CRASH_REPORT, CrashRequestConverter.toString(crashRequest));
  }
}
