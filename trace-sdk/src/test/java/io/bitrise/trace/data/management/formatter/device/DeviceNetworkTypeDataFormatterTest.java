package io.bitrise.trace.data.management.formatter.device;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.collector.device.DeviceNetworkTypeDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;
import org.junit.Test;

/**
 * Tests for {@link DeviceNetworkTypeDataFormatter}.
 */
public class DeviceNetworkTypeDataFormatterTest extends BaseDataFormatterTest {

  @Test
  public void formatData() {
    final String deviceNetworkType = "WIFI";

    final Data inputData = new Data(DeviceNetworkTypeDataCollector.class);
    inputData.setContent(deviceNetworkType);
    final FormattedData[] outputData = new DeviceNetworkTypeDataFormatter().formatData(inputData);

    assertEquals(1, outputData.length);
    assertEquals(new ResourceEntity("device.network", deviceNetworkType),
        outputData[0].getResourceEntity());
  }
}
