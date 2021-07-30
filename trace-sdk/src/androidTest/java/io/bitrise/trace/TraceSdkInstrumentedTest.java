package io.bitrise.trace;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import io.bitrise.trace.configuration.ConfigurationManager;
import java.util.Collections;
import java.util.HashMap;
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
    TraceSdk.initNetworkTracing(null);
    assertTrue(TraceSdk.isNetworkTracingEnabled);
  }

  @Test
  public void initNetworkTracing_whenOptionDisabled() {
    TraceSdk.initNetworkTracing(Collections.singletonList(
        new TraceOption.NetworkUrlConnectionTracing(false)));
    assertFalse(TraceSdk.isNetworkTracingEnabled);
  }
}
