package io.bitrise.trace.data.collector.cpu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

/**
 * Unit tests for {@link SystemCpuUsageDataCollector}.
 */
public class SystemCpuUsageDataCollectorTest {

  private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_1 =
      new CpuUsageData.CpuStat(10, 10, 10, 10, 10, 10, 10,
          10);
  private static final CpuUsageData.CpuStat DUMMY_CPU_STAT_2 =
      new CpuUsageData.CpuStat(3, 3, 3, 3, 3, 3, 3, 3);

  @Test
  public void parseSystemCpuStat_ValidInput() {
    final CpuUsageData.CpuStat actual =
        SystemCpuUsageDataCollector.parseSystemCpuStat(3, " cpu2 23456 2345 2345 "
            + "2345 234 0 0 0 0 0");
    assertThat(actual,
        equalTo(new CpuUsageData.CpuStat(2345f, 2345f, 2345f, 234f, 0f, 0f, 0f, 0f)));
  }

  @Test
  public void parseSystemCpuStat_InvalidInput() {
    final CpuUsageData.CpuStat actual =
        SystemCpuUsageDataCollector.parseSystemCpuStat(0, " cpu2 23456 2345 2345 "
            + "2345 234 0 0 0 0 0");
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
    final CpuUsageData.CpuStat actual =
        SystemCpuUsageDataCollector.getStatPercentages(DUMMY_CPU_STAT_1, 10);
    assertThat(actual, equalTo(new CpuUsageData.CpuStat(100, 100, 100, 100, 100, 100, 100, 100)));
  }

}
