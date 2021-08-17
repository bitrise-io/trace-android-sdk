package io.bitrise.trace.data.storage;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.storage.converters.CrashRequestConverter;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.test.DataTestUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link CrashRequestConverter}.
 */
public class CrashRequestConverterTest {

  private static final String JSON_CRASH_REPORT = "{\"crash\":[{\"threadId\":1," +
      "\"isRequesting\":true,\"frames\":[{\"package\":\"class1\",\"function\":\"method1\"," +
      "\"filename\":\"file1\",\"lineno\":1,\"sequencenumber\":0},{\"package\":\"class2\"," +
      "\"function\":\"method2\",\"filename\":\"file2\",\"lineno\":2,\"sequencenumber\":1}," +
      "{\"package\":\"class3\",\"function\":\"method3\",\"filename\":\"file3\",\"lineno\":3," +
      "\"sequencenumber\":2}]},{\"threadId\":12345,\"isRequesting\":false," +
      "\"frames\":[{\"package\":\"class1\",\"function\":\"method1\",\"filename\":\"file1\"," +
      "\"lineno\":1,\"sequencenumber\":0},{\"package\":\"class2\",\"function\":\"method2\"," +
      "\"filename\":\"file2\",\"lineno\":2,\"sequencenumber\":1},{\"package\":\"class3\"," +
      "\"function\":\"method3\",\"filename\":\"file3\",\"lineno\":3,\"sequencenumber\":2}]}]," +
      "\"metadata\":{\"throwableClassName\":\"throwable class name\"," +
      "\"description\":\"description\",\"timestamp\":\"2021-08-12T15:30:19+01:00\"," +
      "\"uuid\":\"uuid\",\"traceId\":\"trace-id\",\"spanId\":\"span-id\"}," +
      "\"resource\":{\"labels\":{\"app.build\":\"21\",\"app.platform\":\"android\",\"app.session" +
      ".id\":\"session-id\",\"app.version\":\"2.1.0\",\"device.carrier\":\"Telenor Hu\",\"device" +
      ".id\":\"01DJ5NXYX23M9SVKJ2WQM4PQ7X\",\"device.locale\":\"en_US\",\"device" +
      ".network\":\"WIFI\",\"device.rooted\":\"false\",\"device.type\":\"sdk_gphone_x86\",\"os" +
      ".version\":\"30\"},\"type\":\"mobile\"}}";
  final CrashRequest crashRequest = createCrashRequest("uuid", 1628778619944L);

  private CrashRequest createCrashRequest(@NonNull final String uuid,
                                          final long timestamp) {
    return new CrashRequest(
        DataTestUtils.getSampleResource("session-id"),
        createCrashReport(),
        timestamp,
        uuid,
        "trace-id",
        "span-id"
    );
  }
  public static CrashReport createCrashReport() {
    final List<CrashReport.Thread> threads = new ArrayList<>();
    threads.add(new CrashReport.Thread(1L, true, createStackTraceFrames()));
    threads.add(new CrashReport.Thread(12345L, false, createStackTraceFrames()));

    return new CrashReport(threads, "throwable class name", "description",
        null);
  }
  static List<CrashReport.Frame> createStackTraceFrames() {
    final List<CrashReport.Frame> frames = new ArrayList<>();
    frames.add(new CrashReport.Frame("class1", "method1", "file1", 1, 0));
    frames.add(new CrashReport.Frame("class2", "method2", "file2", 2, 1));
    frames.add(new CrashReport.Frame("class3", "method3", "file3", 3, 2));
    return frames;
  }

  @Test
  public void toCrashRequest() {
    assertEquals(crashRequest, CrashRequestConverter.toCrashRequest(JSON_CRASH_REPORT));
  }

  @Test
  public void convertToString() {
    assertEquals(JSON_CRASH_REPORT, CrashRequestConverter.toString(crashRequest));
  }
}
