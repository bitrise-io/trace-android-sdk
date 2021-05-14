package io.bitrise.trace.data.collector.memory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link ApplicationUsedMemoryDataCollector}.
 */
public class ApplicationUsedMemoryDataCollectorInstrumentedTest
    extends BaseDataCollectorInstrumentedTest {

  /**
   * Verifies that when {@link ApplicationUsedMemoryDataCollector#collectData()} is called, the
   * content of the returned {@link Data} should not be {@code null}.
   */
  @Test
  public void collectData_contentShouldBeNotNull() {
    final ApplicationUsedMemoryDataCollector applicationUsedMemoryDataCollector =
        new ApplicationUsedMemoryDataCollector(
            context);
    final Long actualValue = (Long) applicationUsedMemoryDataCollector.collectData().getContent();
    assertThat(actualValue, is(notNullValue()));
  }
}
