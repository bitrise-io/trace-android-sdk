package io.bitrise.trace.data.application;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.application.ApplicationVersionNameDataCollector;
import io.bitrise.trace.data.dto.Data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ApplicationVersionNameDataCollector}
 */
public class ApplicationVersionNameDataCollectorTest {

    private final static String versionName = "1.2.3";
    private final Context mockContext = Mockito.mock(Context.class);
    private final ApplicationVersionNameDataCollector collector = new ApplicationVersionNameDataCollector(
            mockContext);

    @Test
    public void collectData_configurationManagerInitialised() {
        final Map<String, Object> configuration = new HashMap<>();
        configuration.put("VERSION_NAME", versionName);
        ConfigurationManager.getDebugInstance(BuildConfig.TRACE_TOKEN, configuration);

        final Data expectedData = new Data(ApplicationVersionNameDataCollector.class);
        expectedData.setContent(versionName);

        assertEquals(expectedData, collector.collectData());

        ConfigurationManager.reset();
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
