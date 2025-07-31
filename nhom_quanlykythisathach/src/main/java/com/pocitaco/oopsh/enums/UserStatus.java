package com.pocitaco.oopsh.enums;

/**
 * Enum representing different user status
 */
public enum UserStatus {
    ACTIVE("Hoạt động", "active"),
    INACTIVE("Không hoạt động", "inactive"),
    SUSPENDED("Tạm khóa", "suspended");

    private final String displayName;
    private final String value;

    UserStatus(String displayName, String value) {
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
