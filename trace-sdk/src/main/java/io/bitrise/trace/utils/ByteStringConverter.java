package io.bitrise.trace.utils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;
import com.google.protobuf.ByteString;
import java.nio.charset.Charset;

/**
 * Converter for {@link ByteString} to store it in
 * {@link io.bitrise.trace.data.storage.TraceDatabase}.
 */
public class ByteStringConverter {

  /**
   * Converts the given String value to a {@link ByteString}.
   *
   * @param value the value to convert.
   * @return the ByteString for the given value.
   */
  @NonNull
  @TypeConverter
  public static ByteString toByteString(@NonNull final String value) {
    return ByteString.copyFrom(value, Charset.defaultCharset());
  }

  /**
   * Converts the given {@link ByteString} to a String value.
   *
   * @param byteString the ByteString to convert.
   * @return the String value of the given ByteString.
   */
  @TypeConverter
  public static String toString(@NonNull final ByteString byteString) {
    return byteString.toString(Charset.defaultCharset());
  }
}
