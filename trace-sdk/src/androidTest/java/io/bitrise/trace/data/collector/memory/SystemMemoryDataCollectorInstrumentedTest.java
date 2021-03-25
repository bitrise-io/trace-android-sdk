package io.bitrise.trace.data.collector.memory;

import org.junit.Test;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link SystemMemoryDataCollector}.
 */
public class SystemMemoryDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    /**
     * Verifies that when {@link SystemMemoryDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final SystemMemoryDataCollector systemMemoryDataCollector = new SystemMemoryDataCollector(context);
        final Long actualValue = (Long) systemMemoryDataCollector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }
}
