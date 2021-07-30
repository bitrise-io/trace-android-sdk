package io.bitrise.trace;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Tests for {@link TraceOptionsUtil}.
 */
public class TraceOptionsUtilTests {

  @Test
  public void determineIfNetworkUrlConnectionTracing_nullOptions() {
    assertTrue(TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(null));
  }

  @Test
  public void determineIfNetworkUrlConnectionTracing_emptyOptions() {
    assertTrue(TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(new ArrayList<>()));
  }

  @Test
  public void determineIfNetworkUrlConnectionTracing_optionKeyTrue() {
    final List<TraceOption> options = new ArrayList<>();
    options.add(new TraceOption.NetworkUrlConnectionTracing(true));

    assertTrue(TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(options));
  }

  @Test
  public void determineIfNetworkUrlConnectionTracing_optionKeyFalse() {
    final List<TraceOption> options = new ArrayList<>();
    options.add(new TraceOption.NetworkUrlConnectionTracing(false));

    assertFalse(TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(options));
  }

  @Test
  public void determineIfNetworkUrlConnectionTracing_otherOptions() {
    final List<TraceOption> options = new ArrayList<>();
    options.add(new DummyOption());

    assertTrue(TraceOptionsUtil.determineIfNetworkUrlConnectionTracing(options));
  }


  /**
   * private test {@link TraceOption} class.
   */
  protected static class DummyOption extends TraceOption {
    public DummyOption() {
      super("a value");
    }
  }

}
