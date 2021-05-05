package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceNetworkTypeDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DeviceNetworkTypeDataFormatter}.
 */
public class DeviceNetworkTypeDataFormatterTest extends BaseDataFormatterTest {

    final String deviceNetworkType = "WIFI";

    @Test
    public void formatData() {
        final Data inputData = new Data(DeviceNetworkTypeDataCollector.class);
        inputData.setContent(deviceNetworkType);
        final FormattedData[] outputData =  new DeviceNetworkTypeDataFormatter().formatData(inputData);

        assertEquals(1, outputData.length);
        assertEquals(new ResourceEntity("device.network", deviceNetworkType), outputData[0].getResourceEntity());
    }
}
