package io.bitrise.trace.data.collector.device;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorInstrumentedTest
    extends BaseDataCollectorInstrumentedTest {

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void collectData_preSdk29() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    public void collectData_sdk29AndAfter() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertNetworkCollectedData(collector.collectData(), "WIFI");
    }

    private void assertNetworkCollectedData(@NonNull final Data actualData,
                                            @NonNull final String expectedContent) {
        final Data expectedData = new Data(DeviceNetworkTypeDataCollector.class);
        expectedData.setContent(expectedContent);
        assertThat(actualData, is(equalTo(expectedData)));
    }
}
