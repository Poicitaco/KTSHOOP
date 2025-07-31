package com.pocitaco.oopsh.enums;

/**
 * Enum representing different exam status
 */
public enum ExamStatus {
    REGISTRATION_OPEN("Mở đăng ký", "registration_open"),
    REGISTRATION_CLOSED("Đóng đăng ký", "registration_closed"),
    IN_PROGRESS("Đang thi", "in_progress"),
    COMPLETED("Hoàn thành", "completed"),
    CANCELLED("Hủy bỏ", "cancelled");

    private final String displayName;
    private final String value;

    ExamStatus(String displayName, String value) {
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
