package io.bitrise.trace.data.collector.cpu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;

/**
 * {@link DataCollector} type, that collects the usage of the CPU. For information about how CPUs work on Linux based
 * system see linked article. CPU usage data is parsed from "stat" files, see linked manual for the format of these
 * files.
 *
 * @see
 * <a href ="https://www.opsdash.com/blog/cpu-usage-linux.html">https://www.opsdash.com/blog/cpu-usage-linux.html</a>
 * @see
 * <a href="https://man7.org/linux/man-pages/man5/proc.5.html">https://man7.org/linux/man-pages/man5/proc.5.html</a>
 */
public class SystemCpuUsageDataCollector extends CpuUsageDataCollector {

    private CpuUsageData.CpuStat previousAverageSystemStats = new CpuUsageData.CpuStat();

    // region Index for different CPU status information fields from proc/stat
    private static final int PROC_STAT_TOTAL_USAGE_INDEX = 0;
    private static final int PROC_STAT_USER = 1;
    private static final int PROC_STAT_NICE = 2;
    private static final int PROC_STAT_SYSTEM = 3;
    private static final int PROC_STAT_IDLE = 4;
    private static final int PROC_STAT_IO_WAIT = 5;
    private static final int PROC_STAT_IRQ = 6;
    private static final int PROC_STAT_SOFT_IRQ = 7;
    private static final int PROC_STAT_STEAL = 8;
    // endregion

    /**
     * Gets the number of cores available in this device, across all processors.
     *
     * @return the number of cores.
     */
    @VisibleForTesting
    static int getNumberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    @NonNull
    @Override
    public Data collectData() {
        final Data data = new Data(this);
        data.setContent(getCpuUsage());
        return data;
    }

    /**
     * Resolves {@link CpuUsageData.CpuStat} values into percentages.
     *
     * @param cpuStat the given CpuStat to resolve.
     * @return the resolved CpuStat.
     */
    @NonNull
    @VisibleForTesting
    static CpuUsageData.CpuStat getStatPercentages(@NonNull final CpuUsageData.CpuStat cpuStat, final float total) {
        return new CpuUsageData.CpuStat(
                (cpuStat.getUser() / total) * 100,
                (cpuStat.getNice() / total) * 100,
                (cpuStat.getSystem() / total) * 100,
                (cpuStat.getIdle() / total) * 100,
                (cpuStat.getIoWait() / total) * 100,
                (cpuStat.getIrq() / total) * 100,
                (cpuStat.getSoftIrq() / total) * 100,
                (cpuStat.getSteal() / total) * 100);
    }

    /**
     * Sums the different components of a {@link CpuUsageData.CpuStat}.
     *
     * @param cpuStat the given CpuStat.
     * @return the sum of the components.
     */
    @VisibleForTesting
    static float getTotalUsage(@NonNull final CpuUsageData.CpuStat cpuStat) {
        return cpuStat.getUser() +
                cpuStat.getNice() +
                cpuStat.getSystem() +
                cpuStat.getIdle() +
                cpuStat.getIoWait() +
                cpuStat.getIrq() +
                cpuStat.getSoftIrq() +
                cpuStat.getSteal();
    }

    /**
     * Calculates the difference between two {@link CpuUsageData.CpuStat} object.
     *
     * @param first  the CpuStat value to subtract from.
     * @param second the CpuStat value to subtract.
     * @return the difference.
     */
    @NonNull
    @VisibleForTesting
    static CpuUsageData.CpuStat calculateDiff(@NonNull final CpuUsageData.CpuStat first,
                                              @NonNull final CpuUsageData.CpuStat second) {
        return new CpuUsageData.CpuStat(
                first.getUser() - second.getUser(),
                first.getNice() - second.getNice(),
                first.getSystem() - second.getSystem(),
                first.getIdle() - second.getIdle(),
                first.getIoWait() - second.getIoWait(),
                first.getIrq() - second.getIrq(),
                first.getSoftIrq() - second.getSoftIrq(),
                first.getSteal() - second.getSteal()
        );
    }

