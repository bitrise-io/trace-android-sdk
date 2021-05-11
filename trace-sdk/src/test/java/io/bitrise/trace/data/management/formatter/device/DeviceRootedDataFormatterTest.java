package io.bitrise.trace.data.management.formatter.device;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DeviceRootedDataFormatter}.
 */
public class DeviceRootedDataFormatterTest extends BaseDataFormatterTest {

    @Test
    public void formatData_shouldBeRooted() {
        final Data inputData = new Data(DeviceRootedDataCollector.class);
        inputData.setContent(true);

        final FormattedData[] outputData =  new DeviceRootedDataFormatter().formatData(inputData);
        assertEquals(1, outputData.length);
        assertEquals(new ResourceEntity("device.rooted", "true"),
                outputData[0].getResourceEntity());
    }
}
