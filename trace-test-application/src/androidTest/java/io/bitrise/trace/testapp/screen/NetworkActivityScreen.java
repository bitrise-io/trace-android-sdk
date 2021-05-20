package io.bitrise.trace.testapp.screen;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;
import io.bitrise.trace.testapp.network.NetworkActivity;
import io.bitrise.trace.testapp.network.OkHttpActivity;

/**
 * Screen class for {@link NetworkActivity}.
 */
public class NetworkActivityScreen extends BaseScreen {

  private static final BySelector okHttpButton = By.res(id + "btn_network_okhttp");
  private static final BySelector urlConnectionButton = By.res(id + "btn_network_urlconnection");

  /**
   * Constructor for class.
   *
   * @param uiDevice the UiDevice that launches the test cases.
   */
  public NetworkActivityScreen(@NonNull final UiDevice uiDevice) {
    super(uiDevice);
  }

  /**
   * Clicks on the OkHttp tests button and launches the {@link OkHttpActivity}.
   *
   * @return the created {@link BaseNetworkActivityScreen}.
   */
  @NonNull
  public BaseNetworkActivityScreen launchOkHttpActivity() {
    click(okHttpButton);
    return new BaseNetworkActivityScreen(uiDevice);
  }

  @Override
  public void waitTillLoad() {
    uiDevice.wait(Until.findObject(okHttpButton), DEFAULT_TIMEOUT);
    uiDevice.wait(Until.findObject(urlConnectionButton), DEFAULT_TIMEOUT);
  }
}
