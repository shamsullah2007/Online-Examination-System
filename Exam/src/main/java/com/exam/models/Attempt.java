package com.exam.models;

public class Attempt {
    private int     id, userId, examId, score;
    private String  startedAt, submittedAt;
    private boolean isPassed;

    // Constructor for loading from database
    public Attempt(int id, int userId, int examId, String startedAt,
                   String submittedAt, int score, boolean isPassed) {
        this.id          = id;
        this.userId      = userId;
        this.examId      = examId;
        this.startedAt   = startedAt;
        this.submittedAt = submittedAt;
        this.score       = score;
        this.isPassed    = isPassed;
    }

    // Getters
    public int     getId()          { return id; }
    public int     getUserId()      { return userId; }
    public int     getExamId()      { return examId; }
    public int     getScore()       { return score; }
    public String  getStartedAt()   { return startedAt; }
    public String  getSubmittedAt() { return submittedAt; }
    public boolean isPassed()       { return isPassed; }

    // Setters
    public void setScore(int score)           { this.score    = score; }
    public void setPassed(boolean passed)     { this.isPassed = passed; }
    public void setSubmittedAt(String time)   { this.submittedAt = time; }
}