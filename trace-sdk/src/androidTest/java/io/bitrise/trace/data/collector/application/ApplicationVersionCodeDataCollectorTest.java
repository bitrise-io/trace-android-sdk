package io.bitrise.trace.data.collector.application;

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

/**
 * Unit tests for {@link ApplicationVersionCodeDataCollector}
 */
public class ApplicationVersionCodeDataCollectorTest {

    private final static int versionCode = 123;
    private final ApplicationVersionCodeDataCollector collector = new ApplicationVersionCodeDataCollector(
            InstrumentationRegistry.getInstrumentation().getContext());

    @BeforeClass
    public static void setUp() {
        final Map<String, Object> configuration = new HashMap<>();
        configuration.put("VERSION_CODE", versionCode);
        ConfigurationManager.getDebugInstance(BuildConfig.TRACE_TOKEN, configuration);
    }

    @AfterClass
    public static void tearDown() {
        ConfigurationManager.reset();
    }

    @Test
    public void collectData() {
        final Data expectedData = new Data(ApplicationVersionCodeDataCollector.class);
        expectedData.setContent(String.valueOf(versionCode));

        assertEquals(expectedData, collector.collectData());
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
