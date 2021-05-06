package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceLocaleDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link DeviceLocaleDataFormatter}.
 */
public class DeviceLocaleDataFormatterTest extends BaseDataFormatter {

    @Test
    public void formatData_localeShouldBeEnUs() {
        final Data inputData = new Data(DeviceLocaleDataCollector.class);
        inputData.setContent("en_US");

        final FormattedData[] outputData =  new DeviceLocaleDataFormatter().formatData(inputData);
        assertEquals(1, outputData.length);
        assertNotNull(outputData[0].getResourceEntity());
        assertEquals("device.locale", outputData[0].getResourceEntity().getLabel());
        assertEquals("en_US", outputData[0].getResourceEntity().getValue());
    }
}
