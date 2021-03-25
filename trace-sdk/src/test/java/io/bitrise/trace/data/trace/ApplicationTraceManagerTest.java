package io.bitrise.trace.data.trace;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.view.ApplicationForegroundStateDataListener;
import io.bitrise.trace.data.management.formatter.view.FragmentStateDataFormatter;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.TraceTestProvider;
import io.bitrise.trace.utils.ByteStringConverter;
import io.opencensus.proto.trace.v1.Span;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link ApplicationTraceManager}.
 */
public class ApplicationTraceManagerTest {

    private static TraceManager traceManager;
    private Activity mockActivity = mock(Activity.class);
    private static Application mockApplication;
    private static ApplicationTraceManager mockApplicationTraceManager;
    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        mockApplication = mock(Application.class);
        traceManager = ApplicationTraceManager.getInstance(mockApplication);
        traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(mockApplication);
        ApplicationSessionManager.getInstance().startSession();
    }

    /**
     * Tears down the required objects after all the tests run.
     */
    @AfterClass
    public static void tearDownClass() {
        ApplicationSessionManager.getInstance().stopSession();
    }

    /**
     * As {@link TraceActivityLifecycleTracker} is a singleton, if there is any active activity from previous runs,
     * it will stop them.
     */
    @Before
    public void setUpBefore() {
        TraceActivityLifecycleTracker.reset();
        final ApplicationForegroundStateDataListener applicationForegroundStateDataListener =
                new ApplicationForegroundStateDataListener(mockApplication);
        applicationForegroundStateDataListener.startCollecting();
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(applicationForegroundStateDataListener);
        mockApplicationTraceManager = mock(ApplicationTraceManager.class, Mockito.CALLS_REAL_METHODS);
    }

    /**
     * When {@link Trace} is stopped the active Trace should be {@code null}.
     */
    @Test
    public void stopTrace_ActiveTraceShouldBeNull() {
        mockApplicationTraceManager.stopTrace();
        assertThat(mockApplicationTraceManager.getActiveTrace(), is(nullValue()));
    }

    /**
     * When {@link Trace} is started the active Trace should not be {@code null}.
     */
    @Test
    public void startTrace_ActiveTraceShouldNotBeNull() {
        mockApplicationTraceManager.startTrace();
        assertThat(mockApplicationTraceManager.getActiveTrace(), is(notNullValue()));
    }

    /**
     * When a {@link Span} is added to the active {@link Trace} we should be able to get it back from the Trace.
     */
    @Test
    public void addSpanToActiveTrace_ShouldContainIt() {
        mockApplicationTraceManager.startTrace();
        final Trace activeTrace = mockApplicationTraceManager.getActiveTrace();
        final Span sampleSpan = TraceTestProvider.getSampleSpan(activeTrace.getTraceId(), "dummySpanName");
        mockApplicationTraceManager.addSpanToActiveTrace(sampleSpan);
        final Span expectedSpan = sampleSpan.toBuilder()
                .setTraceId(ByteStringConverter.toByteString(
                        mockApplicationTraceManager.getActiveTrace().getTraceId()))
                .build();
        assertEquals(expectedSpan, mockApplicationTraceManager.getActiveTrace().getSpanList().get(0));
    }

    /**
     * When a {@link Span} is added to the active {@link Trace}, but there is no currently active Trace, it should
     * create a new Trace and the Trace ID of the Span should be updated.
     */
    @Test
    public void addSpanToActiveTrace_ShouldChangeTraceId() {
        final Span sampleSpan = FragmentStateDataFormatter.createFragmentViewSpan("name", 10L, 20L,
                "span-id", "parent-span-id");

        assertEquals("", ByteStringConverter.toString(sampleSpan.getTraceId()));
        mockApplicationTraceManager.addSpanToActiveTrace(sampleSpan);

        final Span expectedSpan = sampleSpan.toBuilder()
                .setTraceId(ByteStringConverter.toByteString(
                        mockApplicationTraceManager.getActiveTrace().getTraceId()))
                .build();
        assertEquals(expectedSpan, mockApplicationTraceManager.getActiveTrace().getSpanList().get(0));
    }

    /**
     * Each time a {@link Trace} is started it should have a different ID.
     */
    @Test
    public void getActiveTrace_ShouldChangeWithStart() {
        mockApplicationTraceManager.startTrace();
        final String firstTraceID = mockApplicationTraceManager.getActiveTrace().getTraceId();
        mockApplicationTraceManager.stopTrace();
        mockApplicationTraceManager.startTrace();
        final String secondTraceID = mockApplicationTraceManager.getActiveTrace().getTraceId();
        assertThat(firstTraceID, not(secondTraceID));
    }

    /**
     * When {@link TraceManager#stopTrace()} is not called explicitly during two {@link TraceManager#startTrace()},
     * startTrace method should call it.
     */
    @Test
    public void getActiveTrace_ShouldChangeWithoutStop() {
        mockApplicationTraceManager.startTrace();
        final String firstTraceID = mockApplicationTraceManager.getActiveTrace().getTraceId();
        mockApplicationTraceManager.startTrace();
        final String secondTraceID = mockApplicationTraceManager.getActiveTrace().getTraceId();
        assertThat(firstTraceID, not(secondTraceID));
    }

    /**
     * When no {@link Span}s were created for the currently active {@link Trace}, the root Span ID should be {@code
     * null}.
     */
    @Test
    public void getRootSpanId_ShouldReturnNullWithNoSpans() {
        assertThat(mockApplicationTraceManager.getRootSpanId(), is(nullValue()));
    }

    /**
     * When no root {@link Span} was created for the currently active {@link Trace}, the root Span ID should be {@code
     * null}.
     */
    @Test
    public void getRootSpanId_ShouldReturnNullWithNoRootSpan() {
        mockApplicationTraceManager.createSpanId(false);
        assertThat(mockApplicationTraceManager.getRootSpanId(), is(nullValue()));
    }

    /**
     * When a root {@link Span}s was created for the currently active {@link Trace}, the root Span ID should be
     * not {@code null}.
     */
    @Test
    public void getRootSpanId_ShouldReturnNonNull() {
        mockApplicationTraceManager.createSpanId(true);
        assertThat(mockApplicationTraceManager.getRootSpanId(), is(notNullValue()));
    }

    /**
     * When a a new {@link Trace} is started it should reset the root {@link Span} ID.
     */
    @Test
    public void getRootSpanId_ShouldResetWithNewTrace() {
        mockApplicationTraceManager.dataStorage = mock(TraceDataStorage.class);
        mockApplicationTraceManager.startTrace();
        mockApplicationTraceManager.createSpanId(true);
        mockApplicationTraceManager.startTrace();
        assertThat(mockApplicationTraceManager.getRootSpanId(), is(nullValue()));
    }

    /**
     * When the lifecycle of an activity changes (background/foreground) the ID of the currently active {@link Trace}
     * should change.
     */
    @Test
    public void getActiveTrace_ShouldChangeWithActivityLifecycleChanges() {
        traceManager.startTrace();
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        final String foregroundTraceID = traceManager.getActiveTrace().getTraceId();

        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        final String backgroundTraceID = traceManager.getActiveTrace().getTraceId();

        assertThat(foregroundTraceID, not(backgroundTraceID));
    }

    /**
     * Check that the {@link ApplicationTraceManager#getInstance(Context)} returns a non null object.
     */
    @Test
    public void getInstance_ShouldReturnNonNull() {
        assertNotNull(ApplicationTraceManager.getInstance(mockApplication));
    }

    /**
     * Check that the {@link ApplicationTraceManager#getInstance(Context)} returns always the same object.
     */
    @Test
    public void getInstance_ShouldBeSingleton() {
        assertEquals(ApplicationTraceManager.getInstance(mockApplication),
                ApplicationTraceManager.getInstance(mockApplication));
    }

    /**
     * Check that the {@link ApplicationTraceManager#reset()} resets the {@link TraceManager} and call
     * {@link ApplicationTraceManager#getInstance(Context)} will return a different object on next call.
     */
    @Test
    public void reset_ShouldChangeTraceManager() {
        traceManager = ApplicationTraceManager.getInstance(mockApplication);
        ApplicationTraceManager.reset();
        assertNotEquals(traceManager, ApplicationTraceManager.getInstance(mockApplication));
    }
}