package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;

/**
 * Responsible for creating custom messages which will be sent to the Trace server. Has the same
 * levels of log what Android has:
 * <ul>
 * <li>VERBOSE</li>
 * <li>DEBUG</li>
 * <li>INFORMATION</li>
 * <li>WARNING</li>
 * <li>ERROR</li>
 * </ul>
 *
 * @see <a href="https://developer.android.com/reference/android/util/Log">Android Log</a>
 */
public final class TraceLogCollector {

    /**
     * Constructor for class. Should not be instantiated as it has only static members. Throws
     * UnsupportedOperationException if instantiated with reflection.
     */
    private TraceLogCollector() {
        throw new UnsupportedOperationException("Private constructor for class");
    }

    /**
     * Creates a Log message at VERBOSE level.
     *
     * @param tag     the Tag for the message.
     * @param message the message.
     */
    public static void v(@NonNull final String tag, @NonNull final String message) {
        // TODO add implementation.
    }

    /**
     * Creates a Log message at DEBUG level.
     *
     * @param tag     the Tag for the message.
     * @param message the message.
     */
    public static void d(@NonNull final String tag, @NonNull final String message) {
        // TODO add implementation.
    }

    /**
     * Creates a Log message at INFORMATION level.
     *
     * @param tag     the Tag for the message.
     * @param message the message.
     */
    public static void i(@NonNull final String tag, @NonNull final String message) {
        // TODO add implementation.
    }

    /**
     * Creates a Log message at WARNING level.
     *
     * @param tag     the Tag for the message.
     * @param message the message.
     */
    public static void w(@NonNull final String tag, @NonNull final String message) {
        // TODO add implementation.
    }

    /**
     * Creates a Log message at ERROR level.
     *
     * @param tag     the Tag for the message.
     * @param message the message.
     */
    public static void e(@NonNull final String tag, @NonNull final String message) {
        // TODO add implementation.
    }
}
