package io.bitrise.trace.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import javax.inject.Singleton;

/**
 * SessionManager implementation. The Session will start when the Application is created, and
 * will end, when the Application is terminated.
 */
@Singleton
public class ApplicationSessionManager implements SessionManager {

  /**
   * The singleton instance.
   */
  private static volatile SessionManager sessionManager;

  /**
   * The currently active {@link Session}.
   */
  @Singleton
  private static Session session;

  /**
   * Constructor to prevent instantiation outside of the class.
   */
  private ApplicationSessionManager() {
    // nop
  }

  /**
   * Gets the singleton instance from the class.
   *
   * @return the singleton instance.
   */
  @NonNull
  public static synchronized SessionManager getInstance() {
    if (sessionManager == null) {
      TraceLog.d(LogMessageConstants.APPLICATION_SESSION_MANAGER_INITIALISED);
      sessionManager = new ApplicationSessionManager();
    }
    return sessionManager;
  }

  /**
   * Resets the state of the ApplicationSessionManager.
   */
  public static void reset() {
    if (sessionManager == null) {
      return;
    }

    sessionManager.stopSession();
    sessionManager = null;
  }

  @Override
  public synchronized void startSession() {
    if (session == null) {
      session = new Session();
    }
    TraceLog.d(LogMessageConstants.APPLICATION_SESSION_MANAGER_STARTED);
  }

  @Override
  public synchronized void stopSession() {
    session = null;
    TraceLog.d(LogMessageConstants.APPLICATION_SESSION_MANAGER_STOPPED);
  }

  @Nullable
  @Override
  public Session getActiveSession() {
    return session;
  }

  @VisibleForTesting
  static synchronized boolean isSessionManagerActive() {
    return sessionManager != null;
  }

  @VisibleForTesting
  static synchronized boolean isSessionActive() {
    return session != null;
  }
}
