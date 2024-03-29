package io.bitrise.trace.data.collector.device;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link DeviceLocaleDataCollector}.
 */
public class DeviceLocaleDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

  final DeviceLocaleDataCollector collector = new DeviceLocaleDataCollector(context);

  /**
   * Verifies that when {@link DeviceLocaleDataCollector#collectData()} is called, the content*
   * of the returned {@link Data} should not be {@code null}.
   */
  @Test
  public void collectData_contentShouldBeNotNull() {
    assertThat(collector.collectData().getContent(), is(notNullValue()));
  }
}
