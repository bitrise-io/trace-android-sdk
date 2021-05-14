package io.bitrise.trace.session;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for the {@link ApplicationSessionManager}.
 */
public class ApplicationSessionManagerTest {

  /**
   * A {@link SessionManager} instance.
   */
  private SessionManager sessionManager;

  @Before
  public void setUp() {
    this.sessionManager = ApplicationSessionManager.getInstance();
  }

  /**
   * Checks that the {@link ApplicationSessionManager#getInstance()} returns a Singleton value.
   */
  @Test
  public void getInstance_assertIsSingleton() {
    final SessionManager actualValue = ApplicationSessionManager.getInstance();
    assertThat(actualValue, sameInstance(sessionManager));
  }

  /**
   * Not started {@link SessionManager}s should not have active {@link Session}s.
   */
  @Test
  public void noSession_shouldReturnNull() {
    sessionManager.stopSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, is(nullValue()));
  }

  /**
   * Session should not be {@code null} when the manager started it.
   */
  @Test
  public void startSession_shouldReturnNonNull() {
    sessionManager.startSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, is(notNullValue()));
  }

  /**
   * If SessionManager started multiple times it should not start a new Session.
   */
  @Test
  public void multiStartSession_shouldReturnSingleton() {
    sessionManager.startSession();
    final Session expectedResult = sessionManager.getActiveSession();
    sessionManager.startSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * Stopped SessionManager's Session should be {@code null}.
   */
  @Test
  public void stopSession_shouldSetActiveSessionToNull() {
    sessionManager.startSession();
    sessionManager.stopSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, is(nullValue()));
  }

  /**
   * It is safe to call multiple times stop.
   */
  @Test
  public void multiStopSession_shouldNullSessionStayNull() {
    sessionManager.stopSession();
    sessionManager.stopSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, is(nullValue()));
  }

  /**
   * New Session should be different from previous one.
   */
  @Test
  public void newSession_shouldHaveNewSession() {
    sessionManager.startSession();
    final Session expectedValue = sessionManager.getActiveSession();
    sessionManager.stopSession();
    sessionManager.startSession();
    final Session actualResult = sessionManager.getActiveSession();
    assertThat(actualResult, not(equalTo(expectedValue)));
  }
}