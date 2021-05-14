package io.bitrise.trace.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.protobuf.ByteString;
import java.nio.charset.Charset;
import org.junit.Test;

/**
 * Checks that the converting methods of {@link ByteStringConverter} works as intended.
 */
public class ByteStringConverterTest {

  /**
   * Check that converting a {@link ByteString} to String, then back to ByteString it should
   * not change it.
   */
  @Test
  public void byteStringConvert_shouldBeEqual() {
    final ByteString expectedValue = ByteString.copyFrom("text", Charset.defaultCharset());
    final String stringValue = ByteStringConverter.toString(expectedValue);
    final ByteString actualValue = ByteStringConverter.toByteString(stringValue);
    assertThat(actualValue, equalTo(expectedValue));
  }
}