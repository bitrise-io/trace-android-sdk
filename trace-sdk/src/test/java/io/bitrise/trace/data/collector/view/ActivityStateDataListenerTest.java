package io.bitrise.trace.data.collector.view;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Context;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.ActivityState;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ActivityStateDataListener}.
 */
public class ActivityStateDataListenerTest {

  final Context mockContext = Mockito.mock(Context.class);
  final ActivityStateDataListener listener = new ActivityStateDataListener(mockContext);
  final Activity mockActivity = Mockito.mock(Activity.class);

  @Test
  public void onActivityCreated_notActive() {
    listener.onActivityCreated(mockActivity, null);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void onActivityStarted_notActive() {
    listener.onActivityStarted(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void onActivityStopped_notActive() {
    listener.onActivityStopped(mockActivity);
    assertFalse(listener.isActive());
    assertEquals(listener.activityMap.size(), 0);
  }

  @Test
  public void stopCollecting_shouldEndAnyOpenActivities() {

    final ActivityStateDataListener listener = new ActivityStateDataListener(mockContext);
    final DataManager mockDataManager = Mockito.mock(DataManager.class);
    listener.setDataManager(mockDataManager);

    listener.updateActivityMap(
        mockActivity.hashCode(),
        mockActivity.getClass().getSimpleName(),
        ActivityState.CREATED);

    listener.updateActivityMap(
        mockActivity.hashCode(),
        mockActivity.getClass().getSimpleName(),
        ActivityState.STARTED);

    assertEquals(1, listener.activityMap.size());
    assertEquals(2, listener.activityMap.get(mockActivity.hashCode()).getStateMap().size());

    listener.stopCollecting();

    // assert that a record was added to the state map.
    assertEquals(1, listener.activityMap.size());
    assertEquals(3, listener.activityMap.get(mockActivity.hashCode()).getStateMap().size());

    // assert that data manager was notified
    final ArgumentCaptor<Data> argumentCaptorData = ArgumentCaptor.forClass(Data.class);
    verify(mockDataManager, times(1))
        .handleReceivedData(argumentCaptorData.capture());

    final Object actualContent = argumentCaptorData.getValue().getContent();
    assertTrue(actualContent instanceof ActivityData);

    // verify 3 records are there (created / started / stopped)
    final ActivityData actualActivityData = (ActivityData) actualContent;
    assertEquals(3, actualActivityData.getStateMap().size());

  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], listener.getPermissions());
  }
}
