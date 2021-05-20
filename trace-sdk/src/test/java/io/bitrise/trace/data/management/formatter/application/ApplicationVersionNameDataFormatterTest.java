package io.bitrise.trace.data.management.formatter.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.bitrise.trace.data.collector.application.ApplicationVersionNameDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import org.junit.Test;

/**
 * Unit tests for {@link ApplicationVersionNameDataFormatter}.
 */
public class ApplicationVersionNameDataFormatterTest extends BaseDataFormatterTest {

  @Test
  public void formatData() {
    final String versionName = "1.2.3";

    final Data data = new Data(ApplicationVersionNameDataCollector.class);
    data.setContent(versionName);
    final ApplicationVersionNameDataFormatter formatter = new ApplicationVersionNameDataFormatter();
    final FormattedData[] formattedData = formatter.formatData(data);
    assertEquals(1, formattedData.length);
    assertNotNull(formattedData[0].getResourceEntity());
    assertEquals("app.version", formattedData[0].getResourceEntity().getLabel());
    assertEquals(versionName, formattedData[0].getResourceEntity().getValue());
  }
}
