package io.bitrise.trace.session;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.Test;

/**
 * Unit tests for the {@link Session} objects. They will check that the ULID token creation works
 * as expected, to
 * create unique tokens.
 */
public class SessionTest {

  /**
   * When a {@link Session} is created the {@link io.azam.ulidj.ULID} of it should not be
   * {@code null}.
   */
  @Test
  public void newSession_createsNonNullUlid() {
    final Session session = new Session();
    final String ulid = session.getUlid();
    assertThat(ulid, is(notNullValue()));
  }

  /**
   * When multiple {@link Session}s are created the String value of the
   * {@link io.azam.ulidj.ULID} of it should be different.
   */
  @Test
  public void newSessions_createDifferentUlidStrings() {
    final Session session1 = new Session();
    final String ulid1 = session1.getUlid();

    final Session session2 = new Session();
    final String ulid2 = session2.getUlid();
    assertThat(ulid1, is(not(ulid2)));
  }
}