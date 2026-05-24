package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class StudentDashboard extends Application {

    private String username;

    public StudentDashboard(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage stage) {

        Label welcome = new Label("Welcome, " + username);
        welcome.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ─── Calculate this student's total obtained marks ────────
        int obtainedMarks = 0;

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("submissions.txt"));

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");

                // format: username|question|answer|filePath|marks
                if (parts.length >= 5 && parts[0].equals(username)) {
                    obtainedMarks += Integer.parseInt(parts[4].trim());
                }
            }

            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ─── Calculate total exam marks from questions.txt ────────
        int totalMarks = 0;

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("questions.txt"));

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                // format: "question text | marks"
                String[] parts = line.split("\\|");

                if (parts.length >= 2) {
                    totalMarks += Integer.parseInt(parts[1].trim());
                }
            }

            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ─── Score display ────────────────────────────────────────
        double percent = totalMarks > 0
                ? (obtainedMarks * 100.0 / totalMarks)
                : 0;

        Label scoreLabel = new Label(
                "Your Score: " + obtainedMarks + " / " + totalMarks
                        + "  (" + String.format("%.1f", percent) + "%)"
        );

        scoreLabel.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: "
                        + (percent >= 50 ? "#4CAF50" : "#f44336") + ";"
        );

        Label gradeLabel = new Label("Grade: " + getGrade(percent));
        gradeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Button attemptButton = new Button("Attempt Exam");
        attemptButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        attemptButton.setOnAction(e -> {
            try {
                new AttemptQuestionUI(username).start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                welcome,
                new Separator(),
                scoreLabel,
                gradeLabel,
                new Separator(),
                attemptButton
        );

        Scene scene = new Scene(root, 400, 280);
        stage.setScene(scene);
        stage.setTitle("Student Dashboard - " + username);
        stage.show();
    }

    private String getGrade(double percent) {
        if (percent >= 90) return "A+";
        if (percent >= 80) return "A";
        if (percent >= 70) return "B";
        if (percent >= 60) return "C";
        if (percent >= 50) return "D";
        return "F";
    }
}