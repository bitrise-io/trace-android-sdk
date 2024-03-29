package io.bitrise.trace.data.management.formatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.resource.ResourceLabel;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link ResourceDataFormatter}.
 */
public class ResourceDataFormatterTest extends BaseDataFormatterTest {

  final Data inputData = Mockito.mock(Data.class, Mockito.CALLS_REAL_METHODS);
  final ResourceLabel dummyResourceLabel = ResourceLabel.DEVICE_ROOTED;
  final String dummyResourceEntityLabel = "device.rooted";

  @Test
  public void formatResource_string() {
    inputData.setContent("an exciting string resource");

    final FormattedData[] outputArray =
        ResourceDataFormatter.formatResource(inputData, dummyResourceLabel);
    assertEquals(1, outputArray.length);
    final FormattedData outputData = outputArray[0];

    assertNotNull(outputData);
    assertNotNull(outputData.getResourceEntity());
    assertEquals(dummyResourceEntityLabel, outputData.getResourceEntity().getLabel());
    assertEquals("an exciting string resource", outputData.getResourceEntity().getValue());
  }

  @Test
  public void formatResource_integer() {
    inputData.setContent(21);

    final FormattedData[] outputArray =
        ResourceDataFormatter.formatResource(inputData, dummyResourceLabel);
    assertEquals(1, outputArray.length);
    final FormattedData outputData = outputArray[0];

    assertNotNull(outputData);
    assertNotNull(outputData.getResourceEntity());
    assertEquals(dummyResourceEntityLabel, outputData.getResourceEntity().getLabel());
    assertEquals("21", outputData.getResourceEntity().getValue());
  }

  @Test
  public void formatResource_float() {
    inputData.setContent((float) 3.145);

    final FormattedData[] outputArray =
        ResourceDataFormatter.formatResource(inputData, dummyResourceLabel);
    assertEquals(1, outputArray.length);
    final FormattedData outputData = outputArray[0];

    assertNotNull(outputData);
    assertNotNull(outputData.getResourceEntity());
    assertEquals(dummyResourceEntityLabel, outputData.getResourceEntity().getLabel());
    assertEquals("3.145", outputData.getResourceEntity().getValue());
  }

  @Test
  public void formatResource_boolean() {
    inputData.setContent(true);

    final FormattedData[] outputArray =
        ResourceDataFormatter.formatResource(inputData, dummyResourceLabel);
    assertEquals(1, outputArray.length);
    final FormattedData outputData = outputArray[0];

    assertNotNull(outputData);
    assertNotNull(outputData.getResourceEntity());
    assertEquals(dummyResourceEntityLabel, outputData.getResourceEntity().getLabel());
    assertEquals("true", outputData.getResourceEntity().getValue());
  }

  @Test
  public void formatResource_nullContent() {
    inputData.setContent(null);
    final FormattedData[] outputArray =
        ResourceDataFormatter.formatResource(inputData, dummyResourceLabel);
    assertEquals(0, outputArray.length);
  }
}
