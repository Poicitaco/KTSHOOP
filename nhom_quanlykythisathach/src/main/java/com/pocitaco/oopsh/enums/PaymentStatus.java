package com.pocitaco.oopsh.enums;

/**
 * Enum representing payment status
 */
public enum PaymentStatus {
    PENDING("Chờ thanh toán", "pending"),
    PAID("Đã thanh toán", "paid"),
    FAILED("Thất bại", "failed"),
    REFUNDED("Đã hoàn tiền", "refunded");

    private final String displayName;
    private final String value;

    PaymentStatus(String displayName, String value) {
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
