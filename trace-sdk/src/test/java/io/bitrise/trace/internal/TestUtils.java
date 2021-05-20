package io.bitrise.trace.internal;

import static androidx.annotation.RestrictTo.Scope.TESTS;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Singleton class for utility methods for unit tests.
 */
@RestrictTo(TESTS)
public class TestUtils {

  private TestUtils() {
    throw new UnsupportedOperationException("Private constructor for TestUtils!");
  }

  /**
   * Gets the content of a file and removes any whitespace.
   *
   * @param filePath the location of the file to read.
   *                 e.g. "src/test/resources/io/bitrise/trace/network/metric_request.json"
   * @return the String content of the file.
   * @throws IOException e.g. if the file cannot be found.
   */
  @NonNull
  public static String getJsonContentRemovingWhitespace(@NonNull final String filePath)
      throws IOException {
    final File file = new File(filePath);
    final String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    // regex from https://stackoverflow.com/a/9584469
    return json.replaceAll("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)", "");
  }

}
