package com.exam.models;

public class Option {
    private int     id, questionId;
    private String  optionText;
    private boolean isCorrect;

    // Constructor for loading from database
    public Option(int id, int questionId, String optionText, boolean isCorrect) {
        this.id         = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect  = isCorrect;
    }

    // Getters
    public int     getId()          { return id; }
    public int     getQuestionId()  { return questionId; }
    public String  getOptionText()  { return optionText; }
    public boolean isCorrect()      { return isCorrect; }
}