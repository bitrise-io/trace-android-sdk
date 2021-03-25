package io.bitrise.trace.data.collector.cpu;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import io.bitrise.trace.InstrumentedTestRequirements;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Instrumented tests for {@link SystemCpuUsageDataCollector}.
 */
public class SystemCpuUsageDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    private static final double HUNDRED_PERCENT = 100d;
    private static final double ZERO_PERCENT = 0d;

    /**
     * Verifies that when we have access to the proc/stat files, {@link SystemCpuUsageDataCollector#collectData()} is
     * called, the content of the returned {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }

    /**
     * Verifies that when we have access to the proc/stat files, {@link SystemCpuUsageDataCollector#collectData()} is
     * called, the content of the returned  {@link CpuUsageData.CpuStat} for System usage should not be {@code null}.
     */
    @Test
    public void collectData_systemUsageShouldBeNotNull() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned {@link CpuUsageData.CpuStat} for
     * System usage the user value should be greater than zero.
     */
    @Test
    public void collectData_systemUserUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getUser(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned  {@link CpuUsageData.CpuStat} for
     * System usage the system value should be greater than zero.
     */
    @Test
    public void collectData_systemSystemUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getSystem(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned {@link CpuUsageData.CpuStat} for
     * System usage the nice value should be greater than zero.
     */
    @Test
    public void collectData_systemNiceUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getNice(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned {@link CpuUsageData.CpuStat} for
     * System usage the idle value should be greater than zero.
     */
    @Test
    public void collectData_systemIdleUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getIdle(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned {@link CpuUsageData.CpuStat} for
     * System usage the ioWait value should be greater than zero.
     */
    @Test
    public void collectData_systemIoWaitUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getIoWait(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we have access to the proc/stat files, and
     * {@link SystemCpuUsageDataCollector#collectData()} is called, in the returned {@link CpuUsageData.CpuStat} for
     * System usage the irq value should be greater than zero.
     */
    @Test
    public void collectData_systemIrqUsageShouldBePositive() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue =
                (CpuUsageData.CpuStat) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue.getSystem(), is(greaterThanOrEqualTo(0f)));
    }

    /**
     * Verifies that when we do not have access to the proc/stat files,
     * {@link SystemCpuUsageDataCollector#collectData()} is called, the content of the returned {@link Data} should
     * be {@code null}.
     */
    @Test
    public void collectData_resultShouldBeNullWithoutRootAccess() {
        InstrumentedTestRequirements.assumeCpuApiLevelFail();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData actualValue = (CpuUsageData) systemCpuUsageDataCollector.collectData().getContent();
        assertThat(actualValue, is(nullValue()));
    }

    /**
     * Verifies that when when we do have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getSystemCpuUsage()} is called, the returned {@link CpuUsageData.CpuStat}
     * should be not {@code null}.
     */
    @Test
    public void getSystemCpuUsage_resultShouldNotBeNull() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue = systemCpuUsageDataCollector.getSystemCpuUsage();
        assertThat(actualValue, is(notNullValue()));
    }

    /**
     * Verifies that when when we do not have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getSystemCpuUsage()} is called, the returned {@link CpuUsageData.CpuStat}
     * should be {@code null}.
     */
    @Test
    public void getSystemCpuUsage_resultShouldBeNullWithoutRootAccess() {
        InstrumentedTestRequirements.assumeCpuApiLevelFail();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue = systemCpuUsageDataCollector.getSystemCpuUsage();
        assertThat(actualValue, is(nullValue()));
    }

    /**
     * Verifies that when when we do have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getCpuUsage()} is called, the returned {@link CpuUsageData.CpuStat}
     * should be not {@code null} and all values should be between 0 and 100.
     */
    @Test
    public void getCpuUsage_resultShouldBeValid() {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue = systemCpuUsageDataCollector.getCpuUsage();

        assertThat(actualValue, is(notNullValue()));
        assertThat((double) actualValue.getUser(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getSystem(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getNice(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getIdle(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getIrq(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getSoftIrq(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
        assertThat((double) actualValue.getSteal(),
                is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
    }

    /**
     * Verifies that when when we do not have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getCpuUsage()} is called, the returned {@link CpuUsageData.CpuStat}
     * should be {@code null}.
     */
    @Test
    public void getCpuUsage_resultShouldBeNullWithoutRootAccess() {
        InstrumentedTestRequirements.assumeCpuApiLevelFail();

        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final CpuUsageData.CpuStat actualValue = systemCpuUsageDataCollector.getCpuUsage();
        assertThat(actualValue, is(nullValue()));
    }

    /**
     * Verifies that when when we do have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getCoresUsage(java.io.RandomAccessFile, int)} is called, the returned {
     *
     * @throws IOException when an I/O error happens.
     * @link CpuUsageData.CpuStat} should be not {@code null}.
     */
    @Test
    public void getCoresUsage_resultShouldNotBeNull() throws IOException {
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/stat", "r");
        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final ArrayList<CpuUsageData.CpuStat> actualValue = systemCpuUsageDataCollector.getCoresUsage(randomAccessFile,
                SystemCpuUsageDataCollector.getNumberOfCores());
        assertThat(actualValue, is(notNullValue()));
        assertThat(actualValue.size(), greaterThan(0));
    }

    /**
     * Verifies that when when we do not have access to the proc/stat files and
     * {@link SystemCpuUsageDataCollector#getCoresUsage(java.io.RandomAccessFile, int)} is called, the returned
     * {@link CpuUsageData.CpuStat} should be {@code null}.
     *
     * @throws FileNotFoundException when the File cannot be found.
     */
    @Test(expected = FileNotFoundException.class)
    public void getCoresUsage_resultShouldBeException() throws FileNotFoundException {
        InstrumentedTestRequirements.assumeCpuApiLevelFail();

        new RandomAccessFile("/proc/stat", "r");
    }
}
