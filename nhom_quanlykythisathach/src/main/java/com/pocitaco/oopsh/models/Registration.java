package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class Registration {
    private int id;
    private int userId;
    private int examTypeId;
    private LocalDate registrationDate;
    private String status;
    private String examTypeName; // For display purposes
    private String candidateName; // For display purposes

    public Registration() {
        this.registrationDate = LocalDate.now();
        this.status = "PENDING";
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExamTypeName() {
        return examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", userId=" + userId +
                ", examTypeId=" + examTypeId +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                ", examTypeName='" + examTypeName + '\'' +
                ", candidateName='" + candidateName + '\'' +
                '}';
    }
}
