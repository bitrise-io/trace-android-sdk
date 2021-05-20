package io.bitrise.trace.plugin.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import androidx.annotation.NonNull;
import com.google.gson.JsonSyntaxException;
import io.bitrise.trace.plugin.TraceConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import org.junit.Test;

/**
 * Test class for {@link BuildConfigurationManager}.
 */
public class BuildConfigurationManagerTest {

  private final String resourceDir = "src/test/resources";

  /**
   * Gets a test resource File from the resources directory with the given name.
   *
   * @param fileName the name of the File.
   * @return the relative path of the File.
   */
  private String getTestFilePath(@NonNull final String fileName) {
    return resourceDir + "/" + fileName;
  }

  /**
   * Tests {@link BuildConfigurationManager#parseBuildConfiguration(File)} with a valid JSON file.
   *
   * @throws FileNotFoundException when the JSON file is not found.
   */
  @Test
  public void parseBuildConfiguration_validShouldBeParsed() throws FileNotFoundException {
    final String path = getTestFilePath(TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE);
    final BuildConfiguration buildConfiguration =
        BuildConfigurationManager.parseBuildConfiguration(new File(path));
    assertThat(buildConfiguration.getToken(), equalTo("token_abcdef123456"));
    assertThat(buildConfiguration.getVersion(), equalTo("1.0.0"));
  }

  /**
   * Tests {@link BuildConfigurationManager#parseBuildConfiguration(File)} with an invalid JSON
   * file.
   *
   * @throws FileNotFoundException when the JSON file is not found.
   */
  @Test(expected = JsonSyntaxException.class)
  public void parseBuildConfiguration_invalidShouldThrowException() throws FileNotFoundException {
    final String path = getTestFilePath("bitrise-addons-configuration-invalid.json");
    BuildConfigurationManager.parseBuildConfiguration(new File(path));
  }

  /**
   * Tests {@link BuildConfigurationManager#parseBuildConfiguration(File)} with a different
   * JSON file.
   *
   * @throws FileNotFoundException when the JSON file is not found.
   */
  @Test(expected = IllegalStateException.class)
  public void parseBuildConfiguration_differentShouldParseNullValues()
      throws FileNotFoundException {
    final String path = getTestFilePath("not-bitrise-addons-configuration.json");
    BuildConfigurationManager.parseBuildConfiguration(new File(path));
  }

  /**
   * Tests {@link BuildConfigurationManager#parseBuildConfiguration(File)} with wrong path. In
   * this case FileNotFoundException should be thrown.
   *
   * @throws FileNotFoundException when the JSON file is not found.
   */
  @Test(expected = FileNotFoundException.class)
  public void parseBuildConfiguration_wrongPathException() throws FileNotFoundException {
    final String path = getTestFilePath("wrong_path");
    BuildConfigurationManager.parseBuildConfiguration(new File(path));
  }

  /**
   * Checks that the {@link BuildConfigurationManager#getInstance(String)} returns a Singleton
   * value.
   */
  @Test
  public void getInstance_assertIsSingleton() {
    final BuildConfigurationManager actualValue =
        BuildConfigurationManager.getInstance(resourceDir);
    final BuildConfigurationManager newValue = BuildConfigurationManager.getInstance(resourceDir);
    assertThat(actualValue, sameInstance(newValue));
  }

  /**
   * When {@link BuildConfigurationManager#checkBuildConfigVersion(String)} is called with
   * older supported version, then the config file should throw UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void checkBuildConfigVersion_lowerMajorVersionThrows() {
    final BuildConfigurationManager buildConfigurationManager =
        BuildConfigurationManager.getInstance(resourceDir);
    buildConfigurationManager.checkBuildConfigVersion("0.1.0");
    buildConfigurationManager.checkBuildConfigVersion("2.0.0");
  }

  /**
   * When {@link BuildConfigurationManager#checkBuildConfigVersion(String)} is called with
   * newer supported version, then the config file should throw UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void checkBuildConfigVersion_newerMajorVersionThrows() {
    final BuildConfigurationManager buildConfigurationManager =
        BuildConfigurationManager.getInstance(resourceDir);
    buildConfigurationManager.checkBuildConfigVersion("2.0.0");
  }

  /**
   * This test checks rhe integrity of {@link BuildConfiguration}. If this fails, this means,
   * that the config file format has changed, and
   * {@link TraceConstants#TRACE_CONFIGURATION_SUPPORTED_FILE_VERSION} should be updated. After
   * that this file can be updated to make it pass.
   */
  @Test
  public void checkVersionChange() {
    final Field[] fields = BuildConfiguration.class.getFields();
    final Field[] declaredFields = BuildConfiguration.class.getDeclaredFields();
    final String tokenKeyString =
        "public static final java.lang.String io.bitrise.trace.plugin.configuration"
            + ".BuildConfiguration.TRACE_TOKEN_KEY";
    final String tokenFieldString =
        "private java.lang.String io.bitrise.trace.plugin.configuration"
            + ".BuildConfiguration.token";
    final String versionFieldString =
        "private java.lang.String io.bitrise.trace.plugin.configuration"
            + ".BuildConfiguration.version";

    assertThat(fields.length, is(1));
    assertThat(fields[0].toString(), is(tokenKeyString));
    assertThat(declaredFields.length, is(3));
    assertThat(declaredFields[0].toString(), is(tokenKeyString));
    assertThat(declaredFields[1].toString(), is(tokenFieldString));
    assertThat(declaredFields[2].toString(), is(versionFieldString));
  }
}