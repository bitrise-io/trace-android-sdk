package io.bitrise.trace.data.collector.view;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.hamcrest.core.IsNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.data.TraceActivityLifecycleTracker;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FragmentData;
import io.bitrise.trace.data.dto.FragmentDataStateEntry;
import io.bitrise.trace.data.dto.FragmentState;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.session.ApplicationSessionManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link FragmentStateDataListener}.
 */
public class FragmentStateDataListenerTest {

    private static TraceActivityLifecycleTracker traceActivityLifecycleTracker;
    private static FragmentStateDataListener fragmentStateDataListener;
    private static ActivityStateDataListener activityStateDataListener;
    private static Application mockApplication;
    private static Activity mockActivity = mock(Activity.class);
    private static Activity mockActivity2 = mock(Activity.class);
    private Fragment mockFragment1 = mock(Fragment.class, Mockito.CALLS_REAL_METHODS);
    private android.app.Fragment mockDeprecatedFragment = mock(android.app.Fragment.class);
    private static FragmentManager mockFragmentManager1 = mock(FragmentManager.class);
    private static android.app.FragmentManager mockFragmentManager2 = mock(android.app.FragmentManager.class);
    private View mockView = mock(View.class);

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
        fragmentStateDataListener = new FragmentStateDataListener(mockApplication, activityStateDataListener);
        traceActivityLifecycleTracker.registerTraceActivityLifecycleSink(fragmentStateDataListener);
        fragmentStateDataListener.startCollecting();
    }

    /**
     * When the collection for {@link FragmentStateDataListener} is started, it should be active.
     */
    @Test
    public void isActive_ShouldBeTrueWhenStarted() {
        assertThat(fragmentStateDataListener.isActive(), is(true));
    }

    /**
     * When the collection for {@link FragmentStateDataListener} is stopped, it should not be active.
     */
    @Test
    public void isActive_ShouldBeFalseWhenStopped() {
        fragmentStateDataListener.stopCollecting();
        assertThat(fragmentStateDataListener.isActive(), is(false));
    }

    /**
     * Before any activity is started, {@link FragmentStateDataListener#activityFragmentMap} should be empty.
     */
    @Test
    public void fragmentMap_ShouldBeEmptyWithNoActivity() {
        assertThat(fragmentStateDataListener.activityFragmentMap.isEmpty(), is(true));
    }

    /**
     * After an activity is started, {@link FragmentStateDataListener#activityFragmentMap} should be still empty.
     */
    @Test
    public void fragmentMap_ShouldBeEmptyWithStartedActivity() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        assertThat(fragmentStateDataListener.activityFragmentMap.isEmpty(), is(true));
    }

    /**
     * When a {@link Fragment} is created, it should be in the {@link FragmentStateDataListener#activityFragmentMap}.
     */
    @Test
    public void fragmentMap_ShouldContainCreatedFragment() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentViewCreated(mockFragmentManager1, mockFragment1, mockView, null);
        assertThat(fragmentStateDataListener.activityFragmentMap.containsKey(mockActivity.hashCode()), is(true));
    }

    /**
     * When a {@link Fragment} is paused, it should be in the {@link FragmentStateDataListener#activityFragmentMap}.
     */
    @Test
    public void fragmentMap_ShouldContainPausedFragment() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        assertThat(fragmentStateDataListener.activityFragmentMap.containsKey(mockActivity.hashCode()), is(true));
    }

    /**
     * After activity is stopped, the {@link FragmentStateDataListener#activityFragmentMap} should be flushed, so it should
     * be empty.
     */
    @Test
    public void fragmentMap_FragmentMapShouldBeEmptyAfterActivityStop() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        assertThat(fragmentStateDataListener.activityFragmentMap.isEmpty(), is(true));
    }

    /**
     * After activity is stopped, the {@link DataManager#handleReceivedData(Data)} method should be called.
     */
    @Test
    public void onActivityStopped_ShouldCallHandleReceivedData() {
        final DataManager mockDataManager = mock(DataManager.class);
        fragmentStateDataListener.dataManager = mockDataManager;
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockDataManager, times(1)).handleReceivedData(any(Data.class));
    }

    /**
     * After activity is stopped, the {@link DataManager#handleReceivedData(Data)} method should not be called if
     * Data collection is stopped with {@link FragmentStateDataListener#stopCollecting()}.
     */
    @Test
    public void onActivityStopped_ShouldNotCallHandleReceivedDataWhenCollectingStopped() {
        fragmentStateDataListener.stopCollecting();
        final DataManager mockDataManager = mock(DataManager.class);
        fragmentStateDataListener.dataManager = mockDataManager;
        traceActivityLifecycleTracker.onActivityStopped(mockActivity);
        verify(mockDataManager, never()).handleReceivedData(any(Data.class));
    }

    /**
     * If {@link FragmentStateDataListener#stopCollecting()} is called, it should not affect the current state of the
     * {@link FragmentStateDataListener#activityFragmentMap}.
     */
    @Test
    public void stopCollecting_ShouldNotAffectActivityMap() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        fragmentStateDataListener.stopCollecting();
        assertThat(fragmentStateDataListener.activityFragmentMap.size(), is(1));
    }

    /**
     * If {@link FragmentStateDataListener#startCollecting()} is called, it should empty the
     * {@link FragmentStateDataListener#activityFragmentMap}. This is required, because during the time the collecting is
     * stopped multiple Fragment lifecycle event can occur, which could lead to invalid data.
     */
    @Test
    public void startCollecting_ShouldEmptyActivityMap() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        fragmentStateDataListener.stopCollecting();
        fragmentStateDataListener.startCollecting();
        assertThat(fragmentStateDataListener.activityFragmentMap.size(), is(0));
    }

    /**
     * Test that combines the activity lifecycle and fragment lifecycle events
     * e.g. when a second activity is created, the first activities fragments are recorded
     * correctly.
     */
    @Test
    public void collectData_reflectingActivityLifecycle() {

        // start first activity and fragment
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        fragmentStateDataListener.onFragmentViewCreated(
                mockFragmentManager1, mockFragment1, mockView, null);
        assertThat(fragmentStateDataListener.activityFragmentMap.size(), is(1));
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).size(), is(1));
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode())
                .get(mockFragment1.hashCode()).getStates().size(), is(1));

        // end first fragment
        fragmentStateDataListener.onFragmentPaused(mockFragmentManager1, mockFragment1);
        assertThat(fragmentStateDataListener.activityFragmentMap.size(), is(1));
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).size(), is(1));
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode())
                .get(mockFragment1.hashCode()).getStates().size(), is(2));

        // start second activity with no fragments -> map should not change
        fragmentStateDataListener.onActivityStarted(mockActivity2);
        assertThat(fragmentStateDataListener.activityFragmentMap.size(), is(1));
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).size(), is(1));

        // end first activity -> data should be flushed for the first activity
        fragmentStateDataListener.onActivityStopped(mockActivity);
        assertThat(fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()), is(IsNull.nullValue()));
    }

    /**
     * Test for ensuring activeActivityHashCode is accurate if if activity1 is started,
     * then activity 2 is started, and then the user presses back activity1 should now be the focus.
     */
    @Test
    public void activeActivityHashCode() {
        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        assertEquals(mockActivity.hashCode(), fragmentStateDataListener.activeActivityHashCode);

        traceActivityLifecycleTracker.onActivityStarted(mockActivity2);
        assertEquals(mockActivity2.hashCode(), fragmentStateDataListener.activeActivityHashCode);

        traceActivityLifecycleTracker.onActivityStarted(mockActivity);
        assertEquals(mockActivity.hashCode(), fragmentStateDataListener.activeActivityHashCode);
    }

    /**
     * Test for writing a typical fragment lifecycle event (VIEW_CREATED and PAUSED) and ensuring
     * the data is recorded correctly.
     */
    @Test
    public void recordFragment() {

        fragmentStateDataListener.onActivityStarted(mockActivity);

        final FragmentData fragmentData = new FragmentData("span id");
        fragmentData.setName("fragment name");
        fragmentData.addState(new FragmentDataStateEntry(FragmentState.VIEW_CREATED, 12345L));

        fragmentStateDataListener.recordFragmentData(mockFragment1.hashCode(), fragmentData);
        assertEquals(1, fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).size());
        assertEquals(1, fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).get(mockFragment1.hashCode()).getStates().size());

        final FragmentData fragmentData2 = new FragmentData("span id");
        fragmentData2.setName("fragment name");
        fragmentData2.addState(new FragmentDataStateEntry(FragmentState.PAUSED, 23456L));

        fragmentStateDataListener.recordFragmentData(mockFragment1.hashCode(), fragmentData2);
        assertEquals(1, fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).size());
        assertEquals(2, fragmentStateDataListener.activityFragmentMap
                .get(mockActivity.hashCode()).get(mockFragment1.hashCode()).getStates().size());

    }

    /**
     * Test for if we pass an invalid record (missing a state) it should not be recorded.
     */
    @Test
    public void recordFragment_invalidRecord() {
        final FragmentData fragmentData = new FragmentData("span id");
        fragmentData.setName("fragment name");
        fragmentStateDataListener.recordFragmentData(mockFragment1.hashCode(), fragmentData);
        assertEquals(0, fragmentStateDataListener.activityFragmentMap.size());
    }

    @Test
    public void createFragmentData_deprecatedFragment() {
        final FragmentData fragmentData = fragmentStateDataListener.createFragmentData(
                mockDeprecatedFragment, FragmentState.CREATED);
        assertNotNull(fragmentData);
        assertEquals(1, fragmentData.getStates().size());

        assertFragmentDataMatch(fragmentData, mockDeprecatedFragment.getClass().getSimpleName(),
                FragmentState.CREATED);
    }

    @Test
    public void createFragmentData_androidxFragment() {
        final FragmentData fragmentData = fragmentStateDataListener.createFragmentData(
                mockFragment1, FragmentState.STOPPED);

        assertNotNull(fragmentData);
        assertEquals(1, fragmentData.getStates().size());

        assertFragmentDataMatch(fragmentData, mockFragment1.getClass().getSimpleName(),
                FragmentState.STOPPED);
    }

    /**
     * Asserts that an actual FragmentData matches an expected FragmentData.
     * @param fragmentData the created FragmentData to test against (actual).
     * @param name the name the actual fragmentData should contain.
     * @param fragmentState the state the actual fragmentData should contain.
     */
    private void assertFragmentDataMatch(@NonNull final FragmentData fragmentData,
                                         @NonNull final String name,
                                         @NonNull final FragmentState fragmentState) {
        final String spanId = fragmentData.getSpanId();
        final long timestamp = fragmentData.getStates().get(0).getTimeStamp();
        final String parentTimestamp = fragmentData.getParentSpanId();

        final FragmentData expectedData = new FragmentData(spanId);
        expectedData.setName(name);
        expectedData.addState(fragmentState, timestamp);
        expectedData.setParentSpanId(parentTimestamp);

        assertEquals(expectedData, fragmentData);
    }


    @Test
    public void getParentSpanIdFromActivityId_found() {
        final int activityId = 123;
        final String spanId = "707ccf317d314af1";
        activityStateDataListener.activityMap.put(activityId, new ActivityData(spanId));

        assertEquals(fragmentStateDataListener.getParentSpanIdFromActivityId(activityId), spanId);
    }

    @Test
    public void getParentSpanIdFromActivityId_notFound() {
        final int activityId = 123;
        activityStateDataListener.activityMap.clear();

        assertNull(fragmentStateDataListener.getParentSpanIdFromActivityId(activityId));
    }

    @Test
    public void getParentSpanIdFromFragmentId_found() {
        final int fragmentId = 234;
        final String fragmentSpanId = "707ccf317d314af2";
        final int activeActivityId = 345;
        final Map<Integer, FragmentData> fragmentDataMap = new HashMap<>();
        fragmentDataMap.put(fragmentId, new FragmentData(fragmentSpanId));

        fragmentStateDataListener.activityFragmentMap.put(activeActivityId, fragmentDataMap);
        fragmentStateDataListener.activeActivityHashCode = activeActivityId;

        assertEquals(fragmentStateDataListener.getParentSpanIdFromFragmentId(fragmentId), fragmentSpanId);
    }

    @Test
    public void getParentSpanIdFromFragmentId_noFragmentData() {
        final int fragmentId = 234;
        final int activeActivityId = 345;
        final Map<Integer, FragmentData> fragmentDataMap = new HashMap<>();
        fragmentDataMap.put(fragmentId, null);

        fragmentStateDataListener.activityFragmentMap.put(activeActivityId, fragmentDataMap);
        fragmentStateDataListener.activeActivityHashCode = activeActivityId;

        assertNull(fragmentStateDataListener.getParentSpanIdFromFragmentId(fragmentId));
    }

    @Test
    public void getParentSpanIdFromFragmentId_noRecords() {
        final int fragmentId = 234;
        final int activeActivityId = 345;

        fragmentStateDataListener.activityFragmentMap.clear();
        fragmentStateDataListener.activeActivityHashCode = activeActivityId;

        assertNull(fragmentStateDataListener.getParentSpanIdFromFragmentId(fragmentId));
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], fragmentStateDataListener.getPermissions());
    }

}