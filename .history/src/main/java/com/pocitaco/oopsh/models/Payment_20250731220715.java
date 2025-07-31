package com.pocitaco.oopsh.models;

import com.pocitaco.oopsh.enums.PaymentStatus;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int userId;
    private int examTypeId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + userId +
                ", examTypeId=" + examTypeId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", status=" + status +
                '}';
    }
}
