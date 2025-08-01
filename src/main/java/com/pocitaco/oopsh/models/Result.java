package com.pocitaco.oopsh.models;

import com.pocitaco.oopsh.enums.ResultStatus;

import java.time.LocalDate;

public class Result {
    private int id;
    private int userId;
    private int examTypeId;
    private double score;
    private double theoryScore;
    private double practicalScore;
    private double totalScore;
    private LocalDate examDate;
    private ResultStatus status;
    private String notes;
    private String candidateName; // For display purposes
    private String examTypeName; // For display purposes

    public Result() {
        this.examDate = LocalDate.now();
        this.status = ResultStatus.PENDING;
    }

    public Result(int userId, int examTypeId) {
        this.userId = userId;
        this.examTypeId = examTypeId;
        this.examDate = LocalDate.now();
        this.status = ResultStatus.PENDING;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getTheoryScore() {
        return theoryScore;
    }

    public void setTheoryScore(double theoryScore) {
        this.theoryScore = theoryScore;
        updateTotalScore();
    }

    public double getPracticalScore() {
        return practicalScore;
    }

    public void setPracticalScore(double practicalScore) {
        this.practicalScore = practicalScore;
        updateTotalScore();
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
        this.score = totalScore; // Keep legacy score field updated
    }

    private void updateTotalScore() {
        this.totalScore = (this.theoryScore + this.practicalScore) / 2;
        this.score = this.totalScore;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getExamTypeName() {
        return examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    // Convenience method for status as string
    public String getStatusAsString() {
        return status != null ? status.toString() : "UNKNOWN";
    }

    public void setStatus(String statusString) {
        try {
            this.status = ResultStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            this.status = ResultStatus.PENDING;
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", userId=" + userId +
                ", examTypeId=" + examTypeId +
                ", theoryScore=" + theoryScore +
                ", practicalScore=" + practicalScore +
                ", totalScore=" + totalScore +
                ", examDate=" + examDate +
                ", status=" + status +
                '}';
    }
}
