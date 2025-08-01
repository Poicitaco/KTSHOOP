package com.pocitaco.oopsh.models;

import com.pocitaco.oopsh.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class Payment {
    private int id;
    private int userId;
    private int examTypeId;
    private int registrationId;
    private double amount;
    private LocalDateTime paymentDate;
    private LocalDate dueDate;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private String description;

    public Payment() {
        this.status = PaymentStatus.PENDING;
    }

    public Payment(int userId, int registrationId, double amount) {
        this.userId = userId;
        this.registrationId = registrationId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.dueDate = LocalDate.now().plusDays(7); // Default 7 days due
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Convenience methods for String status handling
    public String getStatusAsString() {
        return status != null ? status.toString() : "UNKNOWN";
    }

    public void setStatus(String statusString) {
        try {
            this.status = PaymentStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + userId +
                ", registrationId=" + registrationId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", status=" + status +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
