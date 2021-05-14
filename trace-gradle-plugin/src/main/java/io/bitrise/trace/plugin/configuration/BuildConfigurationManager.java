package io.bitrise.trace.plugin.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.builder.model.ClassField;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.bitrise.trace.plugin.TraceConstants;
import io.bitrise.trace.plugin.modifier.BuildHelper;
import io.bitrise.trace.plugin.util.TraceException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import org.gradle.api.Project;

/**
 * Responsible for the compile time configurations.
 */
@Singleton
public class BuildConfigurationManager {

  /**
   * The instance for this Singleton.
   */
  @Nullable
  private static volatile BuildConfigurationManager buildConfigurationManager;

  /**
   * The {@link BuildConfiguration} retrieved from the configuration file named
   * {@link TraceConstants#BITRISE_ADDONS_CONFIGURATION_FILE}.
   */
  @NonNull
  private final BuildConfiguration buildConfiguration;

  /**
   * Constructor for the class. Checks if the configuration file exists, can be parsed and the
   * the version of it is supported. Parses the configuration to the {@link #buildConfiguration}.
   *
   * @param rootDir the root directory of the given Android {@link Project}.
   */
  private BuildConfigurationManager(@NonNull final String rootDir) {
    final File jsonFile =
        new File(rootDir + "/" + TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE);
    try {
      buildConfiguration = parseBuildConfiguration(jsonFile);
    } catch (final FileNotFoundException e) {
      throw new TraceException.TraceConfigFileNotFoundException(
          TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE, rootDir);
    } catch (final JsonSyntaxException | JsonIOException je) {
      throw new TraceException.TraceConfigFileInvalidException(
          TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE, je.getLocalizedMessage());
    }
    checkBuildConfigVersion(buildConfiguration.getVersion());
  }

  /**
   * Gets the singleton instance of the BuildConfigurationManager.
   *
   * @param rootDir the root directory of the given {@link Project}.
   * @return the manager.
   */
  @NonNull
  public static synchronized BuildConfigurationManager getInstance(@NonNull final String rootDir) {
    if (buildConfigurationManager == null) {
      buildConfigurationManager = new BuildConfigurationManager(rootDir);
    }
    return buildConfigurationManager;
  }

  /**
   * Parses the given File to a {@link BuildConfiguration}.
   *
   * @param file the File.
   * @return the BuildConfiguration.
   * @throws FileNotFoundException if the given is not found.
   * @throws JsonIOException       if there was a problem reading from the Reader.
   * @throws JsonSyntaxException   if json is not a valid representation for an object of type.
   */
  @NonNull
  static BuildConfiguration parseBuildConfiguration(@NonNull final File file)
      throws FileNotFoundException, JsonIOException, JsonSyntaxException {
    final Gson gson = new Gson();
    return new BuildConfiguration(gson.fromJson(new FileReader(file),
        BuildConfiguration.class));
  }

  /**
   * Applies the parsed {@link BuildConfiguration} to the given Android {@link Project}.
   *
   * @param baseExtension the {@link AppExtension} to add the BuildConfig.
   */
  public void applyBuildConfiguration(@NonNull final BaseExtension baseExtension) {
    addTokenToProject(baseExtension);
  }

  /**
   * Adds the token to the given {@link AppExtension}.
   *
   * @param baseExtension the AppExtension.
   */
  private void addTokenToProject(@NonNull final BaseExtension baseExtension) {
    final ClassField buildConfigClassField =
        BuildHelper.createBuildConfigStringClassField(BuildConfiguration.TRACE_TOKEN_KEY,
            buildConfiguration.getToken());

    final ClassField resValueClassField =
        BuildHelper.createResValueStringClassField(BuildConfiguration.TRACE_TOKEN_KEY,
            buildConfiguration.getToken());

    baseExtension.getBuildTypes().forEach(buildType -> {
      buildType.addBuildConfigField(buildConfigClassField);
      buildType.addResValue(resValueClassField);
    });
  }

  /**
   * Checks if the version of the config file is supported by the plugin or not. Throws
   * UnsupportedOperation if not.
   *
   * @param version the version of the config file.
   */
  void checkBuildConfigVersion(@NonNull final String version) {
    final Version configFileVersion = new Version(version);
    if (!configFileVersion.isSupported(TraceConstants.TRACE_CONFIGURATION_SUPPORTED_FILE_VERSION)) {
      throw new UnsupportedOperationException(
          String.format("Current configuration file's version (%1$s) is not supported. Please use"
              + " same major version of config file or update the Trace plugin version!", version));
    }
  }

  /**
   * Gets the list of permission that will be needed for our DataSources.
   *
   * @return the array of required permissions.
   */
  public List<String> getRequiredPermissions() {
    // TODO add implementation
    return Collections.emptyList();
  }
}
