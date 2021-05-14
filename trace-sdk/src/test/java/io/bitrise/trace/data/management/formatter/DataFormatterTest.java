package io.bitrise.trace.data.management.formatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.test.TraceTestProvider;
import io.opencensus.proto.trace.v1.Span;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link DataFormatter}.
 */
public class DataFormatterTest {

  private static final String DUMMY_TRACE_ID = "ABCDEF";
  private final DataFormatter mockDataFormatter =
      mock(DataFormatter.class, Mockito.CALLS_REAL_METHODS);

  @Test
  public void getTimestamp() {
    final Timestamp output = mockDataFormatter.getTimestamp();
    assertNotNull(output);
  }

  /**
   * When the {@link DataFormatter#toFormattedDataArray(List)} is called, the returned Array of
   * {@link FormattedData} should contain the same Spans.
   */
  @Test
  public void toFormattedDataArray_ListShouldContainTheSameSpans() {
    final Span span1 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span1");
    final Span span2 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span2");
    final Span span3 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span3");
    final List<Span> spanList = new ArrayList<Span>() {{
      add(span1);
      add(span2);
      add(span3);
    }};
    final FormattedData[] formattedDataArray = DataFormatter.toFormattedDataArray(spanList);
    for (int i = 0; i < formattedDataArray.length; i++) {
      assertThat(formattedDataArray[i].getSpan(), is(spanList.get(i)));
    }
  }

  /**
   * When the {@link DataFormatter#toFormattedDataArray(Span...)} is called, the returned Array of
   * {@link FormattedData} should contain the same {@link Span}s.
   */
  @Test
  public void toFormattedDataArray_ArrayShouldContainTheSameSpans() {
    final Span span1 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span1");
    final Span span2 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span2");
    final Span span3 = TraceTestProvider.getSampleSpan(DUMMY_TRACE_ID, "span3");
    final Span[] spans = new Span[] {span1, span2, span3};
    final FormattedData[] formattedDataArray = DataFormatter.toFormattedDataArray(spans);
    for (int i = 0; i < formattedDataArray.length; i++) {
      assertThat(formattedDataArray[i].getSpan(), is(spans[i]));
    }
  }
}
