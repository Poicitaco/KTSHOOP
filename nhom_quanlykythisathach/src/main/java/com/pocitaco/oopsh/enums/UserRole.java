package com.pocitaco.oopsh.enums;

/**
 * Enum representing different user roles in the driving license examination
 * system
 */
public enum UserRole {
    ADMIN("Quản trị viên", "admin"),
    EXAMINER("Giám thị", "examiner"),
    CANDIDATE("Thí sinh", "candidate");

    private final String displayName;
    private final String value;

    UserRole(String displayName, String value) {
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
