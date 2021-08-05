package io.bitrise.trace.data.trace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.TraceTestProvider;
import io.bitrise.trace.utils.TraceClock;
import io.opencensus.proto.trace.v1.Span;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link Trace}.
 */
public class TraceTest {

  final Span activitySpan = TraceTestProvider.createActivityViewSpan();
  final Span networkSpan = TraceTestProvider.createNetworkSpan();

  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
  }

  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
  }

  @Test
  public void getLastActiveViewSpan_noSpans() {
    final Trace trace = new Trace("trace id", new ArrayList<>());
    assertNull(trace.getLastActiveViewSpan());
  }

  @Test
  public void getLastActiveViewSpan_oneSpan_activitySpan() {
    final List<Span> spans = new ArrayList<>();
    spans.add(activitySpan);
    final Trace trace = new Trace("trace id", spans);
    assertEquals(activitySpan, trace.getLastActiveViewSpan());
  }

  @Test
  public void getLastActiveViewSpan_oneSpan_networkSpan() {
    final List<Span> spans = new ArrayList<>();
    spans.add(networkSpan);
    final Trace trace = new Trace("trace id", spans);
    assertNull( trace.getLastActiveViewSpan());
  }

  @Test
  public void getLastActiveViewSpan_twoSpans_oneActivity() {
    final List<Span> spans = new ArrayList<>();
    spans.add(networkSpan);
    spans.add(activitySpan);
    final Trace trace = new Trace("trace id", spans);
    assertEquals(activitySpan, trace.getLastActiveViewSpan());
  }

  @Test
  public void getLastActiveViewSpan_twoSpans_twoActivities() {

    final Span activitySpan1 = TraceTestProvider
        .createActivityViewSpan()
        .toBuilder()
        .setStartTime(TraceClock.createTimestamp(1628175971214L))
        .build();

    final Span activitySpan2 = TraceTestProvider
        .createActivityViewSpan()
        .toBuilder()
        .setStartTime(TraceClock.createTimestamp(1728175971214L))
        .build();

    final List<Span> spans = new ArrayList<>();
    spans.add(activitySpan1);
    spans.add(activitySpan2);
    final Trace trace = new Trace("trace id", spans);
    assertEquals(activitySpan2, trace.getLastActiveViewSpan());
  }

  @Test
  public void getLastActiveViewSpan_twoSpans_noActivitySpan() {
    final List<Span> spans = new ArrayList<>();
    spans.add(networkSpan);
    spans.add(networkSpan);
    final Trace trace = new Trace("trace id", spans);
    assertNull(trace.getLastActiveViewSpan());
  }
}