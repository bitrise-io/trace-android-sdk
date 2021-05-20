package io.bitrise.trace.data.collector.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.dto.Data;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumented tests for {@link ApplicationVersionNameDataCollector}.
 */
public class ApplicationVersionNameDataCollectorInstrumentedTest {

  private static Context context;

  @BeforeClass
  public static void setUp() {
    context = ApplicationProvider.getApplicationContext();
  }

  /**
   * Verifies that when {@link ApplicationVersionNameDataCollector#collectData()} is called,
   * the content of the returned {@link Data} should match with the predefined value.
   */
  @Test
  public void collectData_contentShouldMatch() {
    final ApplicationVersionNameDataCollector applicationVersionNameDataCollector =
        new ApplicationVersionNameDataCollector(context);
    final String actualValue =
        (String) applicationVersionNameDataCollector.collectData().getContent();
    final String expectedValue = "1.0";
    assertThat(actualValue, is(expectedValue));
  }
}
