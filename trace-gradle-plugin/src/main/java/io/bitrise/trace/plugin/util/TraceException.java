package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;

/**
 * TraceExceptions are used to express and describe some kind of business type exceptions.
 */
public class TraceException {

  /**
   * Used when the TraceConfig file is not found in the correct location.
   */
  public static class TraceConfigFileNotFoundException extends IllegalStateException {

    /**
     * Constructor for class.
     *
     * @param fileName      the name if the File.
     * @param rootDirectory the root directory.
     */
    public TraceConfigFileNotFoundException(@NonNull final String fileName,
                                            @NonNull final String rootDirectory) {
      super(String.format(
          "Could not find the JSON configuration file in project root. Please make sure %1$s can "
              + "be found in %2$s", fileName, rootDirectory));
    }
  }

  /**
   * Used when the TraceConfig file has an issue being read.
   */
  public static class TraceConfigFileInvalidException extends IllegalStateException {

    /**
     * Constructor for class.
     *
     * @param fileName     the name of the File.
     * @param errorMessage the error message to display.
     */
    public TraceConfigFileInvalidException(@NonNull final String fileName,
                                           @NonNull final String errorMessage) {
      super(String.format("Invalid %1$s : %2$s", fileName, errorMessage));
    }
  }
}
