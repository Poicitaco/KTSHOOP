package com.pocitaco.oopsh.models;

import java.time.LocalDate;

public class StudyMaterial {
    private int id;
    private String title;
    private String description;
    private String fileUrl;
    private String filePath;
    private int examTypeId;
    private String materialType;
    private LocalDate uploadDate;
    private String uploadedBy;
    private boolean isActive;

    public StudyMaterial() {
        this.uploadDate = LocalDate.now();
        this.isActive = true;
    }

    public StudyMaterial(String title, String description, int examTypeId, String materialType) {
        this.title = title;
        this.description = description;
        this.examTypeId = examTypeId;
        this.materialType = materialType;
        this.uploadDate = LocalDate.now();
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // For backward compatibility
    public java.time.LocalDateTime getCreatedDate() {
        return uploadDate.atStartOfDay();
    }

    public void setCreatedDate(java.time.LocalDateTime createdDate) {
        this.uploadDate = createdDate.toLocalDate();
    }

    @Override
    public String toString() {
        return "StudyMaterial{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", examTypeId=" + examTypeId +
                ", materialType='" + materialType + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }
}
