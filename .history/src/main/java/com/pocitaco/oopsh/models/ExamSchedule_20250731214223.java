package com.pocitaco.oopsh.models;

import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExamSchedule {
    private int id;
    private int examTypeId;
    private int examinerId;
    private LocalDate examDate;
    private TimeSlot timeSlot;
    private int maxCandidates;
    private int registeredCandidates;
    private ScheduleStatus status;
    private String location;
    private LocalDateTime createdDate;
    private String examTypeName; // For display purposes

    public ExamSchedule() {
        this.createdDate = LocalDateTime.now();
        this.status = ScheduleStatus.SCHEDULED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public int getExaminerId() {
        return examinerId;
    }

    public void setExaminerId(int examinerId) {
        this.examinerId = examinerId;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(int maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

    public int getRegisteredCandidates() {
        return registeredCandidates;
    }

    public void setRegisteredCandidates(int registeredCandidates) {
        this.registeredCandidates = registeredCandidates;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getExamTypeName() {
        return examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    @Override
    public String toString() {
        return "ExamSchedule{" +
                "id=" + id +
                ", examTypeId=" + examTypeId +
                ", examinerId=" + examinerId +
                ", examDate=" + examDate +
                ", timeSlot=" + timeSlot +
                ", maxCandidates=" + maxCandidates +
                ", registeredCandidates=" + registeredCandidates +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", examTypeName='" + examTypeName + '\'' +
                '}';
    }
}
