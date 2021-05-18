package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link DeviceCarrierDataCollector}.
 */
public class DeviceCarrierDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    final DeviceCarrierDataCollector collector = new DeviceCarrierDataCollector(context);

    /**
     * Verifies that when {@link DeviceCarrierDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final Data expectedData = new Data(DeviceCarrierDataCollector.class);
        expectedData.setContent("Android");
        assertThat(collector.collectData(), is(equalTo(expectedData)));
    }
}
