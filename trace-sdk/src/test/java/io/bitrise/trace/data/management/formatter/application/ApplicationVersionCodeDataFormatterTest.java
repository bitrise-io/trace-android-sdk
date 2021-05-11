package io.bitrise.trace.data.management.formatter.application;

import org.junit.Test;

import io.bitrise.trace.data.collector.application.ApplicationVersionCodeDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link ApplicationVersionCodeDataFormatter}.
 */
public class ApplicationVersionCodeDataFormatterTest extends BaseDataFormatterTest {

    @Test
    public void formatData() {
        final String versionCode = "123";

        final Data data = new Data(ApplicationVersionCodeDataCollector.class);
        data.setContent(versionCode);
        final ApplicationVersionCodeDataFormatter formatter = new ApplicationVersionCodeDataFormatter();
        final FormattedData[] formattedData = formatter.formatData(data);
        assertEquals(1, formattedData.length);
        assertNotNull(formattedData[0].getResourceEntity());
        assertEquals("app.build", formattedData[0].getResourceEntity().getLabel());
        assertEquals(versionCode, formattedData[0].getResourceEntity().getValue());
    }
}
