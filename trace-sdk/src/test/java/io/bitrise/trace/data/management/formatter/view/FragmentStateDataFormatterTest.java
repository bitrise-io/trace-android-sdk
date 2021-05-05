package io.bitrise.trace.data.management.formatter.view;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.bitrise.trace.data.collector.view.FragmentStateDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.dto.FragmentData;
import io.bitrise.trace.data.dto.FragmentDataStateEntry;
import io.bitrise.trace.data.dto.FragmentState;
import io.bitrise.trace.utils.ByteStringConverter;
import io.opencensus.proto.trace.v1.Span;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test for the {@link FragmentStateDataFormatter} class.
 */
public class FragmentStateDataFormatterTest {

    private static final String DUMMY_SPAN_ID = "0123456789ABCDEF";
    private static final String DUMMY_PARENT_SPAN_ID = "ABCDEF0123456789";
    private static final String DUMMY_FRAGMENT_NAME = "ExampleName";

    private FragmentDataStateEntry fragmentDataStateEntryCreated1 =
            new FragmentDataStateEntry(FragmentState.VIEW_CREATED, 1000);
    private FragmentDataStateEntry fragmentDataStateEntryCreated2 =
            new FragmentDataStateEntry(FragmentState.VIEW_CREATED, 2000);
    private FragmentDataStateEntry fragmentDataStateEntryCreated3 =
            new FragmentDataStateEntry(FragmentState.VIEW_CREATED, 3000);

    private FragmentDataStateEntry fragmentDataStateEntryPaused1 =
            new FragmentDataStateEntry(FragmentState.PAUSED, 1500);
    private FragmentDataStateEntry fragmentDataStateEntryPaused2 =
            new FragmentDataStateEntry(FragmentState.PAUSED, 2500);
    private FragmentDataStateEntry fragmentDataStateEntryPaused3 =
            new FragmentDataStateEntry(FragmentState.PAUSED, 3500);

    private FragmentStateDataFormatter formatter = new FragmentStateDataFormatter();

