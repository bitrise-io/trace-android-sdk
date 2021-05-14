package io.bitrise.trace.data.management.formatter.device;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;
import org.junit.Test;

/**
 * Tests for {@link DeviceOsDataFormatter}.
 */
public class DeviceOsDataFormatterTest extends BaseDataFormatterTest {

  @Test
  public void formatData() {
    final String deviceOs = "30";

    final Data inputData = new Data(DeviceOsVersionDataCollector.class);
    inputData.setContent(deviceOs);
    final FormattedData[] outputData = new DeviceOsDataFormatter().formatData(inputData);

    assertEquals(1, outputData.length);
    assertEquals(new ResourceEntity("os.version", deviceOs), outputData[0].getResourceEntity());
  }
}
