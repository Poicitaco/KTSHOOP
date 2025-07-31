package com.pocitaco.oopsh.models;

import java.time.LocalDateTime;

public class SessionReport {
    private int id;
    private int examinerId;
    private int sessionId;
    private String reportContent;
    private String notes;
    private LocalDateTime createdDate;

    public SessionReport() {
        this.createdDate = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExaminerId() {
        return examinerId;
    }

    public void setExaminerId(int examinerId) {
        this.examinerId = examinerId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "SessionReport{" +
                "id=" + id +
                ", examinerId=" + examinerId +
                ", sessionId=" + sessionId +
                ", reportContent='" + reportContent + '\'' +
                ", notes='" + notes + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
