package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class Certificate {
    private int id;
    private int candidateId;
    private int examId;
    private String certificateNumber;
    private double score;
    private String grade;
    private LocalDate issuedDate;
    private String examTypeName; // For display purposes

    public Certificate() {
        this.issuedDate = LocalDate.now();
    }

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

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
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
                ", examId=" + examId +
                ", certificateNumber='" + certificateNumber + '\'' +
                ", score=" + score +
                ", grade='" + grade + '\'' +
                ", issuedDate=" + issuedDate +
                ", examTypeName='" + examTypeName + '\'' +
                '}';
    }
}
