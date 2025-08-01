package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class MockExam {
    private int id;
    private String title;
    private String description;
    private int examTypeId;
    private int duration; // in minutes
    private String filePath;
    private LocalDate createdDate;
    private boolean isActive;
    private String createdBy;

    public MockExam() {
        this.createdDate = LocalDate.now();
        this.isActive = true;
    }

    public MockExam(int id, String title, String description, int examTypeId, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.examTypeId = examTypeId;
        this.duration = duration;
        this.createdDate = LocalDate.now();
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // Convenience methods for backward compatibility
    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String toString() {
        return "MockExam{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", examTypeId=" + examTypeId +
                ", duration=" + duration +
                ", createdDate=" + createdDate +
                '}';
    }
}
