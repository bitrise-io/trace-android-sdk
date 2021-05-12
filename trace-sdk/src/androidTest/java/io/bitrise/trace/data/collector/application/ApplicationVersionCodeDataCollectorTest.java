package io.bitrise.trace.data.collector.application;

import android.content.res.Resources;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.dto.Data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link ApplicationVersionCodeDataCollector}
 */
public class ApplicationVersionCodeDataCollectorTest {

    private final static int versionCode = 123;
    private final ApplicationVersionCodeDataCollector collector = new ApplicationVersionCodeDataCollector(
            InstrumentationRegistry.getInstrumentation().getContext());

    @Test
    public void collectData_configurationManagerInitialised() {
        final Map<String, Object> configuration = new HashMap<>();
        configuration.put("VERSION_CODE", versionCode);
        ConfigurationManager.getDebugInstance(BuildConfig.TRACE_TOKEN, configuration);

        final Data expectedData = new Data(ApplicationVersionCodeDataCollector.class);
        expectedData.setContent(String.valueOf(versionCode));

        assertEquals(expectedData, collector.collectData());

        ConfigurationManager.reset();
    }

    @Test(expected = Resources.NotFoundException.class)
    public void collectData_configurationManagerNotInitialised() {
       assertNotNull(collector.collectData());
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
