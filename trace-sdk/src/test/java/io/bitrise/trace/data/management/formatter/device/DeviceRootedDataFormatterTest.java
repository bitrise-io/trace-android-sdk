package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link DeviceRootedDataFormatter}.
 */
public class DeviceRootedDataFormatterTest extends BaseDataFormatter {

    @Test
    public void formatData_shouldBeRooted() {
        final Data inputData = new Data(DeviceRootedDataCollector.class);
        inputData.setContent(true);

        final FormattedData[] outputData =  new DeviceRootedDataFormatter().formatData(inputData);
        assertEquals(1, outputData.length);
        assertNotNull(outputData[0].getResourceEntity());
        assertEquals("device.rooted", outputData[0].getResourceEntity().getLabel());
        assertEquals("true", outputData[0].getResourceEntity().getValue());
    }
}
