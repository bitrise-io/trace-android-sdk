package io.bitrise.trace.data.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.application.ApplicationVersionCodeDataCollector;
import io.bitrise.trace.data.dto.Data;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ApplicationVersionCodeDataCollector}.
 */
public class ApplicationVersionCodeDataCollectorTest {

  private static final int versionCode = 123;
  private final Context mockContext = Mockito.mock(Context.class);
  private final ApplicationVersionCodeDataCollector collector =
      new ApplicationVersionCodeDataCollector(
          mockContext);

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

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], collector.getPermissions());
  }

  @Test
  public void getIntervalMs() {
    assertEquals(0, collector.getIntervalMs());
  }
}
