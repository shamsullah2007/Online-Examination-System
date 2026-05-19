package com.exam.models;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int          id, examId, marks;
    private String       text, topic;
    private List<Option> options;
    private int          selectedOptionId = -1; // -1 means not yet answered

    // Constructor for loading from database
    public Question(int id, int examId, String text, int marks, String topic) {
        this.id      = id;
        this.examId  = examId;
        this.text    = text;
        this.marks   = marks;
        this.topic   = topic;
        this.options = new ArrayList<>();
    }

    // Returns true only if the student has selected an answer
    public boolean isAnswered() {
        return selectedOptionId != -1;
    }

    // Getters
    public int          getId()               { return id; }
    public int          getExamId()           { return examId; }
    public String       getText()             { return text; }
    public int          getMarks()            { return marks; }
    public String       getTopic()            { return topic; }
    public List<Option> getOptions()          { return options; }
    public int          getSelectedOptionId() { return selectedOptionId; }

    // Setters
    public void setOptions(List<Option> options)       { this.options          = options; }
    public void setSelectedOptionId(int selectedOptId) { this.selectedOptionId = selectedOptId; }
}