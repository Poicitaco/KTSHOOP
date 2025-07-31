package com.pocitaco.oopsh.models;

import com.pocitaco.oopsh.enums.ResultStatus;

import java.time.LocalDate;

public class Result {
    private int id;
    private int userId;
    private int examTypeId;
    private double score;
    private LocalDate examDate;
    private ResultStatus status;
    private String candidateName; // For display purposes
    private String examTypeName; // For display purposes

    public Result() {
        this.examDate = LocalDate.now();
        this.status = ResultStatus.PENDING;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", userId=" + userId +
                ", examTypeId=" + examTypeId +
                ", score=" + score +
                ", examDate=" + examDate +
                ", status=" + status +
                ", candidateName='" + candidateName + '\'' +
                ", examTypeName='" + examTypeName + '\'' +
                '}';
    }
}
