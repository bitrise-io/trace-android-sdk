package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    /**
     * Verifies that when {@link DeviceNetworkTypeDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final DeviceNetworkTypeDataCollector deviceNetworkTypeDataCollector =
                new DeviceNetworkTypeDataCollector(context);
        final String actualValue = (String) deviceNetworkTypeDataCollector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }
}
