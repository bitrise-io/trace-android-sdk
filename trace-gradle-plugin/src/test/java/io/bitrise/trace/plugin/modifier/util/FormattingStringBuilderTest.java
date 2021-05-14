package io.bitrise.trace.plugin.modifier.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import io.bitrise.trace.plugin.util.FormattingStringBuilder;
import org.junit.Test;

/**
 * Test cases for {@link FormattingStringBuilder}.
 */
public class FormattingStringBuilderTest {

  /**
   * Array of lowercase Strings.
   */
  private final String[] lowerCaseStrings = new String[] {
      "alligator",
      "bear",
      "cat",
      "dolphin"
  };

  /**
   * Array of String with capital starting letters.
   */
  private final String[] titleCaseStrings = new String[] {
      "Alligator",
      "Bear",
      "Cat",
      "Dolphin"
  };

  /**
   * Array of uppercase Strings.
   */
  private final String[] upperCaseStrings = new String[] {
      "ALLIGATOR",
      "BEAR",
      "CAT",
      "DOLPHIN"
  };

  /**
   * Array of String with lowercase starting letters.
   */
  private final String[] inverseTitleCaseStrings = new String[] {
      "aLLIGATOR",
      "bEAR",
      "cAT",
      "dOPLHIN"
  };

  /**
   * Strings starting with lowercase letters will remain lowercase starting letter.
   */
  @Test
  public void formattingStringBuilderAppendTest_lowercase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult = formattingStringBuilder.append(lowerCaseStrings[0]).toString();
    final String expectedResult = lowerCaseStrings[0];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * Strings starting with uppercase letters will change to lowercase starting letter.
   */
  @Test
  public void formattingStringBuilderAppendTest_uppercase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult = formattingStringBuilder.append(titleCaseStrings[0]).toString();
    final String expectedResult = lowerCaseStrings[0];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * Non empty values that are not the first appended values, will have an uppercase starting
   * letter.
   */
  @Test
  public void formattingStringBuilderAppendTest_nonEmptyLowerCase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult = formattingStringBuilder.append(titleCaseStrings[0])
                                                       .append(lowerCaseStrings[1])
                                                       .toString();
    final String expectedResult = lowerCaseStrings[0] + titleCaseStrings[1];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * Non empty values that are not the first appended values, will have an uppercase starting
   * letter.
   */
  @Test
  public void formattingStringBuilderAppendTest_nonEmptyUpperCase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult = formattingStringBuilder.append("")
                                                       .append(upperCaseStrings[1])
                                                       .append(upperCaseStrings[2])
                                                       .append(upperCaseStrings[3])
                                                       .toString();
    final String expectedResult =
        lowerCaseStrings[1] + titleCaseStrings[2] + titleCaseStrings[3];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * {@link FormattingStringBuilder#appendWithoutFormatting(String)} should work exactly like
   * StringBuilder's append.
   */
  @Test
  public void formattingStringBuilderAppendWithoutFormattingTest() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult =
        formattingStringBuilder.appendWithoutFormatting(titleCaseStrings[0])
                               .appendWithoutFormatting(lowerCaseStrings[1])
                               .toString();
    final String expectedResult = titleCaseStrings[0] + lowerCaseStrings[1];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * When the first letter is lowercase it should be made uppercase, the rest should remain
   * untouched.
   */
  @Test
  public void formattingStringBuilderAppendWithCapitalizeFirstTest_lowerCase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult =
        formattingStringBuilder.appendWithCapitalizeFirst(lowerCaseStrings[0])
                               .appendWithCapitalizeFirst(inverseTitleCaseStrings[1])
                               .toString();
    final String expectedResult = titleCaseStrings[0] + upperCaseStrings[1];
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * When the first letter is lowercase it should be made uppercase, the rest should remain
   * untouched.
   */
  @Test
  public void formattingStringBuilderAppendWithCapitalizeFirstTest_upperCase() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    final String actualResult =
        formattingStringBuilder.appendWithCapitalizeFirst(titleCaseStrings[0])
                               .appendWithCapitalizeFirst(titleCaseStrings[1])
                               .toString();
    final String expectedResult = titleCaseStrings[0] + titleCaseStrings[1];
    assertThat(actualResult, is(expectedResult));
  }
}