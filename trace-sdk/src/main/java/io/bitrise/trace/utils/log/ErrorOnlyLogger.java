package io.bitrise.trace.utils.log;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * An error only logger, it will ONLY use android.util.Log.e and ignore all other log type messages.
 */
public final class ErrorOnlyLogger implements Logger {

    @Override
    public void d(@NonNull String tag, @NonNull String message) { }

    @Override
    public void e(@NonNull String tag, @NonNull String message) {
        Log.e(tag, message);
    }

    @Override
    public void i(@NonNull String tag, @NonNull String message) { }

    @Override
    public void v(@NonNull String tag, @NonNull String message) { }

    @Override
    public void w(@NonNull String tag, @NonNull String message) { }
}
