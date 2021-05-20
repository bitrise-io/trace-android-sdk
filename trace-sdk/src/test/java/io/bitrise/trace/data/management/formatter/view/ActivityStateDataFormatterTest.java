package io.bitrise.trace.data.management.formatter.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.collector.view.ActivityStateDataListener;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.ActivityState;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.opencensus.proto.trace.v1.Span;
import org.junit.Test;

/**
 * Tests for {@link ActivityStateDataFormatter}.
 */
public class ActivityStateDataFormatterTest {

  private static final String DUMMY_SPAN_ID = "0123456789ABCDEF";

  @Test
  public void formatData_notActivityData() {
    final Data inputData = new Data(DeviceRootedDataCollector.class);
    inputData.setContent(true);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(0, output.length);
  }

  @Test
  public void formatData_validActivityData() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.CREATED, 10L);
    inputActivityData.putState(ActivityState.STARTED, 20L);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.putState(ActivityState.PAUSED, 40L);
    inputActivityData.putState(ActivityState.STOPPED, 50L);
    inputActivityData.setName("MainActivity.java");

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(1, output.length);

    final Span outputSpan = output[0].getSpan();
    assertEquals("MainActivity.java", outputSpan.getName().getValue());
    assertEquals(10000000L, outputSpan.getStartTime().getNanos());
    assertEquals(50000000L, outputSpan.getEndTime().getNanos());
  }

  @Test
  public void formatData_noCreatedButStart() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.STARTED, 20L);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.putState(ActivityState.PAUSED, 40L);
    inputActivityData.putState(ActivityState.STOPPED, 50L);
    inputActivityData.setName("MainActivity.java");

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(1, output.length);

    final Span outputSpan = output[0].getSpan();
    assertEquals("MainActivity.java", outputSpan.getName().getValue());
    assertEquals(20000000L, outputSpan.getStartTime().getNanos());
    assertEquals(50000000L, outputSpan.getEndTime().getNanos());
  }

  @Test
  public void formatData_noCreatedOrStart() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.putState(ActivityState.PAUSED, 40L);
    inputActivityData.putState(ActivityState.STOPPED, 50L);
    inputActivityData.setName("MainActivity.java");

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(0, output.length);
  }

  @Test
  public void formatData_noStoppedButPaused() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.CREATED, 10L);
    inputActivityData.putState(ActivityState.STARTED, 20L);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.putState(ActivityState.PAUSED, 40L);
    inputActivityData.setName("MainActivity.java");

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(1, output.length);

    final Span outputSpan = output[0].getSpan();
    assertEquals("MainActivity.java", outputSpan.getName().getValue());
    assertEquals(10000000L, outputSpan.getStartTime().getNanos());
    assertEquals(40000000L, outputSpan.getEndTime().getNanos());
  }

  @Test
  public void formatData_noStoppedOrPaused() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.CREATED, 10L);
    inputActivityData.putState(ActivityState.STARTED, 20L);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.setName("MainActivity.java");

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(0, output.length);
  }

  @Test
  public void formatData_noName() {
    final ActivityData inputActivityData = new ActivityData(DUMMY_SPAN_ID);
    inputActivityData.putState(ActivityState.CREATED, 10L);
    inputActivityData.putState(ActivityState.STARTED, 20L);
    inputActivityData.putState(ActivityState.RESUMED, 30L);
    inputActivityData.putState(ActivityState.PAUSED, 40L);
    inputActivityData.putState(ActivityState.STOPPED, 50L);

    final Data inputData = new Data(ActivityStateDataListener.class);
    inputData.setContent(inputActivityData);

    final FormattedData[] output = new ActivityStateDataFormatter().formatData(inputData);

    assertNotNull(output);
    assertEquals(0, output.length);
  }
}
