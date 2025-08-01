package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class Certificate {
    private int id;
    private int candidateId;
    private int examId;
    private int examTypeId;
    private String certificateNumber;
    private String name;
    private double score;
    private String grade;
    private LocalDate issuedDate;
    private LocalDate issueDate; // Alternative name
    private LocalDate validUntil;
    private String examTypeName; // For display purposes

    public Certificate() {
        this.issuedDate = LocalDate.now();
        this.issueDate = LocalDate.now();
    }

    public Certificate(int candidateId, int examTypeId, String certificateNumber, double score) {
        this.candidateId = candidateId;
        this.examTypeId = examTypeId;
        this.certificateNumber = certificateNumber;
        this.score = score;
        this.issuedDate = LocalDate.now();
        this.issueDate = LocalDate.now();
        this.validUntil = LocalDate.now().plusYears(3); // Valid for 3 years
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public void setUserId(int userId) {
        this.candidateId = userId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
        this.issueDate = issuedDate; // Keep both in sync
    }

    public LocalDate getIssueDate() {
        return issueDate != null ? issueDate : issuedDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        this.issuedDate = issueDate; // Keep both in sync
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getExamTypeName() {
        return examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", candidateId=" + candidateId +
                ", examTypeId=" + examTypeId +
                ", certificateNumber='" + certificateNumber + '\'' +
                ", score=" + score +
                ", grade='" + grade + '\'' +
                ", issuedDate=" + issuedDate +
                '}';
    }
}
