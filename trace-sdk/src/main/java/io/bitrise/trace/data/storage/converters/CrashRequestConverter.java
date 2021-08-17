package io.bitrise.trace.data.storage.converters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.network.NetworkClient;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;

/**
 * Converter for {@link CrashRequest} objects to save them properly in the data storage.
 */
public class CrashRequestConverter {
  private static final Gson gson = NetworkClient.getGson();

  /**
   * Converts the given String object into a {@link CrashRequest}. Returns null if there are any
   * issues with the conversion.
   *
   * @param value the string to convert to a CrashRequest
   * @return either a CrashRequest object, or null if there was an issue.
   */
  @Nullable
  @TypeConverter
  public static CrashRequest toCrashRequest(@NonNull final String value) {
    try {

      // if this fails, we return null
      return gson.fromJson(value, CrashRequest.class);

    } catch (Exception e) {
      TraceLog.w(e);
      TraceLog.d(String.format(LogMessageConstants.CONVERTER_FAILED_WITH_VALUE, value));
    }
    return null;
  }

  /**
   * Converts a given {@link CrashRequest} into a String.
   *
   * @param request the CrashRequest object to convert to a String.
   * @return the String value of the CrashRequest.
   */
  @TypeConverter
  public static String toString(@NonNull final CrashRequest request) {
    return gson.toJson(request, CrashRequest.class);
  }
}
