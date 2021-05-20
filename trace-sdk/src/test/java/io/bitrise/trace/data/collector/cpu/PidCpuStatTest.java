package io.bitrise.trace.data.collector.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link CpuUsageData.PidCpuStat}.
 */
public class PidCpuStatTest {

  private final CpuUsageData.PidCpuStat pidCpuStat = new CpuUsageData.PidCpuStat(
      1, 2, 3, 4, 5);

  @Test
  public void toString_shouldMatchWithContent() {
    final String expected =
        "AppCpuStat{utime=1.0, stime=2.0, cutime=3.0, cstime=4.0, starttime=5.0}";
    assertEquals(expected, pidCpuStat.toString());
  }

  @Test
  public void toString_shouldMatchNoContent() {
    final String expected =
        "AppCpuStat{utime=0.0, stime=0.0, cutime=0.0, cstime=0.0, starttime=0.0}";
    final CpuUsageData.PidCpuStat emptyPidCpuStat = new CpuUsageData.PidCpuStat();
    assertEquals(expected, emptyPidCpuStat.toString());
  }

  @Test
  public void equals_sameObject() {
    assertTrue(pidCpuStat.equals(pidCpuStat));
  }

  @Test
  public void equals_notPidCpuStat() {
    assertFalse(pidCpuStat.equals("pid_cpu_stat"));
  }

  @Test
  public void equals_differentPidCpuStat() {
    final CpuUsageData.PidCpuStat differentPidCpuStat = new CpuUsageData.PidCpuStat(
        1, 2, 3, 4, 50);
    assertFalse(pidCpuStat.equals(differentPidCpuStat));
  }

  @Test
  public void hashCode_shouldMatch() {
    assertEquals(pidCpuStat.hashCode(), pidCpuStat.hashCode());
  }
}
