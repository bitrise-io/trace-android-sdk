package io.bitrise.trace.data.management.formatter.application;

import org.junit.Test;

import io.bitrise.trace.data.collector.application.ApplicationVersionNameDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link ApplicationVersionNameDataFormatter}.
 */
public class ApplicationVersionNameDataFormatterTest extends BaseDataFormatterTest {

    final String resourceLabel = "app.version";
    final String versionName = "1.2.3";

    @Test
    public void formatData() {
        final Data data = new Data(ApplicationVersionNameDataCollector.class);
        data.setContent(versionName);
        final ApplicationVersionNameDataFormatter formatter = new ApplicationVersionNameDataFormatter();
        final FormattedData[] formattedData = formatter.formatData(data);
        assertEquals(1, formattedData.length);
        assertNotNull(formattedData[0].getResourceEntity());
        assertEquals(resourceLabel, formattedData[0].getResourceEntity().getLabel());
        assertEquals(versionName, formattedData[0].getResourceEntity().getValue());
    }
}
