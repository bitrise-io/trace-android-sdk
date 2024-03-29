package io.bitrise.trace.data.collector.device;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link DeviceIdDataCollector}.
 */
public class DeviceIdDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

  final DeviceIdDataCollector deviceIdDataCollector = new DeviceIdDataCollector(context);

  /**
   * Verifies that when {@link DeviceIdDataCollector#collectData()} is called, the content of*
   * the returned {@link Data} should not be {@code null}.
   */
  @Test
  public void collectData_contentShouldBeNotNull() {
    final String actualValue = (String) deviceIdDataCollector.collectData().getContent();
    assertThat(actualValue, is(notNullValue()));
  }

  /**
   * Verifies that when {@link DeviceIdDataCollector#collectData()} is called multiple times,
   * the returned ID should be the same.
   */
  @Test
  public void collectData_deviceIdShouldNotChange() {
    final String expectedValue = (String) deviceIdDataCollector.collectData().getContent();
    final String actualValue = (String) deviceIdDataCollector.collectData().getContent();
    assertThat(actualValue, is(expectedValue));
  }
}
