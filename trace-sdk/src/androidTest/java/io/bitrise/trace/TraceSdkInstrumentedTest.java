package io.bitrise.trace;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.utils.log.TraceLog;
import org.junit.Before;
import org.junit.Test;

/**
 * Instrumented tests for {@link TraceSdk}.
 */
public class TraceSdkInstrumentedTest {

  final Context context = InstrumentationRegistry.getInstrumentation()
                                                 .getTargetContext()
                                                 .getApplicationContext();

  @Before
  public void before() {
    TraceSdk.reset();
  }

  @Test
  public void initConfigurations() {
    TraceSdk.initConfigurations(context);
    assertTrue(ConfigurationManager.isInitialised());
  }

  @Test
  public void initNetworkTracing() {
    TraceSdk.initNetworkTracing();
    assertTrue(TraceSdk.isNetworkTracingEnabled);
  }
}
