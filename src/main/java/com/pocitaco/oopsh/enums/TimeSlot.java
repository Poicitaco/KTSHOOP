package com.pocitaco.oopsh.enums;

/**
 * Enum representing time slots for exams
 */
public enum TimeSlot {
    MORNING("Buổi sáng", "morning"),
    AFTERNOON("Buổi chiều", "afternoon"),
    EVENING("Buổi tối", "evening");

    private final String displayName;
    private final String value;

    TimeSlot(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