    /**
     * Parses the given String from the system stat File and returns a {@link CpuUsageData.CpuStat} from it.
     *
     * @param coreNumber the number of the core.
     * @param line       the String line to parse.
     * @return the CpuStat parsed from it, or {@code null} if it could not be read (e.g. CPU offline).
     */
    @Nullable
    @VisibleForTesting
    static CpuUsageData.CpuStat parseSystemCpuStat(final int coreNumber, @NonNull final String line) {
        CpuUsageData.CpuStat cpuStat = null;
        try {
            String cpuStr;
            if (coreNumber > 0) {
                cpuStr = "cpu" + (coreNumber - 1) + " ";
            } else {
                cpuStr = "cpu ";
            }

            if (line.contains(cpuStr)) {
                final String[] usage = line.substring(cpuStr.length()).trim().split(" +");
                cpuStat = new CpuUsageData.CpuStat(
                        Float.parseFloat(usage[PROC_STAT_USER]),
                        Float.parseFloat(usage[PROC_STAT_NICE]),
                        Float.parseFloat(usage[PROC_STAT_SYSTEM]),
                        Float.parseFloat(usage[PROC_STAT_IDLE]),
                        Float.parseFloat(usage[PROC_STAT_IO_WAIT]),
                        Float.parseFloat(usage[PROC_STAT_IRQ]),
                        Float.parseFloat(usage[PROC_STAT_SOFT_IRQ]),
                        Float.parseFloat(usage[PROC_STAT_STEAL])
                );
            }
        } catch (final Exception e) {
            TraceLog.w(e, LogMessageConstants.FAILED_TO_READ_SYSTEM_CPU_STATS);
        }
        return cpuStat;
    }

    /**
     * Gets the CPU usage for the system and the application. From {@link android.os.Build.VERSION_CODES#O} requires
     * root permission.
     *
     * @return {@link CpuUsageData} that holds the values or {@code null} when it cannot be read.
     */
    @Nullable
    @VisibleForTesting
    CpuUsageData.CpuStat getCpuUsage() {
        final CpuUsageData.CpuStat totalCpuUsage = getSystemCpuUsage();
        if (totalCpuUsage == null) {
            return null;
        } else {
            return getSystemCpuUsagePercentage(totalCpuUsage);
        }
    }

    /**
     * Gets the percentage of CPU usage of the system as a {@link CpuUsageData.CpuStat}.
     *
     * @param totalUsageStat the CpuStat of the total system CPU usage.
     * @return the CPU usage percentages for the different components.
     */
    @NonNull
    private CpuUsageData.CpuStat getSystemCpuUsagePercentage(@NonNull final CpuUsageData.CpuStat totalUsageStat) {
        final CpuUsageData.CpuStat diff = calculateDiff(totalUsageStat, previousAverageSystemStats);
        previousAverageSystemStats = totalUsageStat;
        return getStatPercentages(diff, getTotalUsage(totalUsageStat));
    }

    /**
     * Gets the CPU usage for the system as a {@link CpuUsageData.CpuStat}. Works the following way:
     * <ul>
     *     <li>Gets current usage proc/stat file</li>
     *     <li>Reads the total usage from it</li>
     * </ul>
     * <p>
     * If the proc/stat file cannot be read, the IOException is caught and logged, and {@code null} is returned.
     *
     * @return the total CPU usage, or {@code null} when it cannot be read.
     */
    @Nullable
    @VisibleForTesting
    CpuUsageData.CpuStat getSystemCpuUsage() {
        final int numberOfCores = getNumberOfCores();
        final ArrayList<CpuUsageData.CpuStat> currentUsage;
        try {
            currentUsage = getCoresUsage(new RandomAccessFile("/proc/stat", "r"), numberOfCores);
            return currentUsage.get(PROC_STAT_TOTAL_USAGE_INDEX);
        } catch (final IOException e) {
            TraceLog.w(e, LogMessageConstants.FAILED_TO_READ_SYSTEM_CPU_STATS);
        }
        return null;
    }

    /**
     * Gets the current usage of the cores of the CPU.
     * <p>
     * An example for proc/stat file content:
     * cpu  123456 12345 12345 12345 1234 1 1234 0 0 0
     * cpu0 100000 10000 10000 10000 1000 1 1234 0 0 0
     * cpu2 23456 2345 2345 2345 234 0 0 0 0 0
     * <p>
     * As seen, the first line is the total, the rest are for each core.
     *
     * @param randomAccessFile the RandomAccessFile to read from.
     * @param numberOfCores    the number of cores.
     * @return an ArrayList of each CPU core stat, element can be {@code null} when the given core is switched off.
     * @throws IOException if the given RandomAccessFile cannot be read.
     */
    @NonNull
    @VisibleForTesting
    ArrayList<CpuUsageData.CpuStat> getCoresUsage(@NonNull final RandomAccessFile randomAccessFile,
                                                  final int numberOfCores) throws IOException {
        final ArrayList<CpuUsageData.CpuStat> cpuStats = new ArrayList<>();
        String line = randomAccessFile.readLine();

        for (int i = 0; i <= numberOfCores + 1; i++) {
            if (!line.contains("cpu")) {
                break;
            }
            final CpuUsageData.CpuStat currentCpuStat = parseSystemCpuStat(i, line);
            if (currentCpuStat != null) {
                cpuStats.add(i, currentCpuStat);
                line = randomAccessFile.readLine();
            }
        }
        return cpuStats;
    }
}
