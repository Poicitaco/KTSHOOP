package com.pocitaco.oopsh.enums;

/**
 * Enum representing result status
 */
public enum ResultStatus {
    PASSED("Đậu", "passed"),
    FAILED("Rớt", "failed"),
    ABSENT("Vắng mặt", "absent"),
    PENDING("Chờ kết quả", "pending");

    private final String displayName;
    private final String value;

    ResultStatus(String displayName, String value) {
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
