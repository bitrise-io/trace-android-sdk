package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


/**
 * Data class for storing the required data for listening for Fragment lifecycle events. It
 * should contain the following data:
 * <ul>
 *      <li>lifecycle events that the given Fragment reached, to be able to identify the start
 *      and the end of it</li>
 *      <li>the name of the Fragment to be able to provide a human readable title for it on the
 *      frontend</li>
 *      <li>the Span ID of the parent (if there is one), to be able to create a hierarchy
 *      betweenthem</li>
 *      <li>the Span ID of this Fragment</li>
 * </ul>
 *
 * <p>Example format:
 *   FragmentData{states={FragmentDataStateEntry{fragmentState=VIEW_CREATED, timeStamp=1191582961},
 *   name='ExampleName', parentSpanId='QWERTZUIOPASDFGH', spanId='ABCDEFGHIJKLMNOP'}
 */
public class FragmentData {

  /**
   * The Span ID for this FragmentData.
   */
  @NonNull
  private final String spanId;
  /**
   * The List of {@link FragmentDataStateEntry}s of the given Fragment.
   */
  @NonNull
  private List<FragmentDataStateEntry> states;
  /**
   * The name of the Fragment.
   */
  @Nullable
  private String name;
  /**
   * The ID of the parent Span.
   */
  @Nullable
  private String parentSpanId;

  /**
   * Constructor for class.
   *
   * @param spanId the ID of the Span that will be created from this.
   */
  public FragmentData(@NonNull final String spanId) {
    this.states = new ArrayList<>();
    this.spanId = spanId;
  }

  @Nullable
  public String getParentSpanId() {
    return parentSpanId;
  }

  public void setParentSpanId(@Nullable final String parentSpanId) {
    this.parentSpanId = parentSpanId;
  }

  @NonNull
  public List<FragmentDataStateEntry> getStates() {
    return states;
  }

  public void setStates(@NonNull final List<FragmentDataStateEntry> states) {
    this.states = states;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable final String name) {
    this.name = name;
  }

  @NonNull
  public String getSpanId() {
    return spanId;
  }

  /**
   * Adds the given {@link FragmentState} to the {@link #states}.
   *
   * @param fragmentState the FragmentState to put.
   * @param time          the time when the FragmentState was reached. See more info in the
   *                      javadoc of {@link #states}.
   */
  public void addState(@NonNull final FragmentState fragmentState, @NonNull final Long time) {
    states.add(new FragmentDataStateEntry(fragmentState, time));
  }

  /**
   * Adds a new {@link FragmentDataStateEntry} to the {@link #states}.
   *
   * @param entry the entry to add.
   */
  public void addState(@NonNull final FragmentDataStateEntry entry) {
    states.add(entry);
  }

  @Override
  public String toString() {
    return "FragmentData{"
        + "states=" + states
        + ", name='" + name + '\''
        + ", parentSpanId='" + parentSpanId + '\''
        + ", spanId='" + spanId + '\''
        + '}';
  }
}
