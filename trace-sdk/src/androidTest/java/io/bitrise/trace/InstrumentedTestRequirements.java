package io.bitrise.trace;

import static org.junit.Assume.assumeTrue;

import android.os.Build;
import io.bitrise.trace.data.collector.view.FragmentStateDataListener;
import org.junit.AssumptionViolatedException;

/**
 * Provides different requirements for test cases to run in cases. For example: when a test case
 * should not run on a given API level.
 */
public class InstrumentedTestRequirements {

  private static final String NETWORK_POLICY_CLEARTEXT_LIMITATION_MESSAGE =
      "Can only run on API Level 27 or lower "
          + "because of Network policy limitations on cleartext traffic!";
  private static final String API_LEVEL_CPU_REQUIREMENT_MESSAGE =
      "proc/stat file requires root access on "
          + "API level 26 and above, without it we cannot access files and results will be null!";
  private static final String API_LEVEL_CPU_REQUIREMENT_FAIL_MESSAGE =
      "proc/stat file requires root access on "
          + "API level 26 and above, this is required to check the cases when do not have "
          + "permission to access these files!";
  private static final String API_LEVEL_DEPRECATED_FRAGMENT_MESSAGE =
      "Requires at least API level 26 to run!";

  private InstrumentedTestRequirements() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  /**
   * Checks if the current API level is suitable for TestKit tests or not. A test is skipped
   * due to network policy limitations. See more on project level README. Cleartext communication
   * is using the unencrypted HTTP protocol instead of HTTPS.
   *
   * @throws AssumptionViolatedException when the current API level is not proper for the
   *                                     TestKit test.
   * @see
   * <a href="https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted">https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted</a>
   */
  public static void assumeTestKitApiLevel() throws AssumptionViolatedException {
    assumeTrue(NETWORK_POLICY_CLEARTEXT_LIMITATION_MESSAGE,
        Build.VERSION.SDK_INT < Build.VERSION_CODES.P);
  }

  /**
   * Checks if the current API level is suitable for CPU usage tests or not. By default you do
   * not have root permission on Android emulators, and starting from
   * {@link Build.VERSION_CODES#O} accessing /proc/stat files requires it.
   *
   * @throws AssumptionViolatedException when the current API level is not proper.
   */
  public static void assumeCpuApiLevel() throws AssumptionViolatedException {
    assumeTrue(API_LEVEL_CPU_REQUIREMENT_MESSAGE,
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O);
  }

  /**
   * Checks the opposite of {@link #assumeCpuApiLevel()} for testing wht happens, when we do
   * not have access to /proc/stat file during CPU data collection. Tests which would require
   * this should use it.
   *
   * @throws AssumptionViolatedException when the current API level is not proper for the TestKit
   *                                     test.
   */
  public static void assumeCpuApiLevelFail() throws AssumptionViolatedException {
    assumeTrue(API_LEVEL_CPU_REQUIREMENT_FAIL_MESSAGE,
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);
  }

  /**
   * Checks that the API level is at least 26, as call to getDeprecatedFragmentCallbackTracker()
   * method in{@link FragmentStateDataListener} requires it.
   *
   * @throws AssumptionViolatedException when the current API level is not proper for the test.
   */
  public static void assumeDeprecatedFragmentLevel() throws AssumptionViolatedException {
    assumeTrue(API_LEVEL_DEPRECATED_FRAGMENT_MESSAGE,
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);
  }
}
