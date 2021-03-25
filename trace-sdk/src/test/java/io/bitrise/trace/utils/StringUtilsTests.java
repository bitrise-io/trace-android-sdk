package io.bitrise.trace.utils;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link StringUtils}.
 */
public class StringUtilsTests {

    @Test
    public void join_withThreeItems_commaSeparator() {
        final String[] input = {"Apple", "Banana", "Clementine"};
        final String output = StringUtils.join(input, ",");
        assertEquals("Apple,Banana,Clementine", output);
    }

    @Test
    public void join_withThreeItems_noSeparator() {
        final String[] input = {"Date", "Elderberry", "Fig"};
        final String output = StringUtils.join(input, "");
        assertEquals("DateElderberryFig", output);
    }

    @Test
    public void join_withOneItems_commaSeparator() {
        final String[] input = {"Grapefruit"};
        final String output = StringUtils.join(input, ",");
        assertEquals("Grapefruit", output);
    }

    @Test
    public void join_withNoItems_commaSeparator() {
        final String[] input = {};
        final String output = StringUtils.join(input, ",");
        assertEquals("", output);
    }

    @Test
    public void join_list_withThreeItems_commaSeparator() {
        final List<String> input = Arrays.asList("Apple", "Banana", "Clementine");
        final String output = StringUtils.join(input, ",");
        assertEquals("Apple,Banana,Clementine", output);
    }

    @Test
    public void join_list_withThreeItems_noSeparator() {
        final List<String> input = Arrays.asList("Date", "Elderberry", "Fig");
        final String output = StringUtils.join(input, "");
        assertEquals("DateElderberryFig", output);
    }

    @Test
    public void join_list_withOneItems_commaSeparator() {
        final List<String> input = Collections.singletonList("Grapefruit");
        final String output = StringUtils.join(input, ",");
        assertEquals("Grapefruit", output);
    }

    @Test
    public void join_list_withNoItems_commaSeparator() {
        final List<String> input = new ArrayList<>();
        final String output = StringUtils.join(input, ",");
        assertEquals("", output);
    }
}
