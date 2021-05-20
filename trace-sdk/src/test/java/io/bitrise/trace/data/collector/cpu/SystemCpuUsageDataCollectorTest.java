package io.bitrise.trace.data.collector.cpu;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link SystemCpuUsageDataCollector}.
 */
public class SystemCpuUsageDataCollectorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_1 = new CpuUsageData.CpuStat(10, 10, 10, 10, 10, 10, 10,
            10);
    private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_2 = new CpuUsageData.CpuStat(3, 3, 3, 3, 3, 3, 3, 3);
    private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_3 =
            new CpuUsageData.CpuStat(10, 20, 30, 40, 50, 60, 70, 80);
    private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_4 =
            new CpuUsageData.CpuStat(100, 200, 300, 400, 500, 600, 700, 800);
    private final SystemCpuUsageDataCollector collector = new SystemCpuUsageDataCollector();

    @Test
    public void parseSystemCpuStat_ValidInput() {
        final CpuUsageData.CpuStat actual = SystemCpuUsageDataCollector.parseSystemCpuStat(3, " cpu2 23456 2345 2345 " +
                "2345 234 0 0 0 0 0");
        assertThat(actual, equalTo(new CpuUsageData.CpuStat(2345f, 2345f, 2345f, 234f, 0f, 0f, 0f, 0f)));
    }

    @Test
    public void parseSystemCpuStat_InvalidInput() {
        final CpuUsageData.CpuStat actual = SystemCpuUsageDataCollector.parseSystemCpuStat(0, " cpu2 23456 2345 2345 " +
                "2345 234 0 0 0 0 0");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void calculateDiff_ShouldBeTheDifference() {
        final CpuUsageData.CpuStat actual = SystemCpuUsageDataCollector.calculateDiff(DUMMY_CPU_STAT_1,
                DUMMY_CPU_STAT_2);
        final CpuUsageData.CpuStat expected = new CpuUsageData.CpuStat(7, 7, 7, 7, 7, 7, 7, 7);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void getTotalUsage_ShouldBeTheTotal() {
        assertThat(SystemCpuUsageDataCollector.getTotalUsage(DUMMY_CPU_STAT_1), equalTo(80f));
    }

    @Test
    public void getStatPercentages_ShouldBeThePercentage() {
        final CpuUsageData.CpuStat actual = SystemCpuUsageDataCollector.getStatPercentages(DUMMY_CPU_STAT_1, 10);
        assertThat(actual, equalTo(new CpuUsageData.CpuStat(100, 100, 100, 100, 100, 100, 100, 100)));
    }

    @Test
    public void getPermissions() {
        final SystemCpuUsageDataCollector collector = new SystemCpuUsageDataCollector();
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        final SystemCpuUsageDataCollector collector = new SystemCpuUsageDataCollector();
        assertEquals(15000, collector.getIntervalMs());
    }

    @Test
    public void getSystemCpuUsagePercentage() {
        collector.previousAverageSystemStats = DUMMY_CPU_STAT_3;
        final CpuUsageData.CpuStat result = collector.getSystemCpuUsagePercentage(DUMMY_CPU_STAT_4);

        assertEquals(new CpuUsageData.CpuStat(2.5f, 5.0f, 7.5000005f,
                10.0f, 12.5f, 15.000001f, 17.5f, 20.0f), result);
        assertEquals(DUMMY_CPU_STAT_4, collector.previousAverageSystemStats);
    }

    @Test
    public void getCoresUsage_2cores() throws IOException {
        final File file = temporaryFolder.newFile();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.writeBytes("cpu  0 110 220 330 440 550 660 770 880 0 0\n");
        randomAccessFile.writeBytes("cpu0 0 100 200 300 400 500 600 700 800 0 0\n");
        randomAccessFile.writeBytes("cpu2 0 10 20 30 40 50 60 70 80 0 0\n");
        randomAccessFile.close();

        final List<CpuUsageData.CpuStat> result =
                collector.getCoresUsage(new RandomAccessFile(file, "r"), 2);

        assertEquals(3, result.size());
        assertEquals(new CpuUsageData.CpuStat(110.0f, 220.0f, 330.0f, 440.0f,
                550.0f, 660.0f, 770.0f, 880.0f), result.get(0));
        assertEquals(DUMMY_CPU_STAT_4, result.get(1));
        assertEquals(DUMMY_CPU_STAT_3, result.get(2));
    }

    @Test
    public void getCoresUsage_2cores_missing2ndCoreRepeatsFirstCore() throws IOException {
        final File file = temporaryFolder.newFile();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.writeBytes("cpu  0 110 220 330 440 550 660 770 880 0 0\n");
        randomAccessFile.writeBytes("cpu0 0 100 200 300 400 500 600 700 800 0 0\n");
        randomAccessFile.close();

        final List<CpuUsageData.CpuStat> result =
                collector.getCoresUsage(new RandomAccessFile(file, "r"), 2);

        assertEquals(2, result.size());
        assertEquals(new CpuUsageData.CpuStat(110.0f, 220.0f, 330.0f, 440.0f,
                550.0f, 660.0f, 770.0f, 880.0f), result.get(0));
        assertEquals(DUMMY_CPU_STAT_4, result.get(1));

        file.deleteOnExit();
    }

    @Test
    public void getCoresUsage_nullLines() throws IOException {
        final File file = temporaryFolder.newFile();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");

        final List<CpuUsageData.CpuStat> result =
                collector.getCoresUsage(new RandomAccessFile(file, "r"), 2);

        assertEquals(0, result.size());
    }

    @Test
    public void getCoresUsage_linesNotCpuRecord() throws IOException {
        final File file = temporaryFolder.newFile();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.writeBytes("=^..^=");

        final List<CpuUsageData.CpuStat> result =
                collector.getCoresUsage(new RandomAccessFile(file, "r"), 2);

        assertEquals(0, result.size());
    }

}
