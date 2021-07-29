package io.bitrise.trace.data.collector.device;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import androidx.annotation.NonNull;
import androidx.test.filters.SdkSuppress;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;
import org.junit.Test;

/**
 * Instrumented tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorInstrumentedTest
    extends BaseDataCollectorInstrumentedTest {

  @Test
  @SdkSuppress(maxSdkVersion = 24)
  public void collectData_sdk24AndEarlier() {
    final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
    assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
  }

  @Test
  @SdkSuppress(minSdkVersion = 25)
  public void collectData_sdk25AndAfter() {
    final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
    assertNetworkCollectedData(collector.collectData(), "WIFI");
  }

  private void assertNetworkCollectedData(@NonNull final Data actualData,
                                          @NonNull final String expectedContent) {
    final Data expectedData = new Data(DeviceNetworkTypeDataCollector.class);
    expectedData.setContent(expectedContent);
    assertThat(actualData, is(equalTo(expectedData)));
  }
}
