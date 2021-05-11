package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceLocaleDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link DeviceLocaleDataFormatter}.
 */
public class DeviceLocaleDataFormatterTest extends BaseDataFormatterTest {

    @Test
    public void formatData_localeShouldBeEnUs() {
        final String deviceLocale = "en_US";

        final Data inputData = new Data(DeviceLocaleDataCollector.class);
        inputData.setContent("en_US");

        final FormattedData[] outputData =  new DeviceLocaleDataFormatter().formatData(inputData);
        assertEquals(1, outputData.length);
        assertNotNull(outputData[0].getResourceEntity());
        assertEquals(new ResourceEntity("device.locale", deviceLocale), outputData[0].getResourceEntity());
    }
}
