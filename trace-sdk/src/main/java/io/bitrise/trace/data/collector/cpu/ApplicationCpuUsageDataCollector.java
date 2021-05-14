package io.bitrise.trace.data.collector.cpu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {@link DataCollector} type, that collects the Applications usage of the CPU. For information
 * about how CPUs work on Linux based system see linked article. CPU usage data is parsed from
 * "stat" files, see linked manual for the format of these files.
 *
 * @see
 * <a href="https://www.opsdash.com/blog/cpu-usage-linux.html">https://www.opsdash.com/blog/cpu-usage-linux.html</a>
 * @see
 * <a href="https://man7.org/linux/man-pages/man5/proc.5.html">https://man7.org/linux/man-pages/man5/proc.5.html</a>
 */
public class ApplicationCpuUsageDataCollector extends CpuUsageDataCollector {

  // region Index for different CPU status information fields from proc/[pid]/stat
  private static final int PROC_PID_STAT_UTIME = 13;
  private static final int PROC_PID_STAT_STIME = 14;
  private static final int PROC_PID_STAT_CUTIME = 15;
  private static final int PROC_PID_STAT_CSTIME = 16;
  private static final int PROC_PID_STAT_START_TIME = 21;
  private static final int CPU_MEASUREMENT_DEFAULT_SLEEP_AMOUNT = 100;
  private static Integer userHZ;
  // endregion
  @VisibleForTesting
  CpuUsageData.PidCpuStatWithUptime previousAverageApplicationStats;

  public ApplicationCpuUsageDataCollector() {
    previousAverageApplicationStats = getApplicationCpuUsage();
  }

  /**
   * Gets the uptime of the CPU.
   *
   * @return the uptime.
   */
  @VisibleForTesting
  static float getUptime(final float cpuStartTime, final float elapsedSeconds) {
    return elapsedSeconds
        - (cpuStartTime / getNumberOfClockTicksPerSecond(elapsedSeconds, cpuStartTime));
  }

  /**
   * Gets the USER_HZ constant. By default it is 100 for most Android devices, but it can be
   * altered.
   *
   * @param elapsedSeconds the elapsed seconds since boot.
   * @param startTime      The time the process started after system boot. In kernels before
   *                       Linux 2.6, this value was expressed in jiffies.  Since Linux 2.6, the
   *                       value is expressed in clock ticks (divide by sysconf(_SC_CLK_TCK)).
   *                       This means elapsedTime= ticks/USER_HZ -> USER_HZ = startTime/elapsedTime.
   * @return the USER_HZ.
   */
  @VisibleForTesting
  static int getNumberOfClockTicksPerSecond(final float elapsedSeconds, final float startTime) {
    if (userHZ == null) {
      userHZ = Math.round(startTime / elapsedSeconds);
    }

    return 100;
  }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getApplicationCpuUsagePercentage(previousAverageApplicationStats));
    return data;
  }

  /**
   * Gets the CPU usage percentage for the application as a Double value. Works the following way:
   * <ul>
   *     <li>Gets current usage proc/self/stat file</li>
   *     <li>From the previous measure it calculates the change since the last measure</li>
   *     <li>Calculates and returns the percentage of usage</li>
   * </ul>
   *
   * <p>If the proc/self/stat file cannot be read, the IOException is caught and logged, and {@code
   * null} is returned.
   *
   * @return the CPU usage percentage, or {@code null} when it cannot be read.
   */
  @Nullable
  @VisibleForTesting
  Double getApplicationCpuUsagePercentage(
      @NonNull final CpuUsageData.PidCpuStatWithUptime previousPidCpuStatWithUptime) {
    final CpuUsageData.PidCpuStatWithUptime pidCpuStatWithUptime = getApplicationCpuUsage();
    final CpuUsageData.PidCpuStat currentStats = pidCpuStatWithUptime.getPidCpuStat();
    final CpuUsageData.PidCpuStat previousStats = previousPidCpuStatWithUptime.getPidCpuStat();

    final double totalTime = currentStats.getUtime() + currentStats.getStime()
        + currentStats.getCstime() + currentStats.getCutime();
    final double previousTotalTime = previousStats.getUtime() + previousStats.getStime()
        + previousStats.getCstime() + previousStats.getCutime();
    final double totalTimeDiff = totalTime - previousTotalTime;

    final double upTimeDiff =
        pidCpuStatWithUptime.getUptime() - previousPidCpuStatWithUptime.getUptime();
    final double clkTck = getNumberOfClockTicksPerSecond(pidCpuStatWithUptime.getElapsedSeconds(),
        currentStats.getStarttime());

    if (upTimeDiff == 0) {
      try {
        Thread.sleep(CPU_MEASUREMENT_DEFAULT_SLEEP_AMOUNT);
      } catch (final InterruptedException e) {
        // nop
      }
      return getApplicationCpuUsagePercentage(previousPidCpuStatWithUptime);
    }
    previousAverageApplicationStats = pidCpuStatWithUptime;

    final double result = 100 * (totalTimeDiff / clkTck) / upTimeDiff;

    // filter out invalid values
    if (result < 0 || result > 100) {
      TraceLog.w(LogMessageConstants.FAILED_TO_READ_CPU_STATS);
      return null;
    }

    return result;
  }

  /**
   * Gets the usage of the CPU for a given application (process).
   *
   * @return a {@link CpuUsageData.PidCpuStatWithUptime} containing the data.
   * @see
   * <a href="https://man7.org/linux/man-pages/man5/proc.5.html">https://man7.org/linux/man-pages/man5/proc.5.html</a>
   */
  @Nullable
  @VisibleForTesting
  CpuUsageData.PidCpuStatWithUptime getApplicationCpuUsage() {
    try {
      return parsePidCpuStat(new RandomAccessFile("/proc/self/stat", "r").readLine());
    } catch (final IOException e) {
      TraceLog.w(e, LogMessageConstants.FAILED_TO_READ_APPLICATION_CPU_STATS);
    }
    return null;
  }


  /**
   * Parses the given String from a given process stat File and returns a
   * {@link CpuUsageData.PidCpuStatWithUptime} from it.
   *
   * @param line the String line to parse.
   * @return the PidCpuStatWithUptime parsed from it, or {@code null} if it could not be read
   * (e.g. CPU offline).
   */
  @Nullable
  @VisibleForTesting
  CpuUsageData.PidCpuStatWithUptime parsePidCpuStat(@NonNull final String line) {
    CpuUsageData.PidCpuStatWithUptime pidCpuStatWithUptime = null;
    try {
      final String[] usage = line.split(" +");
      final CpuUsageData.PidCpuStat pidCpuStat = new CpuUsageData.PidCpuStat(
          Float.parseFloat(usage[PROC_PID_STAT_UTIME]),
          Float.parseFloat(usage[PROC_PID_STAT_STIME]),
          Float.parseFloat(usage[PROC_PID_STAT_CUTIME]),
          Float.parseFloat(usage[PROC_PID_STAT_CSTIME]),
          Float.parseFloat(usage[PROC_PID_STAT_START_TIME])
      );
      final long elapsedSeconds = TraceClock.getElapsedSeconds();
      final double uptime = getUptime(pidCpuStat.getStarttime(), elapsedSeconds);
      pidCpuStatWithUptime =
          new CpuUsageData.PidCpuStatWithUptime(pidCpuStat, uptime, elapsedSeconds);
    } catch (final Exception e) {
      TraceLog.w(e, LogMessageConstants.FAILED_TO_READ_APPLICATION_CPU_STATS);
    }
    return pidCpuStatWithUptime;
  }
}
