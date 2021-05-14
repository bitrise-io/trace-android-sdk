package io.bitrise.trace.data.collector.cpu;

import androidx.annotation.NonNull;
import java.util.Objects;

/**
 * Data class for {@link SystemCpuUsageDataCollector}.
 */
public class CpuUsageData {

  private CpuUsageData() {
    throw new UnsupportedOperationException("Must be a subclass!");
  }

  /**
   * Data class for managing stats of the CPU from proc/stat.
   */
  public static class CpuStat {
    private float user;
    private float system;
    private float nice;
    private float idle;
    private float ioWait;
    private float irq;
    private float softIrq;
    private float steal;

    /**
     * Constructor for class.
     *
     * @param user    the user time.
     * @param nice    the nice time.
     * @param system  the system time.
     * @param idle    the idel time.
     * @param ioWait  the ioWait time
     * @param irq     the irq time.
     * @param softIrq the softIrq time.
     * @param steal   the steal time.
     */
    public CpuStat(
        final float user,
        final float nice,
        final float system,
        final float idle,
        final float ioWait,
        final float irq,
        final float softIrq,
        final float steal
    ) {
      this.user = user;
      this.nice = nice;
      this.system = system;
      this.idle = idle;
      this.ioWait = ioWait;
      this.irq = irq;
      this.softIrq = softIrq;
      this.steal = steal;
    }

    public CpuStat() {
      //nop
    }

    public float getUser() {
      return user;
    }

    public float getSystem() {
      return system;
    }

    public float getNice() {
      return nice;
    }

    public float getIdle() {
      return idle;
    }

    public float getIoWait() {
      return ioWait;
    }

    public float getIrq() {
      return irq;
    }

    public float getSoftIrq() {
      return softIrq;
    }

    public float getSteal() {
      return steal;
    }

    @Override
    public String toString() {
      return "CpuStat{"
          + "user=" + user
          + ", system=" + system
          + ", nice=" + nice
          + ", idle=" + idle
          + ", ioWait=" + ioWait
          + ", irq=" + irq
          + ", softIrq=" + softIrq
          + ", steal=" + steal
          + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof CpuStat)) {
        return false;
      }
      final CpuStat cpuStat = (CpuStat) o;
      return Float.compare(cpuStat.user, user) == 0
          && Float.compare(cpuStat.system, system) == 0
          && Float.compare(cpuStat.nice, nice) == 0
          && Float.compare(cpuStat.idle, idle) == 0
          && Float.compare(cpuStat.ioWait, ioWait) == 0
          && Float.compare(cpuStat.irq, irq) == 0
          && Float.compare(cpuStat.softIrq, softIrq) == 0
          && Float.compare(cpuStat.steal, steal) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(user, system, nice, idle, ioWait, irq, softIrq, steal);
    }
  }

  /**
   * Data class for managing stats of the CPU from proc/[pid]/stat.
   */
  public static class PidCpuStat {
    private float utime;
    private float stime;
    private float cutime;
    private float cstime;
    private float starttime;

    /**
     * Constructor for class.
     *
     * @param utime     the uTime.
     * @param stime     the sTime.
     * @param cutime    the suTime.
     * @param cstime    the csTime.
     * @param starttime the starttime.
     */
    public PidCpuStat(
        final float utime,
        final float stime,
        final float cutime,
        final float cstime,
        final float starttime
    ) {
      this.utime = utime;
      this.stime = stime;
      this.cutime = cutime;
      this.cstime = cstime;
      this.starttime = starttime;
    }

    /**
     * Constructor for class.
     */
    public PidCpuStat() {
    }

    public float getUtime() {
      return utime;
    }

    public float getStime() {
      return stime;
    }

    public float getCutime() {
      return cutime;
    }

    public float getCstime() {
      return cstime;
    }

    public float getStarttime() {
      return starttime;
    }

    @Override
    public String toString() {
      return "AppCpuStat{"
          + "utime=" + utime
          + ", stime=" + stime
          + ", cutime=" + cutime
          + ", cstime=" + cstime
          + ", starttime=" + starttime
          + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof PidCpuStat)) {
        return false;
      }
      final PidCpuStat that = (PidCpuStat) o;
      return Float.compare(that.utime, utime) == 0
          && Float.compare(that.stime, stime) == 0
          && Float.compare(that.cutime, cutime) == 0
          && Float.compare(that.cstime, cstime) == 0
          && Float.compare(that.starttime, starttime) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(utime, stime, cutime, cstime, starttime);
    }
  }

  /**
   * A {@link PidCpuStat} data class with the uptime.
   */
  public static class PidCpuStatWithUptime {

    @NonNull
    private final PidCpuStat pidCpuStat;
    private double uptime;

    private float elapsedSeconds;

    /**
     * Constructor for class.
     *
     * @param pidCpuStat     the given {@link PidCpuStat}.
     * @param uptime         the uptime.
     * @param elapsedSeconds the elapsed time in seconds.
     */
    public PidCpuStatWithUptime(@NonNull final PidCpuStat pidCpuStat,
                                final double uptime,
                                final float elapsedSeconds) {
      this.pidCpuStat = pidCpuStat;
      this.uptime = uptime;
      this.elapsedSeconds = elapsedSeconds;
    }

    public PidCpuStatWithUptime() {
      this.pidCpuStat = new PidCpuStat();
    }

    public double getUptime() {
      return uptime;
    }

    @NonNull
    public PidCpuStat getPidCpuStat() {
      return pidCpuStat;
    }

    public float getElapsedSeconds() {
      return elapsedSeconds;
    }

    @Override
    public String toString() {
      return "PidCpuStatWithUptime{"
          + "pidCpuStat=" + pidCpuStat
          + ", uptime=" + uptime
          + ", elapsedSeconds=" + elapsedSeconds
          + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof PidCpuStatWithUptime)) {
        return false;
      }
      final PidCpuStatWithUptime that = (PidCpuStatWithUptime) o;
      return Double.compare(that.uptime, uptime) == 0
          && Float.compare(that.elapsedSeconds, elapsedSeconds) == 0
          && pidCpuStat.equals(that.pidCpuStat);
    }

    @Override
    public int hashCode() {
      return Objects.hash(pidCpuStat, uptime, elapsedSeconds);
    }
  }
}
