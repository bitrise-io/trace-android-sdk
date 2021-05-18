package io.bitrise.trace.data.collector.memory;

import org.junit.Test;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link SystemMemoryDataCollector}.
 */
public class SystemMemoryDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    private final SystemMemoryDataCollector collector = new SystemMemoryDataCollector(context);

    /**
     * Verifies that when {@link SystemMemoryDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final Long actualValue = (Long) collector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }

}
