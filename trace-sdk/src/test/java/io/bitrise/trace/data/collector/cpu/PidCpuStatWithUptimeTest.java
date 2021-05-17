package io.bitrise.trace.data.collector.cpu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CpuUsageData.PidCpuStatWithUptime}
 */
public class PidCpuStatWithUptimeTest {

    private final CpuUsageData.PidCpuStat pidCpuStat = new CpuUsageData.PidCpuStat(
            1, 2, 3, 4, 5 );
    private final CpuUsageData.PidCpuStatWithUptime pidCpuStatWithUptime =
            new CpuUsageData.PidCpuStatWithUptime(pidCpuStat, 10, 20 );

    @Test
    public void toString_shouldMatchWithContent() {
        final String expected = "PidCpuStatWithUptime{pidCpuStat=AppCpuStat{utime=1.0, stime=2.0, cutime=3.0, cstime=4.0, starttime=5.0}, uptime=10.0, elapsedSeconds=20.0}";
        assertEquals(expected, pidCpuStatWithUptime.toString());
    }

    @Test
    public void toString_shouldMatchNoContent() {
        final String expected = "PidCpuStatWithUptime{pidCpuStat=AppCpuStat{utime=0.0, stime=0.0, cutime=0.0, cstime=0.0, starttime=0.0}, uptime=0.0, elapsedSeconds=0.0}";
        final CpuUsageData.PidCpuStatWithUptime emptyPidCpuStatWithUptime = new CpuUsageData.PidCpuStatWithUptime();
        assertEquals(expected, emptyPidCpuStatWithUptime.toString());
    }

    @Test
    public void equals_sameObject() {
        assertTrue(pidCpuStatWithUptime.equals(pidCpuStatWithUptime));
    }

    @Test
    public void equals_notPidCpuStat() {
        assertFalse(pidCpuStatWithUptime.equals("pid_cpu_stat"));
    }

    @Test
    public void equals_differentPidCpuStatWithUptime() {
        final CpuUsageData.PidCpuStat differentPidCpuStat = new CpuUsageData.PidCpuStat(
                10,20,30,40,50 );
        final CpuUsageData.PidCpuStatWithUptime differentPidCpuStatWithUptime =
                new CpuUsageData.PidCpuStatWithUptime(differentPidCpuStat, 10, 20 );
        assertFalse(pidCpuStatWithUptime.equals(differentPidCpuStatWithUptime));
    }

    @Test
    public void hashCode_shouldMatch() {
        assertEquals(1975361854, pidCpuStatWithUptime.hashCode());
    }
}
