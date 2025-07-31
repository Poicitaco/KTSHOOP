package com.pocitaco.oopsh.enums;

/**
 * Enum representing different exam types (Theory/Practice)
 */
public enum ExamSubType {
    THEORY("Lý thuyết", "theory"),
    PRACTICE("Thực hành", "practice");

    private final String displayName;
    private final String value;

    ExamSubType(String displayName, String value) {
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
