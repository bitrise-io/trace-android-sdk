package io.bitrise.trace.data.collector.device;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link DeviceOsVersionDataCollector}.
 */
public class DeviceOsVersionDataCollectorInstrumentedTest
    extends BaseDataCollectorInstrumentedTest {

  /**
   * Verifies that when {@link DeviceOsVersionDataCollector#collectData()} is called, the
   * content of the returned {@link Data} should not be {@code null}.
   */
  @Test
  public void collectData_contentShouldBeNotNull() {
    final DeviceOsVersionDataCollector deviceOsVersionDataCollector =
        new DeviceOsVersionDataCollector();
    final String actualValue = (String) deviceOsVersionDataCollector.collectData().getContent();
    assertThat(actualValue, is(notNullValue()));
  }
}
