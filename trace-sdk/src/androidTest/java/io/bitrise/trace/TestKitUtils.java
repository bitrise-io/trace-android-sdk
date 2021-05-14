package io.bitrise.trace;

import androidx.annotation.NonNull;
import io.bitrise.trace.network.NetworkCommunicator;
import io.bitrise.trace.network.TestNetworkClient;

/**
 * Utility class for running TestKit tests.
 */
public class TestKitUtils {

  private TestKitUtils() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  //region getNetworkCommunicatorWithToken() methods. For declaring the valid token in the
  // TestKit please see the
  // README for details.

  /**
   * Gets a {@link NetworkCommunicator} with the given token, which should be valid.
   *
   * @return the NetworkCommunicator.
   */
  @NonNull
  public static NetworkCommunicator getNetworkCommunicatorWithValidToken() {
    return TestNetworkClient.getTestKitCommunicator("70a14e32-97b1-4b6d-b4bc-ff592c780325");
  }

  /**
   * Gets a {@link NetworkCommunicator} with the given token, which should be "unexpected".
   *
   * @return the NetworkCommunicator.
   */
  @NonNull
  public static NetworkCommunicator getNetworkCommunicatorWithUnexpectedToken() {
    return TestNetworkClient.getTestKitCommunicator("unexpected");
  }

  /**
   * Gets a {@link NetworkCommunicator} with the given token, which should be invalid.
   *
   * @return the NetworkCommunicator.
   */
  @NonNull
  public static NetworkCommunicator getNetworkCommunicatorWithInvalidToken() {
    return TestNetworkClient.getTestKitCommunicator("0123456789abcdef");
  }
  //endregion
}
