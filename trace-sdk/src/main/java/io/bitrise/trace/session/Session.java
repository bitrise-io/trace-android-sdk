package io.bitrise.trace.session;

import androidx.annotation.NonNull;

import java.security.SecureRandom;

import io.azam.ulidj.ULID;

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
     * Gets the ULID of the Session.
     *
     * @return the ULID.
     */
    @NonNull
    public String getUlid() {
        return ulid;
    }
}
