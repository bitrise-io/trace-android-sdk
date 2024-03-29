package io.bitrise.trace.data.collector.device;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Context;
import com.scottyab.rootbeer.RootBeer;
import io.bitrise.trace.data.dto.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link DeviceRootedDataCollector}.
 */
public class DeviceRootedDataCollectorTest {

  private RootBeer mockRootBeer;
  private DeviceRootedDataCollector deviceRootedDataCollector;

  /**
   * Sets up each test case.
   */
  @Before
  public void setUp() {
    Context mockContext = Mockito.mock(Context.class);
    mockRootBeer = Mockito.mock(RootBeer.class);
    deviceRootedDataCollector = new DeviceRootedDataCollector(mockContext);
    deviceRootedDataCollector.rootBeer = mockRootBeer;
  }

  /**
   * Verifies that when {@link DeviceRootedDataCollector#collectData()} is called, the content
   * of the returned {@link Data} should not be {@code null}.
   */
  @Test
  public void collectData_contentShouldBeNotNull() {
    assertNotNull(deviceRootedDataCollector.collectData().getContent());
  }

  @Test
  public void collectData_contentShouldBeFalse() {
    when(mockRootBeer.isRooted()).thenReturn(false);
    assertFalse((boolean) deviceRootedDataCollector.collectData().getContent());
  }

  @Test
  public void collectData_contentShouldBeTrue() {
    when(mockRootBeer.isRooted()).thenReturn(true);
    assertTrue((boolean) deviceRootedDataCollector.collectData().getContent());
  }

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], deviceRootedDataCollector.getPermissions());
  }
}
