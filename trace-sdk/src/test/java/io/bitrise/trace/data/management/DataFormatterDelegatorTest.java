package io.bitrise.trace.data.management;

import androidx.annotation.NonNull;

import org.junit.Test;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.DataSourceType;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for the {@link DataFormatterDelegator} class.
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
            dataFormatterDelegator.formatData(new Data(dataSourceType));
        }
    }

    /**
     * Verifies that non handled {@link DataSourceType}s should throw IllegalArgumentException when the
     * DataFormatterDelegator tries to format it.
     */
    @Test(expected = IllegalArgumentException.class)
    public void formatData_allDataSourceTypesShouldBeDelted() {
        final DataFormatterDelegator dataFormatterDelegator = DataFormatterDelegator.getInstance();
        dataFormatterDelegator.formatData(new Data(DataSourceType.valueOf("Invalid")));
    }
}