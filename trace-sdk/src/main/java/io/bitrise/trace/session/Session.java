package io.bitrise.trace.session;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.azam.ulidj.ULID;
import java.security.SecureRandom;

/**
 * Data class for sessions.
 */
public class Session {

  /**
   * The {@link ULID} - A Universally Unique Lexicographically Sortable Identifier.
   * Similar to a {@link java.util.UUID} but it can be sorted alphabetically and in a time manner.
   */
  @NonNull
  private final String ulid;

  /**
   * Constructor for class.
   */
  public Session() {
    this.ulid = ULID.random(new SecureRandom());
  }

  /**
   * Constructor with the sessionId to use - used only for testing purposes.
   *
   * @param sessionId a ulid to use for the session id.
   */
  @VisibleForTesting
  public Session(@NonNull final String sessionId) {
    this.ulid = sessionId;
  }

  /**
   * Gets the ULID of the Session.
   *
   * @return the ULID.
   */
  @NonNull
  public String getUlid() {
    return ulid;
  }


}
