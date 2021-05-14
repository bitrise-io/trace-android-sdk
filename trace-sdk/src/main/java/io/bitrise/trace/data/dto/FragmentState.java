package io.bitrise.trace.data.dto;

/**
 * Enum for holing the possible states in a Fragments lifecycle.
 */
public enum FragmentState {
  ATTACHED,
  CREATED,
  VIEW_CREATED,
  ACTIVITY_CREATED,
  STARTED,
  RESUMED,
  PAUSED,
  STOPPED,
  VIEW_DESTROYED,
  DESTROYED,
  DETACHED,
}