    /**
     * When the input data timestamp is before all of the timestamps of {@link FragmentDataStateEntry}s, it should
     * return 0.
     */
    @Test
    public void getFirstValidPausedIndex_ShouldBeZero() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused1);
            add(fragmentDataStateEntryPaused2);
        }};
        final int actualValue = FragmentStateDataFormatter.getFirstValidPausedIndex(1000, fragmentDataStateEntries);
        assertThat(actualValue, is(0));
    }

    /**
     * When the input data timestamp is between the timestamps of {@link FragmentDataStateEntry}s, it should return a
     * number greater than zero.
     */
    @Test
    public void getFirstValidPausedIndex_ShouldBeGreaterThanZero() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused1);
            add(fragmentDataStateEntryPaused2);
        }};
        final int actualValue = FragmentStateDataFormatter.getFirstValidPausedIndex(2000, fragmentDataStateEntries);
        assertThat(actualValue, greaterThan(0));
    }

    /**
     * When the input data timestamp is after all of the timestamps of {@link FragmentDataStateEntry}s, it should
     * return the {@link FragmentStateDataFormatter#INVALID_INDEX}.
     */
    @Test
    public void getFirstValidPausedIndex_ShouldBeInvalid() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused1);
            add(fragmentDataStateEntryPaused2);
        }};
        final int actualValue = FragmentStateDataFormatter.getFirstValidPausedIndex(3000, fragmentDataStateEntries);
        assertThat(actualValue, is(FragmentStateDataFormatter.INVALID_INDEX));
    }

    /**
     * The input List of {@link FragmentDataStateEntry}s should be sorted based on their timestamp in ascending order.
     */
    @Test
    public void sortFragmentDataStates_ShouldSortList() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused1);
            add(fragmentDataStateEntryPaused3);
        }};

        final List<FragmentDataStateEntry> expectedValue = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused1);
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused3);
        }};

        FragmentStateDataFormatter.sortFragmentDataStates(fragmentDataStateEntries);
        assertThat(fragmentDataStateEntries, contains(expectedValue.toArray(new FragmentDataStateEntry[]{})));
    }

    /**
     * When the first {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} happens after the first
     * FragmentDataStateEntry with {@link FragmentState#VIEW_CREATED}, it does not have to filter it and should
     * return the same List but ordered.
     */
    @Test
    public void validatePausedEntries_ShouldNotFilter() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated1);
                    add(fragmentDataStateEntryCreated3);
                    add(fragmentDataStateEntryCreated2);
                }};

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused3);
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused1);
        }};
        final List<FragmentDataStateEntry> actualValue = FragmentStateDataFormatter.validatePausedEntries(
                "fragmentName", viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);

        assertThat(actualValue, contains(pausedFragmentDataStateEntries.toArray(new FragmentDataStateEntry[]{})));
    }

    /**
     * When the some of the {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} happens before the first
     * FragmentDataStateEntry with {@link FragmentState#VIEW_CREATED}, it should filter it and should return
     * the filtered List.
     */
    @Test
    public void validatePausedEntries_ShouldFilter() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated3);
                    add(fragmentDataStateEntryCreated2);
                }};

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused3);
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused1);
        }};

        final List<FragmentDataStateEntry> actualValue =
                FragmentStateDataFormatter.validatePausedEntries("fragmentName",
                        viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);
        final List<FragmentDataStateEntry> expectedValue = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused3);
        }};

        assertThat(actualValue, contains(expectedValue.toArray(new FragmentDataStateEntry[]{})));
    }

    /**
     * When the all of the {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} happens before the first
     * FragmentDataStateEntry with {@link FragmentState#VIEW_CREATED}, it should return an empty List.
     */
    @Test
    public void validatePausedEntries_ShouldReturnEmpty() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated3);
                }};

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused1);
        }};

        final List<FragmentDataStateEntry> actualValue =
                FragmentStateDataFormatter.validatePausedEntries("fragmentName",
                        viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);
        assertThat(actualValue.size(), is(0));
    }

    /**
     * When there are {@link FragmentDataStateEntry}s with {@link FragmentState#VIEW_CREATED} and
     * {@link FragmentState#PAUSED} both, it should return {@code true}.
     */
    @Test
    public void validateEntries_ShouldBeValidWithEntries() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated2);
                }};

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused3);
        }};

        final boolean actualValue = FragmentStateDataFormatter.validateEntries(DUMMY_FRAGMENT_NAME,
                viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);

        assertThat(actualValue, is(true));
    }

    /**
     * When there are no {@link FragmentDataStateEntry}s with {@link FragmentState#VIEW_CREATED} it should return
     * {@code false}.
     */
    @Test
    public void validateEntries_ShouldBeInvalidWithoutViewCreatedEntries() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries = new ArrayList<>();

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused3);
            add(fragmentDataStateEntryPaused2);
            add(fragmentDataStateEntryPaused1);
        }};

        final boolean actualValue = FragmentStateDataFormatter.validateEntries(DUMMY_FRAGMENT_NAME,
                viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);

        assertThat(actualValue, is(false));
    }

    /**
     * When there are no {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} it should return {@code
     * false}.
     */
    @Test
    public void validateEntries_ShouldBeInvalidWithoutPausedEntries() {
        final List<FragmentDataStateEntry> viewCreatedFragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated2);
                    add(fragmentDataStateEntryCreated3);
                    add(fragmentDataStateEntryCreated1);
                }};

        final List<FragmentDataStateEntry> pausedFragmentDataStateEntries = new ArrayList<>();

        final boolean actualValue = FragmentStateDataFormatter.validateEntries(DUMMY_FRAGMENT_NAME,
                viewCreatedFragmentDataStateEntries, pausedFragmentDataStateEntries);

        assertThat(actualValue, is(false));
    }

    /**
     * Should return from a List of {@link FragmentDataStateEntry}s only the ones with
     * {@link FragmentState#VIEW_CREATED}.
     */
    @Test
    public void findAllByState_ShouldReturnOnlyViewCreated() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated2);
                    add(fragmentDataStateEntryPaused1);
                    add(fragmentDataStateEntryCreated1);
                }};

        final List<FragmentDataStateEntry> expectedValue = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryCreated2);
            add(fragmentDataStateEntryCreated1);
        }};

        final List<FragmentDataStateEntry> actualValue = FragmentStateDataFormatter.findAllByState(
                fragmentDataStateEntries,
                FragmentState.VIEW_CREATED);
        assertThat(actualValue, containsInAnyOrder(expectedValue.toArray(new FragmentDataStateEntry[]{})));
    }

    /**
     * Should return from a List of {@link FragmentDataStateEntry}s only the ones with {@link FragmentState#PAUSED}.
     */
    @Test
    public void findAllByState_ShouldReturnOnlyPaused() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated2);
                    add(fragmentDataStateEntryPaused1);
                    add(fragmentDataStateEntryCreated1);
                }};

        final List<FragmentDataStateEntry> expectedValue = new ArrayList<FragmentDataStateEntry>() {{
            add(fragmentDataStateEntryPaused1);
        }};

        assertThat(FragmentStateDataFormatter.findAllByState(fragmentDataStateEntries,
                FragmentState.PAUSED), contains(expectedValue.toArray(new FragmentDataStateEntry[]{})));
    }

    /**
     * From the given {@link FragmentDataStateEntry}s input it should return the expected {@link Span}s as result.
     */
    @Test
    public void getFragmentSpans_ShouldCreateProperSpans() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated2);
                    add(fragmentDataStateEntryPaused1);
                    add(fragmentDataStateEntryCreated1);
                    add(fragmentDataStateEntryPaused2);
                }};

        final FragmentData fragmentData = new FragmentData(DUMMY_SPAN_ID);

        fragmentData.setName(DUMMY_FRAGMENT_NAME);
        fragmentData.setParentSpanId(DUMMY_PARENT_SPAN_ID);
        fragmentData.setStates(fragmentDataStateEntries);

        final List<Span> actualValue = FragmentStateDataFormatter.getFragmentSpans(fragmentData);

        final List<Span> expectedValue = new ArrayList<Span>() {{
            add(FragmentStateDataFormatter.createFragmentViewSpan(DUMMY_FRAGMENT_NAME, 1000,
                    1500, ByteStringConverter.toString(actualValue.get(0).getSpanId()),
                    DUMMY_PARENT_SPAN_ID));
            add(FragmentStateDataFormatter.createFragmentViewSpan(DUMMY_FRAGMENT_NAME, 2000,
                    2500, ByteStringConverter.toString(actualValue.get(1).getSpanId()),
                    DUMMY_PARENT_SPAN_ID));
        }};

        assertThat(actualValue, contains(expectedValue.toArray(new Span[]{})));
    }

    /**
     * Providing an empty input for {@link FragmentStateDataFormatter#getFragmentSpans(FragmentData)} should produce
     * an empty output.
     */
    @Test
    public void getFragmentSpans_ShouldBeEmpty() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries = new ArrayList<>();

        final FragmentData fragmentData = new FragmentData(DUMMY_SPAN_ID);

        fragmentData.setName(DUMMY_FRAGMENT_NAME);
        fragmentData.setStates(fragmentDataStateEntries);

        final List<Span> actualValue = FragmentStateDataFormatter.getFragmentSpans(fragmentData);
        assertThat(actualValue.size(), is(0));
    }

    @Test
    public void formatData_notMap() {
        final Data data = new Data(FragmentStateDataListener.class);
        data.setContent("kittens");
        assertArrayEquals(new FormattedData[]{}, formatter.formatData(data));
    }

    @Test
    public void formatData_oneSpan() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated1);
                    add(fragmentDataStateEntryPaused1);
                }};
        final FragmentData fragmentData = new FragmentData(DUMMY_SPAN_ID);
        fragmentData.setName(DUMMY_FRAGMENT_NAME);
        fragmentData.setParentSpanId(DUMMY_PARENT_SPAN_ID);
        fragmentData.setStates(fragmentDataStateEntries);

        final Map<Integer, FragmentData> map = new HashMap<>();
        map.put(1234, fragmentData);
        final Data data = new Data(FragmentStateDataListener.class);
        data.setContent(map);

        final FormattedData[] formattedData = formatter.formatData(data);

        assertEquals(1, formattedData.length);
    }

    @Test
    public void formatData_twoSpan() {
        final List<FragmentDataStateEntry> fragmentDataStateEntries =
                new ArrayList<FragmentDataStateEntry>() {{
                    add(fragmentDataStateEntryCreated1);
                    add(fragmentDataStateEntryPaused1);
                    add(fragmentDataStateEntryCreated2);
                    add(fragmentDataStateEntryPaused2);
                }};
        final FragmentData fragmentData = new FragmentData(DUMMY_SPAN_ID);
        fragmentData.setName(DUMMY_FRAGMENT_NAME);
        fragmentData.setParentSpanId(DUMMY_PARENT_SPAN_ID);
        fragmentData.setStates(fragmentDataStateEntries);

        final Map<Integer, FragmentData> map = new HashMap<>();
        map.put(1234, fragmentData);
        final Data data = new Data(FragmentStateDataListener.class);
        data.setContent(map);

        final FormattedData[] formattedData = formatter.formatData(data);

        assertEquals(2, formattedData.length);
    }
}
