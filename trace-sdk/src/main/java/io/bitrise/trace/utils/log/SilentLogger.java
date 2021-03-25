package io.bitrise.trace.utils.log;

import androidx.annotation.NonNull;

/**
 * An silent flavour logger, that will call not call any further methods and not display any output.
 */
public final class SilentLogger implements Logger {

    @Override
    public void d(@NonNull String tag, @NonNull String message) { }

    @Override
    public void e(@NonNull String tag, @NonNull String message) { }

    @Override
    public void i(@NonNull String tag, @NonNull String message) { }

    @Override
    public void v(@NonNull String tag, @NonNull String message) { }

    @Override
    public void w(@NonNull String tag, @NonNull String message) { }
}
