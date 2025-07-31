package com.pocitaco.oopsh.enums;

/**
 * Enum representing registration status
 */
public enum RegistrationStatus {
    REGISTERED("Đã đăng ký", "registered"),
    CONFIRMED("Đã xác nhận", "confirmed"),
    COMPLETED("Hoàn thành", "completed"),
    CANCELLED("Đã hủy", "cancelled");

    private final String displayName;
    private final String value;

    RegistrationStatus(String displayName, String value) {
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
