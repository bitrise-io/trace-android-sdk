package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Custom implementation of the {@link PrintWriter} class, to add different coloring to the output and some extra
 * indentation.
 */
public class FunctionalTestWriter extends PrintWriter {

    private static final char[] indentation = new char[]{' ', ' ', ' '};
    private static final char[] escapeChar = new char[]{27};
    private final char[] coloringCode;
    private final int extraLength;

    /**
     * Constructor for class, Requires the given OutputStream and the {@link PrintColor} which will be used.
     *
     * @param outputStream the OutputStream.
     * @param printColor   the given color.
     */
    public FunctionalTestWriter(@NonNull final OutputStream outputStream, @NonNull final PrintColor printColor) {
        super(outputStream);
        this.coloringCode = createColorCode(printColor);
        this.extraLength = indentation.length + coloringCode.length + escapeChar.length;
    }

    /**
     * Inserts to the start the escape char.
     *
     * @param chars the given chars to escape.
     * @return the given chars with the escape char.
     */
    @NonNull
    private static char[] concatEscapeChar(@NonNull final char[] chars) {
        return concatCharArrays(escapeChar, chars);
    }

    /**
     * Concatenates two arrays of chars.
     *
     * @param first  the first array.
     * @param second the second array.
     * @return the concatenated result.
     */
    @NonNull
    private static char[] concatCharArrays(@NonNull final char[] first, @NonNull final char[] second) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(first);
        stringBuilder.append(second);
        return stringBuilder.toString().toCharArray();
    }

    /**
     * Creates a color code from the given {@link PrintColor}.
     *
     * @param printColor the given PrintColor.
     * @return the char array representation of the given color.
     */
    @NonNull
    private static char[] createColorCode(@NonNull final PrintColor printColor) {
        return ("[" + printColor.getColorCode() + "m").toCharArray();
    }

    /**
     * Resets the console to the default color.
     */
    public static void reset() {
        System.out.println(concatEscapeChar(createColorCode(PrintColor.DEFAULT)));
    }

    @Override
    public void write(@NonNull final char[] chars, final int i, final int i1) {
        final char[] coloredChars = color(chars);
        final char[] output = addIndent(coloredChars);
        super.write(output, i, i1 + extraLength);
    }

    /**
     * Inserts to the start of the given char array the {@link #indentation}.
     *
     * @param chars the given chars.
     * @return the indented array of chars.
     */
    @NonNull
    private char[] addIndent(@NonNull final char[] chars) {
        return concatCharArrays(indentation, chars);
    }

    /**
     * Inserts to the start the coloring chars to a given array of chars.
     *
     * @param chars the given array of chars.
     * @return the given text with the coloring chars.
     */
    @NonNull
    private char[] color(@NonNull final char[] chars) {
        final char[] escaped = concatEscapeChar(coloringCode);
        return concatCharArrays(escaped, chars);
    }

    /**
     * Enum class for different colors. This is based on the ANSI escape codes.
     *
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">https://en.wikipedia.org/wiki/ANSI_escape_code</a>
     */
    public enum PrintColor {
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        MAGENTA(35),
        CYAN(36),
        WHITE(36),
        DEFAULT(39);

        private final int colorCode;

        PrintColor(final int colorCode) {
            this.colorCode = colorCode;
        }

        public int getColorCode() {
            return colorCode;
        }
    }
}