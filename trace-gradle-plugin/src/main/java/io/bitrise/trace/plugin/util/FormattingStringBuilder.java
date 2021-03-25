package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Decorator class for StringBuilder.
 */
public class FormattingStringBuilder {

    /**
     * The StringBuilder for formatted string appending.
     */
    private StringBuilder stringBuilder;

    /**
     * Constructor for class.
     */
    public FormattingStringBuilder() {
        this.stringBuilder = new StringBuilder();
    }

    /**
     * Formats and appends the given String value to the builder. The formatting will make
     * the text lowercase, optionally capitalizes the first letter if it is not the first
     * value in the builder.
     *
     * @param value the String value.
     * @return this builder.
     */
    @NonNull
    public FormattingStringBuilder append(@Nullable String value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        value = value.toLowerCase();
        if (stringBuilder.length() > 0) {
            appendWithCapitalizeFirst(value);
        } else {
            stringBuilder.append(value);
        }
        return this;
    }

    /**
     * Formats the given String and appends to the builder. The formatting will capitalize only
     * the first letter, will not change other letters.
     *
     * @param value the String value.
     * @return this builder.
     */
    @NonNull
    public FormattingStringBuilder appendWithCapitalizeFirst(@Nullable String value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        value = value.substring(0, 1).toUpperCase() + value.substring(1);
        stringBuilder.append(value);
        return this;
    }

    /**
     * Appends the given String value to the builder.
     *
     * @param value the String value.
     * @return this builder.
     */
    @NonNull
    public FormattingStringBuilder appendWithoutFormatting(@Nullable final String value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        stringBuilder.append(value);
        return this;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
