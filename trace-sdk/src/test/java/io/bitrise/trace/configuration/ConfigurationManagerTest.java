package io.bitrise.trace.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ConfigurationManager} class.
 */
public class ConfigurationManagerTest {

  @Before
  public void setUp() {
    ConfigurationManager.reset();
  }

  @After
  public void tearDown() {
    ConfigurationManager.reset();
  }

  /**
   * Check that a call to {@link ConfigurationManager#getInstance()} should return the same
   * instance.
   */
  @Test
  public void getInstance_shouldBeSingleton() {
    final ConfigurationManager expectedValue = ConfigurationManager.getInstance();
    final ConfigurationManager actualValue = ConfigurationManager.getInstance();
    assertThat(expectedValue, is(actualValue));
  }

  @Test
  public void getBuildConfigItem_itemExists() {
    final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    ConfigurationManager.initialised = true;
    final Map<String, Object> buildConfigurationMap = new HashMap<>();
    buildConfigurationMap.put("item", "something");
    ConfigurationManager.configurationMap = buildConfigurationMap;

    assertNotNull(configurationManager.getConfigItem("item"));
  }

  @Test
  public void getBuildConfigItem_itemDoesNotExist() {
    final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    ConfigurationManager.initialised = true;
    ConfigurationManager.configurationMap = new HashMap<>();

    assertNull(configurationManager.getConfigItem("item"));
  }

  @Test
  public void getBuildConfigItem_mapIsNull() {
    final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    ConfigurationManager.initialised = true;

    assertNull(configurationManager.getConfigItem("item"));
  }
}
