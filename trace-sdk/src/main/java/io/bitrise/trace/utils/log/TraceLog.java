package io.bitrise.trace.utils.log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.TraceSdk;
import javax.annotation.Nonnull;
import javax.inject.Singleton;

/**
 * Singleton class for logging messages to the console.
 */
@Singleton
public class TraceLog {

  /**
   * Tag of the SDK for logging.
   */
  public static final String TAG = "TraceSdk";
  private static final Object lock = new Object();
  @VisibleForTesting
  static Logger logger = null;

  private TraceLog() {
    throw new UnsupportedOperationException("Private constructor for TraceLog!");
  }

  /**
   * Makes the current logger an android.util.Log type Logger and will write to the Logcat.
   *
   * @return logger.
   */
  public static synchronized Logger makeAndroidLogger() {
    synchronized (lock) {
      logger = new AndroidLogger();
      return logger;
    }
  }

  /**
   * Makes the current logger a silent logger and does not output anything to the Logcat.
   *
   * @return logger.
   */
  public static synchronized Logger makeSilentLogger() {
    synchronized (lock) {
      logger = new SilentLogger();
      return logger;
    }
  }

  /**
   * Makes the current logger an error only logger, and will only write Log.e's to the Logcat.
   *
   * @return logger.
   */
  public static synchronized Logger makeErrorOnlyLogger() {
    synchronized (lock) {
      logger = new ErrorOnlyLogger();
      return logger;
    }
  }

  /**
   * Gets the current logger implementation, if the type has not been set it will become a
   * Silent logger by default.
   *
   * @return logger.
   */
  public static synchronized Logger getLogger() {
    synchronized (lock) {
      if (logger == null) {
        logger = new SilentLogger();
      }

      return logger;
    }
  }

  /**
   * Resets the current logger - required only for testing purposes.
   */
  @VisibleForTesting
  public static synchronized void reset() {
    synchronized (lock) {
      logger = null;
    }
  }

  //region
  // The following methods save us getting the current logger e.g. you can write:
  // TraceLog.w("MESSAGE"); instead of TraceLog.getLogger.w(TraceLog.TRACE_LOGGER_TAG, "MESSAGE");

  /**
   * Calls the current logger.d method
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void d(@NonNull String message) {
    if (logger == null) {
      makeSilentLogger();
    }

    logger.d(TAG, message);
  }

  /**
   * Calls the current logger.e method
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void e(@NonNull String message) {
    if (logger == null) {
      makeSilentLogger();
    }

    logger.e(TAG, message);
  }

  /**
   * Creates a TraceLog.e event with location and exception information.
   * e.g. className - methodName() : exceptions localized message.
   *
   * @param exception the exception that should be logged.
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void e(@NonNull Exception exception) {
    TraceLog.e(
        getLocationInformationFromStackTraceElement(new Throwable().getStackTrace())
            + exception.getLocalizedMessage());
  }

  /**
   * Creates a TraceLog.e event with location, exception, and additional information.
   * e.g. className - methodName() : exceptions localized message - additional information.
   *
   * @param exception the exception that should be logged.
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void e(@NonNull Exception exception, @NonNull String message) {
    TraceLog.e(
        getLocationInformationFromStackTraceElement(new Throwable().getStackTrace())
            + exception.getLocalizedMessage()
            + " - "
            + message);
  }

  /**
   * Calls the current logger.i method
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void i(@NonNull String message) {
    if (logger == null) {
      makeSilentLogger();
    }

    logger.i(TAG, message);
  }

  /**
   * Calls the current logger.v method
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void v(@NonNull String message) {
    if (logger == null) {
      makeSilentLogger();
    }

    logger.v(TAG, message);
  }

  /**
   * Calls the current logger.w method
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void w(@NonNull String message) {
    if (logger == null) {
      makeSilentLogger();
    }

    logger.w(TAG, message);
  }

  /**
   * Creates a TraceLog.w event with location and exception information.
   * e.g. className - methodName() : exceptions localized message.
   *
   * @param exception the exception that should be logged.
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void w(@NonNull Exception exception) {
    TraceLog.w(
        getLocationInformationFromStackTraceElement(new Throwable().getStackTrace())
            + exception.getLocalizedMessage());
  }

  /**
   * Creates a TraceLog.w event with location, exception, and additional information.
   * e.g. className - methodName() : exceptions localized message - an optional extra message
   * we provide.
   *
   * @param exception the exception that was thrown
   * @param message   additional context with the exception
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void w(@NonNull Exception exception,
                       @NonNull String message) {
    TraceLog.w(
        getLocationInformationFromStackTraceElement(new Throwable().getStackTrace())
            + exception.getLocalizedMessage()
            + " - "
            + message);
  }

  /**
   * Creates a TraceLog.w event with location, error, and additional information.
   * e.g. className - methodName() : error's localized message - an optional extra message we
   * provide.
   *
   * @param error   the error that was thrown
   * @param message additional context with the exception
   */
  @SuppressWarnings("checkstyle:MethodName")
  public static void w(@NonNull Error error,
                       @NonNull String message) {
    TraceLog.w(
        getLocationInformationFromStackTraceElement(new Throwable().getStackTrace())
            + error.getLocalizedMessage()
            + " - "
            + message);
  }
  //endregion

  /**
   * Creates a string of the calling class name and method from a stack trace.
   *
   * @param stackTraceElements provided from the location where the log was called.
   * @return "className - methodName() :" or "location unknown: " if the stack trace was empty.
   */
  @Nonnull
  private static String getLocationInformationFromStackTraceElement(
      @NonNull StackTraceElement[] stackTraceElements) {

    // https://stackoverflow.com/a/4332163
    if (stackTraceElements.length >= 2) {

      // we're using the second element because we want the place just before the TraceLog was
      // called.
      int interestedStackIndex = 1;

      // try to get the simple class name from the full class name
      String fullClassName = stackTraceElements[interestedStackIndex].getClassName();
      String simpleName = null;
      try {
        simpleName = Class.forName(fullClassName).getSimpleName();
      } catch (ClassNotFoundException e) {
        // nop
      }

      // if we couldn't get the simple class name, default to using the longer class name
      if (simpleName == null) {
        simpleName = fullClassName;
      }

      return simpleName
          + " - "
          + stackTraceElements[interestedStackIndex].getMethodName()
          + "() : ";
    } else {
      return "location unknown: ";
    }
  }


  /**
   * Creates a TraceLog.v log message only if the TraceSdk is also set to DEBUG_ENABLED to true.
   *
   * @param message the message to log.
   */
  public static void debugV(@NonNull String message) {
    if (TraceSdk.isDebugEnabled()) {
      TraceLog.v(message);
    }
  }
}

