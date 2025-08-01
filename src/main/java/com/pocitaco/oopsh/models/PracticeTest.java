package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class PracticeTest {
    private int id;
    private String title;
    private String description;
    private int examTypeId;
    private String difficulty;
    private int numberOfQuestions;
    private int duration; // in minutes
    private String filePath;
    private LocalDate createdDate;
    private boolean isActive;

    public PracticeTest() {
        this.createdDate = LocalDate.now();
        this.isActive = true;
    }

    public PracticeTest(int id, String title, String description, int examTypeId,
            String difficulty, int numberOfQuestions, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.examTypeId = examTypeId;
        this.difficulty = difficulty;
        this.numberOfQuestions = numberOfQuestions;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
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

    // Convenience methods
    public String getName() {
        return title; // For backward compatibility
    }

    public void setName(String name) {
        this.title = name; // For backward compatibility
    }

    @Override
    public String toString() {
        return "PracticeTest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                ", duration=" + duration +
                '}';
    }
}
