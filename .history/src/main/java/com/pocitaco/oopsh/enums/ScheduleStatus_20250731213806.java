package com.pocitaco.oopsh.enums;

/**
 * Enum representing schedule status
 */
public enum ScheduleStatus {
    OPEN("Mở đăng ký", "open"),
    SCHEDULED("Đã lên lịch", "scheduled"),
    IN_PROGRESS("Đang diễn ra", "in_progress"),
    COMPLETED("Hoàn thành", "completed"),
    CANCELLED("Đã hủy", "cancelled");

    private final String displayName;
    private final String value;

    ScheduleStatus(String displayName, String value) {
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
