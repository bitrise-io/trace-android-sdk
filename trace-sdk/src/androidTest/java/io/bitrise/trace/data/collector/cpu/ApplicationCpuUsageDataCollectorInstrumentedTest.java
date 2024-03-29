package io.bitrise.trace.data.collector.cpu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import org.junit.Test;

/**
 * Instrumented tests for {@link ApplicationCpuUsageDataCollector}.
 */
public class ApplicationCpuUsageDataCollectorInstrumentedTest
    extends BaseDataCollectorInstrumentedTest {

  private static final double HUNDRED_PERCENT = 100d;
  private static final double ZERO_PERCENT = 0d;
  private final ApplicationCpuUsageDataCollector collector = new ApplicationCpuUsageDataCollector();

  /**
   * Verifies that when {@link ApplicationCpuUsageDataCollector#collectData()} is called, the
   * returned usage should not be {@code null}.
   */
  @Test
  public void collectData_applicationUsageShouldBeValid() {
    final Double actual = (Double) collector.collectData().getContent();
    assertThat(actual,
        is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
  }

  /**
   * Verifies that when  {@link SystemCpuUsageDataCollector#collectData()} is called, the
   * returned usage value should be greater than zero.
   */
  @Test
  public void collectData_applicationUserUsageShouldBePositive() {
    final Double actualValue = (Double) collector.collectData().getContent();
    assertThat(actualValue, is(greaterThanOrEqualTo(0d)));
  }

  @Test
  public void parsePidCpuStat_ValidInput() {
    final String line =
        "10267 (e.trace.testapp) R 1863 1863 0 0 -1 4211008 7678 0 85 0 49 10 0 0 10 -10 34 0 "
            + "118182231 1516228608 20270 18446744073709551615 1661407232 1661423840 4286766912 "
            + "4286744272 4033760474 0 4612";

    final CpuUsageData.PidCpuStat actual =
        collector.parsePidCpuStat(line).getPidCpuStat();
    final CpuUsageData.PidCpuStat expected =
        new CpuUsageData.PidCpuStat(49f, 10f, 0f, 0f, 1.18182232E8f);

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void parsePidCpuStat_InvalidInput() {
    final String line = "something that is not cool";
    final CpuUsageData.PidCpuStatWithUptime actual = collector.parsePidCpuStat(line);
    assertThat(actual, is(nullValue()));
  }

  @Test
  public void parsePidCpuStat_NullInput() {
    final String line = null;
    final CpuUsageData.PidCpuStatWithUptime actual = collector.parsePidCpuStat(line);
    assertThat(actual, is(nullValue()));
  }

  @Test
  public void getApplicationCpuUsage_ShouldBeNotNull() {
    final CpuUsageData.PidCpuStatWithUptime actual = collector.getApplicationCpuUsage();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getApplicationCpuUsagePercentage_ShouldBeValid() {
    final Double actual = collector.getApplicationCpuUsagePercentage(
        collector.previousAverageApplicationStats);
    assertThat(actual,
        is(both(greaterThanOrEqualTo(ZERO_PERCENT)).and(lessThanOrEqualTo(HUNDRED_PERCENT))));
  }
}
