package io.bitrise.trace.data.collector.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import io.bitrise.trace.InstrumentedTestRequirements;
import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.session.ApplicationSessionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumented tests for the {@link FragmentStateDataListener}.
 */
public class FragmentStateDataListenerInstrumentedTest {

  private static final Activity mockActivity = mock(Activity.class);
  private static final Activity mockActivity2 = mock(Activity.class);
  private static final android.app.FragmentManager mockFragmentManager2 =
      mock(android.app.FragmentManager.class);
  private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
  private static FragmentStateDataListener fragmentStateDataListener;
  private static ActivityStateDataListener activityStateDataListener;
  private static Application mockApplication;
  private final android.app.Fragment mockFragment2 = mock(android.app.Fragment.class);
  private final View mockView = mock(View.class);

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
    mockApplication = mock(Application.class);
    traceActivityLifecycleTracker = TraceActivityLifecycleTracker.getInstance(mockApplication);
    activityStateDataListener = new ActivityStateDataListener(mockApplication);
    when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager2);
    when(mockActivity2.getFragmentManager()).thenReturn(mockFragmentManager2);
    BaseDataCollectorInstrumentedTest.useTestDataStorage(mockApplication);
  }

  /**
   * Tears down the required objects after all the tests run.
   */
  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
  }

  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUp() {
    TraceActivityLifecycleTracker.reset();
    fragmentStateDataListener =
        new FragmentStateDataListener(mockApplication, activityStateDataListener);
    traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(fragmentStateDataListener);
    fragmentStateDataListener.startCollecting();
  }

  /**
   * When a {@link android.app.Fragment} is created, it should be in the
   * {@link FragmentStateDataListener#activityFragmentMap}.
   */
  @Test
  public void fragmentMap_ShouldContainCreatedDeprecatedFragment() {
    InstrumentedTestRequirements.assumeDeprecatedFragmentLevel();
    traceActivityLifecycleTracker.onActivityStarted(mockActivity);
    fragmentStateDataListener.getDeprecatedFragmentCallbackTracker()
                             .onFragmentViewCreated(mockFragmentManager2,
                                 mockFragment2, mockView, null);
    assertThat(fragmentStateDataListener.activityFragmentMap.containsKey(mockActivity.hashCode()),
        is(true));
  }

  /**
   * When a {@link android.app.Fragment} is paused, it should be in the
   * {@link FragmentStateDataListener#activityFragmentMap}.
   */
  @Test
  public void fragmentMap_ShouldContainPausedDeprecatedFragment() {
    InstrumentedTestRequirements.assumeDeprecatedFragmentLevel();
    fragmentStateDataListener.onActivityStarted(mockActivity);
    fragmentStateDataListener.getDeprecatedFragmentCallbackTracker()
                             .onFragmentPaused(mockFragmentManager2,
                                 mockFragment2);
    assertThat(fragmentStateDataListener.activityFragmentMap.containsKey(mockActivity.hashCode()),
        is(true));
  }

  /**
   * Tests if the listener has not been set to active, when a deprecated callback method is
   * called,
   * the activityFragmentMap should not be changed.
   */
  @Test
  public void fragmentMap_deprecatedFragmentCallback_onFragmentViewCreated_notStarted() {
    InstrumentedTestRequirements.assumeDeprecatedFragmentLevel();
    FragmentStateDataListener listener =
        new FragmentStateDataListener(mockApplication, activityStateDataListener);

    listener.getDeprecatedFragmentCallbackTracker()
            .onFragmentViewCreated(mockFragmentManager2,
            mockFragment2, mockView, null);
    assertThat(listener.isActive(), is(false));
    assertThat(listener.activityFragmentMap.size(), is(equalTo(0)));
  }

  /**
   * Tests if the listener has not been set to active, when a deprecated callback method is
   * called,
   * the activityFragmentMap should not be changed.
   */
  @Test
  public void fragmentMap_deprecatedFragmentCallback_onFragmentPaused_notStarted() {
    InstrumentedTestRequirements.assumeDeprecatedFragmentLevel();
    FragmentStateDataListener listener =
        new FragmentStateDataListener(mockApplication, activityStateDataListener);

    listener.getDeprecatedFragmentCallbackTracker()
            .onFragmentPaused(mockFragmentManager2, mockFragment2);
    assertThat(listener.isActive(), is(false));
    assertThat(listener.activityFragmentMap.size(), is(equalTo(0)));
  }

}