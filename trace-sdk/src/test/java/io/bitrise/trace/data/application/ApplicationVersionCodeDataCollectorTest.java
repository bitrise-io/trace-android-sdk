package io.bitrise.trace.data.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import io.bitrise.trace.data.collector.application.ApplicationVersionCodeDataCollector;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ApplicationVersionCodeDataCollector}.
 */
public class ApplicationVersionCodeDataCollectorTest {

  private static final int versionCode = 123;
  private final PackageManager mockPackageManager = Mockito.mock(PackageManager.class);
  private final String packageName = "packageName";
  private final ApplicationVersionCodeDataCollector collector =
      new ApplicationVersionCodeDataCollector(mockPackageManager, packageName);

  @Test
  public void collectData_configurationManagerInitialised()
      throws PackageManager.NameNotFoundException {
    final PackageInfo packageInfo = new PackageInfo();
    packageInfo.versionCode = versionCode;
    when(mockPackageManager.getPackageInfo(packageName, 0))
        .thenReturn(packageInfo);

    final Data expectedData = new Data(ApplicationVersionCodeDataCollector.class);
    expectedData.setContent(String.valueOf(versionCode));

    assertEquals(expectedData, collector.collectData());
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], collector.getPermissions());
  }

  @Test
  public void getIntervalMs() {
    assertEquals(0, collector.getIntervalMs());
  }
}
