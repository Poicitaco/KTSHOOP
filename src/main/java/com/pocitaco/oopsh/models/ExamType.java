package com.pocitaco.oopsh.models;

import java.time.LocalDateTime;

/**
 * ExamType model class representing different types of driving licenses
 */
public class ExamType {
    private int id;
    private String code;
    private String name;
    private String description;
    private double fee;
    private int theoryPassScore;
    private int practicePassScore;
    private int duration; // Duration in minutes
    private int passingScore; // General passing score
    private String status;
    private LocalDateTime createdDate;

    // Constructors
    public ExamType() {
        this.status = "ACTIVE";
        this.createdDate = LocalDateTime.now();
    }

    public ExamType(String code, String name, String description, double fee,
            int theoryPassScore, int practicePassScore) {
        this();
        this.code = code;
        this.name = name;
        this.description = description;
        this.fee = fee;
        this.theoryPassScore = theoryPassScore;
        this.practicePassScore = practicePassScore;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getTheoryPassScore() {
        return theoryPassScore;
    }

    public void setTheoryPassScore(int theoryPassScore) {
        this.theoryPassScore = theoryPassScore;
    }

    public int getPracticePassScore() {
        return practicePassScore;
    }

    public void setPracticePassScore(int practicePassScore) {
        this.practicePassScore = practicePassScore;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ExamType examType = (ExamType) obj;
        return id == examType.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
