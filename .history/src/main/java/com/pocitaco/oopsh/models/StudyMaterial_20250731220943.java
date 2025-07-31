package com.pocitaco.oopsh.models;

import java.time.LocalDateTime;

public class StudyMaterial {
    private int id;
    private String title;
    private String description;
    private String fileUrl;
    private LocalDateTime createdDate;

    public StudyMaterial() {
        this.createdDate = LocalDateTime.now();
    }

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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "StudyMaterial{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
