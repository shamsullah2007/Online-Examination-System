package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminDashboard extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("Admin Panel");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // --- Question Fields ---
        TextField questionField = new TextField();
        questionField.setPromptText("Enter Question");

        TextField marksField = new TextField();
        marksField.setPromptText("Enter Marks");

        Button saveButton = new Button("Save Question");

        // --- Exam Schedule ---
        Label scheduleLabel = new Label("Exam Schedule (24hr format  HH:mm):");

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Start Time e.g. 10:00");

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("End Time e.g. 11:00");

        Button scheduleButton = new Button("Set Exam Schedule");
        scheduleButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white;"
        );

        Label status = new Label();

        // --- Save Question ---
        saveButton.setOnAction(e -> {
            String question = questionField.getText().trim();
            String marksText = marksField.getText().trim();

            if (question.isEmpty() || marksText.isEmpty()) {
                status.setText("Fill all fields.");
                return;
            }

            try {
                int marks = Integer.parseInt(marksText);
                Question q = new Question(question, marks);

                // Save question
                BufferedWriter writer =
                        new BufferedWriter(new FileWriter("questions.txt", true));
                writer.write(q.toString());
                writer.newLine();
                writer.close();

                // ✅ Recalculate and save total marks to exam_total.txt
                int total = 0;
                BufferedReader reader =
                        new BufferedReader(new FileReader("questions.txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            total += Integer.parseInt(parts[1].trim());
                        }
                    }
                }
                reader.close();

                BufferedWriter totalWriter =
                        new BufferedWriter(new FileWriter("exam_total.txt", false));
                totalWriter.write(String.valueOf(total));
                totalWriter.close();

                status.setText("Question saved! Total marks: " + total);
                questionField.clear();
                marksField.clear();

            } catch (NumberFormatException ex) {
                status.setText("Marks must be a number.");
            } catch (IOException ex) {
                status.setText("File error.");
            }
        });

        // --- Set Schedule ---
        scheduleButton.setOnAction(e -> {

            String startText = startTimeField.getText().trim();
            String endText   = endTimeField.getText().trim();

            if (startText.isEmpty() || endText.isEmpty()) {
                status.setText("Enter both start and end time.");
                return;
            }

            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime startTime = LocalTime.parse(startText, fmt);
                LocalTime endTime   = LocalTime.parse(endText,   fmt);

                if (!endTime.isAfter(startTime)) {
                    status.setText("End time must be after start time.");
                    return;
                }

                // Save to exam_config.txt as:  startTime,endTime
                BufferedWriter writer =
                        new BufferedWriter(
                                new FileWriter("exam_config.txt", false)
                        );

                writer.write(startText + "," + endText);
                writer.close();

                status.setText(
                        "Exam scheduled: " + startText + " → " + endText
                );

                startTimeField.clear();
                endTimeField.clear();

            } catch (DateTimeParseException ex) {
                status.setText("Invalid time format. Use HH:mm (e.g. 10:00)");
            } catch (IOException ex) {
                status.setText("File error.");
            }
        });
        Button viewSubButton = new Button("Grade Submissions");
        viewSubButton.setStyle(
                "-fx-background-color: #FF9800;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 13px;"
        );

        viewSubButton.setOnAction(e -> {
            try {
                new ViewSubmissionUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox timeRow = new HBox(10, startTimeField, endTimeField);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                title,
                new Separator(),
                new Label("-- Add Question --"),
                questionField,
                marksField,
                saveButton,
                new Separator(),
                new Label("-- Exam Schedule --"),
                scheduleLabel,
                timeRow,
                scheduleButton,
                viewSubButton,
                status
        );

        Scene scene = new Scene(root, 420, 420);
        stage.setScene(scene);
        stage.setTitle("Admin Panel");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}