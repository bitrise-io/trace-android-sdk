package io.bitrise.trace.data.collector.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link CpuUsageData.CpuStat}.
 */
public class CpuStatTest {
  final CpuUsageData.CpuStat cpuStat = new CpuUsageData.CpuStat(
      1, 2, 3, 4, 5, 6, 7, 8);

  @Test
  public void toString_shouldMatch() {
    final String expected =
        "CpuStat{user=1.0, system=3.0, nice=2.0, idle=4.0, ioWait=5.0, irq=6.0, softIrq=7.0, "
            + "steal=8.0}";
    assertEquals(expected, cpuStat.toString());
  }

  @Test
  public void equals_sameObject() {
    assertTrue(cpuStat.equals(cpuStat));
  }

  @Test
  public void equals_notCpuStat() {
    assertFalse(cpuStat.equals("cpu_stat"));
  }

  @Test
  public void equals_differentCpuStat() {
    final CpuUsageData.CpuStat differentCpuStat = new CpuUsageData.CpuStat(
        1, 2, 3, 4, 5, 6, 7, 80);
    assertFalse(cpuStat.equals(differentCpuStat));
  }

  @Test
  public void hashCode_shouldMatch() {
    assertEquals(cpuStat.hashCode(), cpuStat.hashCode());
  }
}
