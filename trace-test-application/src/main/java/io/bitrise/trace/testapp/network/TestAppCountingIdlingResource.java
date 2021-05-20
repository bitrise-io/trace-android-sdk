package io.bitrise.trace.testapp.network;

import androidx.test.espresso.idling.CountingIdlingResource;
import javax.inject.Singleton;

/**
 * This is an {@link CountingIdlingResource} singleton for use in tests.
 *
 * @see
 * <a href="https://developer.android.com/reference/androidx/test/espresso/idling/CountingIdlingResource">the docs</a>
 */
@Singleton
public class TestAppCountingIdlingResource {

  private static final String RESOURCE = "TEST_APP_RESOURCE";
  private static CountingIdlingResource countingIdlingResource =
      new CountingIdlingResource(RESOURCE);

  private TestAppCountingIdlingResource() {
    // private constructor
  }

  public static CountingIdlingResource getInstance() {
    return countingIdlingResource;
  }

  public static void increment() {
    countingIdlingResource.increment();
  }

  /**
   * Decrements this resource.
   */
  public static void decrement() {
    if (!countingIdlingResource.isIdleNow()) {
      countingIdlingResource.decrement();
    }
  }

  public static void reset() {
    countingIdlingResource = new CountingIdlingResource(RESOURCE);
  }
}
