package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViewSubmissionUI extends Application {

    // ─── Load all submissions from file ──────────────────────────
    private List<Submission> loadSubmissions() {

        List<Submission> list = new ArrayList<>();

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("submissions.txt"));

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");

                if (parts.length >= 5) {
                    list.add(new Submission(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            Integer.parseInt(parts[4].trim())
                    ));
                }
            }

            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // ─── Save all submissions back to file ───────────────────────
    private void saveSubmissions(List<Submission> list) {

        try {
            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter("submissions.txt", false)
                    );

            for (Submission s : list) {
                writer.write(s.toString());
                writer.newLine();
            }

            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ─── Load total marks from exam_total.txt ────────────────────
    private int loadTotalMarks() {

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("exam_total.txt"));

            String line = reader.readLine();
            reader.close();

            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    @Override
    public void start(Stage stage) {

        List<Submission> submissions = loadSubmissions();
        int totalExamMarks = loadTotalMarks();

        // ─── Header ───────────────────────────────────────────────
        Label title = new Label("Grade Student Submissions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label totalInfo = new Label("Total Exam Marks: " + totalExamMarks);
        totalInfo.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

        Label status = new Label();
        status.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        // ─── Submission cards ─────────────────────────────────────
        VBox cardContainer = new VBox(15);

        if (submissions.isEmpty()) {
            cardContainer.getChildren().add(
                    new Label("No submissions found.")
            );
        }

        for (int i = 0; i < submissions.size(); i++) {

            Submission s   = submissions.get(i);
            int        idx = i;

            // Card box
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setStyle(
                    "-fx-border-color: #90CAF9;"
                            + "-fx-border-width: 1.5;"
                            + "-fx-border-radius: 6;"
                            + "-fx-background-color: #F3F8FF;"
                            + "-fx-background-radius: 6;"
            );

            // Student name
            Label studentLbl = new Label("Student:   " + s.getUsername());
            studentLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

            // Question
            Label questionLbl = new Label("Question:  " + s.getQuestion());
            questionLbl.setWrapText(true);

            // Answer
            Label answerHeader = new Label("Answer:");
            answerHeader.setStyle("-fx-font-weight: bold;");

            TextArea answerArea = new TextArea(s.getAnswer());
            answerArea.setEditable(false);
            answerArea.setWrapText(true);
            answerArea.setPrefRowCount(3);
            answerArea.setStyle("-fx-background-color: #fff;");

            // File path
            Label fileLbl = new Label(
                    "File:      " + (s.getFilePath().isEmpty()
                            ? "None"
                            : s.getFilePath())
            );
            fileLbl.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");

            // Marks row
            Label marksLbl = new Label("Award Marks:");
            marksLbl.setStyle("-fx-font-weight: bold;");

            TextField marksField = new TextField(
                    String.valueOf(s.getMarks())
            );
            marksField.setPrefWidth(70);
            marksField.setPromptText("0");

            Label outOfLbl = new Label("out of " + totalExamMarks);
            outOfLbl.setStyle("-fx-text-fill: #555;");

            Button saveBtn = new Button("Save Marks");
            saveBtn.setStyle(
                    "-fx-background-color: #4CAF50;"
                            + "-fx-text-fill: white;"
                            + "-fx-font-weight: bold;"
            );

            // ── Save marks for this submission ────────────────────
            saveBtn.setOnAction(e -> {

                String input = marksField.getText().trim();

                if (input.isEmpty()) {
                    status.setText("Enter marks for " + s.getUsername());
                    status.setStyle("-fx-text-fill: red;");
                    return;
                }

                try {
                    int newMarks = Integer.parseInt(input);

                    if (newMarks < 0) {
                        status.setText("Marks cannot be negative.");
                        status.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    if (newMarks > totalExamMarks) {
                        status.setText(
                                "Marks cannot exceed total: " + totalExamMarks
                        );
                        status.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    // Update in-memory list
                    submissions.get(idx).setMarks(newMarks);

                    // Write back to file
                    saveSubmissions(submissions);

                    status.setText(
                            "✔ Marks saved for "
                                    + s.getUsername()
                                    + " → " + newMarks + " marks"
                    );
                    status.setStyle("-fx-text-fill: green;");

                } catch (NumberFormatException ex) {
                    status.setText("Please enter a valid number.");
                    status.setStyle("-fx-text-fill: red;");
                }
            });

            HBox marksRow = new HBox(8,
                    marksLbl,
                    marksField,
                    outOfLbl,
                    saveBtn
            );
            marksRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            card.getChildren().addAll(
                    studentLbl,
                    questionLbl,
                    answerHeader,
                    answerArea,
                    fileLbl,
                    new Separator(),
                    marksRow
            );

            cardContainer.getChildren().add(card);
        }

        // ─── Summary table ────────────────────────────────────────
        Label summaryTitle = new Label("Score Summary");
        summaryTitle.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold;"
        );

        VBox summaryBox = buildSummary(submissions, totalExamMarks);

        // Refresh summary after saving marks
        Button refreshBtn = new Button("Refresh Summary");
        refreshBtn.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white;"
        );
        refreshBtn.setOnAction(e -> {
            List<Submission> fresh = loadSubmissions();
            VBox newBox = buildSummary(fresh, totalExamMarks);

            VBox parent = (VBox) summaryBox.getParent();
            int  sidx   = parent.getChildren().indexOf(summaryBox);

            if (sidx >= 0) {
                parent.getChildren().set(sidx, newBox);
            }

            status.setText("Summary refreshed.");
            status.setStyle("-fx-text-fill: green;");
        });

        // ─── Scroll pane wraps submission cards ───────────────────
        ScrollPane scroll = new ScrollPane(cardContainer);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color: transparent;");

        // ─── Root layout ──────────────────────────────────────────
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                title,
                totalInfo,
                status,
                new Separator(),
                new Label("── Submissions ──"),
                scroll,
                new Separator(),
                summaryTitle,
                summaryBox,
                refreshBtn
        );

        ScrollPane outerScroll = new ScrollPane(root);
        outerScroll.setFitToWidth(true);

        Scene scene = new Scene(outerScroll, 720, 700);
        stage.setScene(scene);
        stage.setTitle("Grade Submissions");
        stage.show();
    }

    // ─── Build per-student summary table ─────────────────────────
    private VBox buildSummary(List<Submission> submissions,
                              int totalExamMarks) {

        VBox box = new VBox(4);

        java.util.Map<String, Integer> scoreMap =
                new java.util.LinkedHashMap<>();

        for (Submission s : submissions) {
            scoreMap.merge(s.getUsername(), s.getMarks(), Integer::sum);
        }

        if (scoreMap.isEmpty()) {
            box.getChildren().add(new Label("No data yet."));
            return box;
        }

        // Header row
        HBox header = new HBox(0,
                cell("Student",    160, true),
                cell("Obtained",    90, true),
                cell("Out of",      90, true),
                cell("Percentage", 110, true),
                cell("Grade",       80, true)
        );
        box.getChildren().add(header);

        for (java.util.Map.Entry<String, Integer> entry
                : scoreMap.entrySet()) {

            String student  = entry.getKey();
            int    obtained = entry.getValue();
            double pct      = totalExamMarks > 0
                    ? (obtained * 100.0 / totalExamMarks)
                    : 0;

            HBox row = new HBox(0,
                    cell(student,                           160, false),
                    cell(String.valueOf(obtained),           90, false),
                    cell(String.valueOf(totalExamMarks),     90, false),
                    cell(String.format("%.1f%%", pct),      110, false),
                    cell(grade(pct),                         80, false)
            );
            box.getChildren().add(row);
        }

        return box;
    }

    private Label cell(String text, double width, boolean header) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setPadding(new Insets(5, 8, 5, 8));
        l.setStyle(
                "-fx-border-color: #bbb;"
                        + "-fx-border-width: 0.5;"
                        + (header
                        ? "-fx-background-color: #1976D2;"
                          + "-fx-text-fill: white;"
                          + "-fx-font-weight: bold;"
                        : "-fx-background-color: #fff;")
        );
        return l;
    }

    private String grade(double pct) {
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }
}