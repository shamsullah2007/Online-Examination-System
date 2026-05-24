package com.example.olineexaminationsystem;

public class Question {


        private String question;
        private int marks;

        public Question(String question,
                        int marks) {

            this.question = question;
            this.marks = marks;
        }

    @Override
    public String toString() {
        return this.question+" | "+this.marks;
    }
}
