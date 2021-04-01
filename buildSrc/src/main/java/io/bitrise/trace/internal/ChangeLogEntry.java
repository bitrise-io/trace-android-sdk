package io.bitrise.trace.internal;

/**
 * Inner data class for change log entries.
 */
final class ChangeLogEntry {

    private final String type;
    private final String title;
    private final String details;

    public ChangeLogEntry(final String type, final String title, final String details) {
        this.type = type;
        this.title = title;
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return String.format("* %s: **%s:** %s", type, title, details);
    }
}
