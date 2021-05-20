package io.bitrise.trace.plugin.configuration;

import androidx.annotation.NonNull;
import io.bitrise.trace.plugin.TraceConstants;
import java.util.Objects;

/**
 * Data class for build configuration, the
 * {@link io.bitrise.trace.plugin.TraceConstants#BITRISE_ADDONS_CONFIGURATION_FILE} file will be
 * parsed to this Object.
 */
public class BuildConfiguration {

  /**
   * The key for the {@link BuildConfiguration} token value to store in the BuildConfig of the
   * Android project.
   */
  public static final String TRACE_TOKEN_KEY = "BITRISE_TRACE_TOKEN";

  /**
   * The token that will be used for the authentication.
   */
  @NonNull
  private String token;

  /**
   * The version of this BuildConfiguration file.
   */
  @NonNull
  private String version;

  /**
   * Constructor for class.
   *
   * @param token   the token.
   * @param version the version.
   */
  public BuildConfiguration(@NonNull final String token, @NonNull final String version) {
    if (token == null) {
      throw new IllegalStateException(
          String.format("Token is missing from %s!",
              TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE));
    }
    if (version == null) {
      throw new IllegalStateException(
          String.format("Version is missing from %s",
              TraceConstants.BITRISE_ADDONS_CONFIGURATION_FILE));
    }
    this.token = token;
    this.version = version;
  }

  /**
   * Constructor for validating parsed JSON value, as GSON has no indicator for required
   * fields, which should not be {@code null}.
   *
   * @param buildConfiguration the BuildConfiguration to validate.
   */
  public BuildConfiguration(@NonNull final BuildConfiguration buildConfiguration) {
    this(buildConfiguration.getToken(), buildConfiguration.getVersion());
  }

  @NonNull
  public String getToken() {
    return token;
  }

  public void setToken(@NonNull final String token) {
    Objects.requireNonNull(token);
    this.token = token;
  }

  @NonNull
  public String getVersion() {
    return version;
  }

  public void setVersion(@NonNull final String version) {
    Objects.requireNonNull(version);
    this.version = version;
  }
}
