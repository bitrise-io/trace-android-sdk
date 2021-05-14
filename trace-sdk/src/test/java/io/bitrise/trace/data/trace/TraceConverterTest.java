package io.bitrise.trace.data.trace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.TraceTestProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link TraceConverter}.
 */
public class TraceConverterTest {

  private static final String ACTIVITY_TRACE =
      "{\"trace_id\":\"4d81c1df91b2477f8e791a895167ea10\","
          + "\"span_list\":[{\"trace_id\":\"trace-id\",\"span_id\":\"span-id\","
          + "\"name\":{\"value\":\"name\"},\"start_time\":{\"seconds\":1613645681,"
          + "\"nanos\":160000000},\"end_time\":{\"seconds\":1613645691,\"nanos\":170000000},"
          + "\"kind\":2}],\"session_id\":\"01F0V3VMFR5KBS1F9CR91P11K3\"}";
  private static final String PREVIOUS_TRACE_RECORD =
      "{\"sessionId\":\"01F0V1JHFF700P023HMPV40SP7\",\"spanList\":[{\"attributeMap\":{},"
          + "\"endTimestamp\":{\"memoizedIsInitialized\":1,\"nanos_\":436000000,"
          + "\"seconds_\":1615815329,\"unknownFields\":{\"fields\":{}},\"memoizedSize\":-1,"
          + "\"memoizedHashCode\":0},\"kind\":0,\"name\":\"IndexActivity\","
          + "\"spanId\":\"77d25914-1801-4\",\"startTimestamp\":{\"memoizedIsInitialized\":1,"
          + "\"nanos_\":336000000,\"seconds_\":1615815329,\"unknownFields\":{\"fields\":{}},"
          + "\"memoizedSize\":-1,\"memoizedHashCode\":0}}],\"traceId\":\"36e817c7-8614-41\"}";

  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
  }

  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
  }

  private Trace createActivityTrace() {
    final Trace activityTrace = new Trace();
    activityTrace.addSpan(TraceTestProvider.createActivityViewSpan());
    activityTrace.setTraceId("4d81c1df91b2477f8e791a895167ea10");
    activityTrace.setSessionId("01F0V3VMFR5KBS1F9CR91P11K3");
    return activityTrace;
  }

  /**
   * This tests the previous trace object, if the user has upgraded, they may have an older style
   * object saved, we need to ensure this does not cause any issues.
   */
  @Test
  public void toTrace_previousObjectTypes() {
    final Trace actual = TraceConverter.toTrace(PREVIOUS_TRACE_RECORD);
    assertEquals(0, actual.getSpanList().size());
    assertNotNull(actual.getTraceId());
    assertEquals(ApplicationSessionManager.getInstance().getActiveSession().getUlid(),
        actual.getSessionId());
  }

  @Test
  public void toString_activityViewSpan() {
    assertEquals(ACTIVITY_TRACE, TraceConverter.toString(createActivityTrace()));
  }

  @Test
  public void toTrace_activityViewSpan() {
    assertEquals(createActivityTrace(), TraceConverter.toTrace(ACTIVITY_TRACE));
  }
}
