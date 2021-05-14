package io.bitrise.trace.data.management;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import androidx.annotation.NonNull;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Unit tests for the {@link DataFormatterDelegator} class.
 */
public class DataFormatterDelegatorTest {

  /**
   * Checks that the {@link DataFormatterDelegator#getInstance()} returns a Singleton value.
   */
  @Test
  public void getInstance_assertIsSingleton() {
    final DataFormatterDelegator expectedValue = DataFormatterDelegator.getInstance();
    final DataFormatterDelegator actualValue = DataFormatterDelegator.getInstance();
    assertThat(actualValue, sameInstance(expectedValue));
  }

  /**
   * Verifies that all {@link DataSourceType}s are delegated to a {@link Formatter}.
   */
  @Test
  public void formatData_allDataSourceTypesShouldBeDelegated() {
    final DataFormatterDelegator dataFormatterDelegator = DataFormatterDelegator.getInstance();
    for (@NonNull final DataSourceType dataSourceType : DataSourceType.values()) {
      assertNotNull(dataFormatterDelegator.formatData(new Data(dataSourceType)));
    }
  }

  /**
   * Verifies that non handled {@link DataSourceType}s should throw IllegalArgumentException
   * when the DataFormatterDelegator tries to format it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void formatData_allDataSourceTypesShouldBeDeleted() {
    final DataFormatterDelegator dataFormatterDelegator = DataFormatterDelegator.getInstance();
    dataFormatterDelegator.formatData(new Data(DataSourceType.valueOf("Invalid")));
  }
}