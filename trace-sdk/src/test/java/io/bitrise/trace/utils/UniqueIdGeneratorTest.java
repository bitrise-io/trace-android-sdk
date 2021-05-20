package io.bitrise.trace.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Test cases for {@link UniqueIdGenerator}.
 */
public class UniqueIdGeneratorTest {

  @Test
  public void makeSpanId() {
    final String id = UniqueIdGenerator.makeSpanId();
    assertEquals(16, id.length());
    assertFalse(id.contains("-"));
  }

  @Test
  public void makeTraceId() {
    final String id = UniqueIdGenerator.makeTraceId();
    assertEquals(32, id.length());
    assertFalse(id.contains("-"));
  }
}
