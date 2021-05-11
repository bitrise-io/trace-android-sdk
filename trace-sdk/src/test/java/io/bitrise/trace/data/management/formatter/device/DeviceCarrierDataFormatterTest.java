package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceCarrierDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DeviceCarrierDataFormatter}.
 */
public class DeviceCarrierDataFormatterTest extends BaseDataFormatterTest {

    @Test
    public void formatData() {
        final String carrier = "vodafone";

        final Data inputData = new Data(DeviceCarrierDataCollector.class);
        inputData.setContent(carrier);
        final FormattedData[] outputData =  new DeviceCarrierDataFormatter().formatData(inputData);

        assertEquals(1, outputData.length);
        assertEquals(new ResourceEntity("device.carrier", carrier), outputData[0].getResourceEntity());
    }
}
