package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link DeviceLocaleDataCollector}.
 */
public class DeviceLocaleDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    /**
     * Verifies that when {@link DeviceLocaleDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final DeviceLocaleDataCollector deviceLocaleDataCollector = new DeviceLocaleDataCollector(context);
        assertThat(deviceLocaleDataCollector.collectData().getContent(), is(notNullValue()));
    }
}
