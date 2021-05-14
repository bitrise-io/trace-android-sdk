package io.bitrise.trace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import org.junit.Test;

/**
 * Test cases for {@link TraceSdk}.
 */
public class TraceSdkTest {

  /**
   * Checks if {@link TraceSdk#initSessionManager()} inits the Session.
   */
  @Test
  public void initSessionManager_SessionShouldNotBeNull() {
    TraceSdk.initSessionManager();
    final Session actualResult = ApplicationSessionManager.getInstance().getActiveSession();
    assertThat(actualResult, is(notNullValue()));
  }
}