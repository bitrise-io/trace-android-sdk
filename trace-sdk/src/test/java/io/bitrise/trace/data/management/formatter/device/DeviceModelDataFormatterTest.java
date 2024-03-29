package io.bitrise.trace.data.management.formatter.device;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.collector.device.DeviceModelDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;
import org.junit.Test;

/**
 * Tests for {@link DeviceModelDataFormatter}.
 */
public class DeviceModelDataFormatterTest extends BaseDataFormatterTest {

  @Test
  public void formatData() {
    final String deviceModel = "Pixel 3";

    final Data inputData = new Data(DeviceModelDataCollector.class);
    inputData.setContent(deviceModel);
    final FormattedData[] outputData = new DeviceModelDataFormatter().formatData(inputData);

    assertEquals(1, outputData.length);
    assertEquals(new ResourceEntity("device.type", deviceModel), outputData[0].getResourceEntity());
  }
}
