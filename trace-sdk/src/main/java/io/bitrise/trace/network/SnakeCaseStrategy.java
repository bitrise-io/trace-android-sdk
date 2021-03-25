package io.bitrise.trace.network;

import androidx.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * A {@link FieldNamingStrategy} implementation to convert JSON field names to snake case. Similar to
 * {@link com.google.gson.FieldNamingPolicy#LOWER_CASE_WITH_UNDERSCORES}, but this removes the underscore char from
 * the end if present.
 */
public class SnakeCaseStrategy implements FieldNamingStrategy {

    /**
     * Separates word, if it was written with camel case. Copy of
     * {@link FieldNamingPolicy#LOWER_CASE_WITH_UNDERSCORES#separateCamelCase(String, String)}
     *
     * @param name      the field to work on.
     * @param separator the separator char.
     * @return the new word, or the same if it was not formatted with camel case.
     */
    @NonNull
    static String separateCamelCase(@NonNull final String name, @NonNull final String separator) {
        final StringBuilder translation = new StringBuilder();
        int i = 0;

        for (final int length = name.length(); i < length; ++i) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }

            translation.append(character);
        }

        return translation.toString();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copy of {@link FieldNamingPolicy#LOWER_CASE_WITH_UNDERSCORES#translateName(Field)} with addition to remove
     * underscore from endings.
     */
    @Override
    @NonNull
    public String translateName(@NonNull final Field field) {
        final String s = separateCamelCase(field.getName(), "_").toLowerCase(Locale.ENGLISH);
        if (s.charAt(s.length() - 1) == '_') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
}
