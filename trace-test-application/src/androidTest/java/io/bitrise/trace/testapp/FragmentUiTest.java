package io.bitrise.trace.testapp;

import static org.junit.Assert.assertNotNull;

import androidx.test.filters.SdkSuppress;
import io.bitrise.trace.testapp.screen.IndexActivityScreen;
import io.bitrise.trace.testapp.screen.MainActivityScreen;
import io.bitrise.trace.testapp.screen.SecondActivityScreen;
import io.bitrise.trace.testapp.screen.ThirdActivityScreen;
import org.junit.Test;

/**
 * UiTest for Fragment related behaviour.
 */
// This test currently fails on sdk21 devices
@SdkSuppress(minSdkVersion = 22)
public class FragmentUiTest extends BaseUiTest {

  /**
   * Tests Activity lifecycle states in different cases.
   * <ul>
   *     <li>Cold application start</li>
   *     <li>Hot application start</li>
   *     <li>Launching up new Activity</li>
   * </ul>
   */
  @Test
  public void fragmentStateTest() {
    final IndexActivityScreen indexActivityScreen = new IndexActivityScreen(uiDevice);
    assertNotNull(indexActivityScreen.getButtonUiTestsLabel());

    final MainActivityScreen mainActivityScreen = indexActivityScreen.launchUiTests();
    mainActivityScreen
        .showParentFragment()
        .showChildFragment();

    final SecondActivityScreen secondActivityScreen = mainActivityScreen.launchSecondActivity();
    secondActivityScreen
        .showParentFragment()
        .showChildFragment();

    final ThirdActivityScreen thirdActivityScreen = secondActivityScreen.launchThirdActivity();
    thirdActivityScreen
        .showParentFragment()
        .showChildFragment();

    uiDevice.pressHome();
  }
}
