package io.bitrise.trace.session;

import androidx.annotation.Nullable;
import javax.inject.Singleton;

/**
 * Manages the {@link Session}s.
 */
@Singleton
public interface SessionManager {

  /**
   * Starts the Session.
   */
  void startSession();

  /**
   * Stops the Session.
   */
  void stopSession();

  /**
   * Returns the active Session or {@code null} when there is no active Session.
   *
   * @return the active Session.
   */
  @Nullable
  Session getActiveSession();
}
