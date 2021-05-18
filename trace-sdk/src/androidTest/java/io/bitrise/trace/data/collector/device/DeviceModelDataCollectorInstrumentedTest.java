package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link DeviceModelDataCollector}.
 */
public class DeviceModelDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    final DeviceModelDataCollector collector = new DeviceModelDataCollector();

    /**
     * Verifies that when {@link DeviceModelDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final String actualValue = (String) collector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }

}
