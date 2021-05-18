package io.bitrise.trace.data.collector.device;

import androidx.annotation.NonNull;
import androidx.test.filters.SdkSuppress;

import org.junit.Test;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void collectData_pre29() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    public void collectData_post29() {
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
