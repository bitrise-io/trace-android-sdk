package io.bitrise.trace.plugin.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Data class that holds a version that follows semantic versioning conventions.
 *
 * @see <a href="https://semver.org">Semantic versioning</a>
 */
public class Version implements Comparable<Version> {

  private final String version;
  private final int patch;
  private final int minor;
  private final int major;

  /**
   * Constructor for class.
   *
   * @param version the String representation of the version.
   */
  public Version(@NonNull final String version) {
    if (!version.matches("([0-9]+)\\.([0-9]+)\\.([0-9]+)")) {
      throw new IllegalArgumentException(String.format("Invalid version format: %1$s", version));
    }
    final String[] versionCodes = version.split("\\.");
    this.major = Integer.parseInt(versionCodes[0]);
    this.minor = Integer.parseInt(versionCodes[1]);
    this.patch = Integer.parseInt(versionCodes[2]);
    this.version = version;
  }

  /**
   * Checks that this Version is supported or not. A version is considered to be supported,
   * when it's major version number is equal to the references major version number.
   *
   * @param supportedVersionString the reference version, to compare with.
   * @return {@code true} if yes, {@code false} otherwise.
   */
  public boolean isSupported(@NonNull final String supportedVersionString) {
    final Version supportedVersion = new Version(supportedVersionString);
    return supportedVersion.major == this.major;
  }

  /**
   * A version number should be considered greater, if the major version number is higher. If
   * they are equal, the the minor version number is checked, if the minor version numbers are
   * equal too, then the patch version is checked. If the patch versions are equal too, then the
   * two versions considered equal.
   *
   * @param that the Version to compare to.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   * or greater than the specified object.
   */
  @Override
  public int compareTo(@Nullable final Version that) {
    if (that == null) {
      return 1;
    }
    final int major = Integer.compare(this.major, that.major);
    if (major != 0) {
      return major;
    }
    final int minor = Integer.compare(this.minor, that.minor);
    if (minor != 0) {
      return minor;
    }
    return Integer.compare(this.patch, that.patch);
  }

  @Override
  public boolean equals(@Nullable final Object that) {
    if (this == that) {
      return true;
    }
    if (that == null) {
      return false;
    }
    if (this.getClass() != that.getClass()) {
      return false;
    }
    return this.compareTo((Version) that) == 0;
  }

  @Override
  public final String toString() {
    return this.version;
  }
}