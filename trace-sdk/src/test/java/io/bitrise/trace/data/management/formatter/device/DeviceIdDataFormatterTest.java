package io.bitrise.trace.data.management.formatter.device;

import static org.junit.Assert.assertEquals;

import io.bitrise.trace.data.collector.device.DeviceIdDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.resource.ResourceEntity;
import java.util.UUID;
import org.junit.Test;

/**
 * Tests for {@link DeviceIdDataFormatter}.
 */
public class DeviceIdDataFormatterTest extends BaseDataFormatterTest {

  @Test
  public void formatData() {
    final String devideId = UUID.randomUUID().toString();

    final Data inputData = new Data(DeviceIdDataCollector.class);
    inputData.setContent(devideId);
    final FormattedData[] outputData = new DeviceIdDataFormatter().formatData(inputData);

    assertEquals(1, outputData.length);
    assertEquals(new ResourceEntity("device.id", devideId), outputData[0].getResourceEntity());
  }
}
