package com.example.olineexaminationsystem;

public class Submission {

    private String username;
    private String question;
    private String answer;
    private String filePath;
    private int marks;

    public Submission(String username,
                      String question,
                      String answer,
                      String filePath,
                      int marks) {

        this.username = username;
        this.question = question;
        this.answer = answer;
        this.filePath = filePath;
        this.marks = marks;
    }

    @Override
    public String toString() {

        return username + "|" +
                question + "|" +
                answer + "|" +
                filePath + "|" +
                marks;
    }
}
