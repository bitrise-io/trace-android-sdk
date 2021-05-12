package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Custom implementation of the {@link PrintWriter} class, to add different coloring to the
 * output and some extra
 * indentation. The coloring is based on the ANSI escape codes.
 *
 * @see
 * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">https://en.wikipedia.org/wiki/ANSI_escape_code</a>
 */
public class FunctionalTestWriter extends PrintWriter {

    private static final char[] indentation = new char[]{' ', ' ', ' '};
    private static final char[] escapeChar = new char[]{27};
    private final char[] coloringCode;
    private final char[] coloringEnd = concatEscapeChar("[39;49m".toCharArray());

    /**
     * Constructor for class, Requires the given OutputStream and the {@link PrintColor} which will be used.
     *
     * @param outputStream the OutputStream.
     * @param printColor   the given color.
     */
    public FunctionalTestWriter(@NonNull final OutputStream outputStream, @NonNull final PrintColor printColor) {
        super(outputStream);
        this.coloringCode = createColorCode(printColor);
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
        final Rgb rgb = printColor.getRgb();
        return String.format("[38;2;%s;%s;%sm", rgb.getRed(), rgb.getGreen(), rgb.getBlue()).toCharArray();
    }

    @Override
    public void write(@NonNull final char[] chars, final int i, final int i1) {
        final char[] charsToWrite = Arrays.copyOfRange(chars, i, i + i1);
        final char[] coloredChars = color(charsToWrite);
        final char[] indentedAndColoredChars = addIndent(coloredChars);
        final char[] toPrint = concatCharArrays(indentedAndColoredChars, coloringEnd);
        super.write(toPrint, 0, toPrint.length);
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
     * Enum class for different colors. The color RGB values are in align with the Bitkit.
     *
     * @see
     * <a href="https://bitkit.netlify.app/documentation/materials/colors">https://bitkit.netlify.app/documentation/materials/colors</a>
     */
    public enum PrintColor {
        RED4(new Rgb(238, 0, 59)),
        AQUA4(new Rgb(14, 199, 186));

        private final Rgb rgb;

        PrintColor(@NonNull final Rgb rgb) {
            this.rgb = rgb;
        }

        public Rgb getRgb() {
            return rgb;
        }
    }

    /**
     * Data class for representing a RGB color.
     */
    public static class Rgb {

        private final int red;
        private final int green;
        private final int blue;

        public Rgb(final int red, final int green, final int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }
    }
}