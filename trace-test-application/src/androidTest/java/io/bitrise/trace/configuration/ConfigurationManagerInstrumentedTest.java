package io.bitrise.trace.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.TraceSdk;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumented tests for the {@link ConfigurationManager} class.
 */
public class ConfigurationManagerInstrumentedTest {

  @BeforeClass
  public static void setUp() {
    TraceSdk.reset();
  }

  /**
   * Checks that a call to {@link ConfigurationManager#isInitialised()} should return {@code
   * true} after a call made to {@link ConfigurationManager#init(Context)}.
   */
  @Test
  public void getInstance_shouldInitialise() {
    ConfigurationManager.init(ApplicationProvider.getApplicationContext());
    final boolean actualValue = ConfigurationManager.isInitialised();
    assertThat(actualValue, is(true));
  }

  /**
   * Checks that a call to {@link ConfigurationManager#isInitialised()} should return {@code
   * false} after a call made to {@link ConfigurationManager#reset()}.
   */
  @Test
  public void reset_shouldNotBeInitialised() {
    ConfigurationManager.init(ApplicationProvider.getApplicationContext());
    ConfigurationManager.reset();

    final boolean actualValue = ConfigurationManager.isInitialised();
    assertThat(actualValue, is(false));
  }
}
