package com.exam.models;

public class Exam {
    private int     id, durationMinutes, totalMarks, passMarks, createdBy;
    private String  title, description, scheduledAt;
    private boolean isActive;

    // Constructor for creating new exam (admin form — no id yet)
    public Exam(String title, String description, int durationMinutes,
                int totalMarks, int passMarks, String scheduledAt,
                int createdBy, boolean isActive) {
        this.title           = title;
        this.description     = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks      = totalMarks;
        this.passMarks       = passMarks;
        this.scheduledAt     = scheduledAt;
        this.createdBy       = createdBy;
        this.isActive        = isActive;
    }

    // Constructor for loading from database (includes id)
    public Exam(int id, String title, String description, int durationMinutes,
                int totalMarks, int passMarks, String scheduledAt,
                int createdBy, boolean isActive) {
        this(title, description, durationMinutes, totalMarks,
                passMarks, scheduledAt, createdBy, isActive);
        this.id = id;
    }

    // Getters
    public int     getId()              { return id; }
    public String  getTitle()           { return title; }
    public String  getDescription()     { return description; }
    public int     getDurationMinutes() { return durationMinutes; }
    public int     getTotalMarks()      { return totalMarks; }
    public int     getPassMarks()       { return passMarks; }
    public String  getScheduledAt()     { return scheduledAt; }
    public int     getCreatedBy()       { return createdBy; }
    public boolean isActive()           { return isActive; }

    // Setters
    public void setActive(boolean active)       { this.isActive        = active; }
    public void setTitle(String title)          { this.title           = title; }
    public void setDescription(String desc)     { this.description     = desc; }
    public void setScheduledAt(String time)     { this.scheduledAt     = time; }
}