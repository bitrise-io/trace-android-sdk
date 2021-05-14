package io.bitrise.trace.data.collector.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.dto.Data;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumented tests for {@link ApplicationVersionCodeDataCollector}.
 */
public class ApplicationVersionCodeDataCollectorInstrumentedTest {

  private static Context context;

  @BeforeClass
  public static void setUp() {
    context = ApplicationProvider.getApplicationContext();
  }

  /**
   * Verifies that when {@link ApplicationVersionCodeDataCollector#collectData()} is called,
   * the content of the returned {@link Data} should match with the predefined value.
   */
  @Test
  public void collectData_contentShouldMatch() {
    final ApplicationVersionCodeDataCollector applicationVersionCodeDataCollector =
        new ApplicationVersionCodeDataCollector(context);
    final String actualValue =
        (String) applicationVersionCodeDataCollector.collectData().getContent();
    final String expectedValue = "1";
    assertThat(actualValue, is(expectedValue));
  }
}
