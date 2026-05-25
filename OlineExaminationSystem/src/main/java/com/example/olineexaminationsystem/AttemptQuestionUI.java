package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AttemptQuestionUI extends Application {

    private String username;
    private Timer scheduleTimer;

    public AttemptQuestionUI(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage stage) {

        // ─── Load Questions ───────────────────────────────────────
        List<String> questions = new ArrayList<>();

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("questions.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    questions.add(line.split("\\|")[0].trim()); // ✅ fixed split
                }
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ─── Load Exam Schedule ───────────────────────────────────
        final LocalTime[] startTime = {null};
        final LocalTime[] endTime   = {null};

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader("exam_config.txt"));
            String line = reader.readLine();
            reader.close();

            if (line != null && line.contains(",")) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
                String[] parts = line.split(",");
                startTime[0] = LocalTime.parse(parts[0].trim(), fmt);
                endTime[0]   = LocalTime.parse(parts[1].trim(), fmt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ─── State ────────────────────────────────────────────────
        final int[]     currentIndex     = {0};
        final String[]  uploadedFilePath = {""};
        final boolean[] examEnded        = {false}; // ✅ guard flag

        // ─── UI Components ────────────────────────────────────────
        Label timerDisplay = new Label();
        timerDisplay.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;"
        );

        // Show schedule info right away
        if (startTime[0] != null && endTime[0] != null) {
            timerDisplay.setText(
                    "Exam Window:  " + startTime[0] + "  →  " + endTime[0]
            );
        } else {
            timerDisplay.setText("No exam scheduled.");
        }

        Label questionCounter = new Label();
        Label questionLabel   = new Label("Press Start to begin.");
        questionLabel.setWrapText(true);

        TextArea answerArea = new TextArea();
        answerArea.setPromptText("Write your answer here...");
        answerArea.setEditable(false);  // locked until Start

        Button uploadButton = new Button("Upload PDF/Image");
        uploadButton.setDisable(true);  // locked until Start

        Label fileLabel = new Label("No File Selected");

        Button prevButton   = new Button("Previous");
        Button nextButton   = new Button("Next");
        Button submitButton = new Button("Submit Answer");
        Button startButton  = new Button("▶  Start Exam");

        prevButton.setDisable(true);
        nextButton.setDisable(true);
        submitButton.setDisable(true);

        startButton.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-background-color: #4CAF50;"
                        + "-fx-text-fill: white;"
        );

        Label status = new Label();

        // ─── Update question display ──────────────────────────────
        Runnable updateQuestion = () -> {
            if (questions.isEmpty()) {
                questionLabel.setText("No questions available.");
                return;
            }
            int idx = currentIndex[0];
            questionCounter.setText(
                    "Question " + (idx + 1) + " of " + questions.size()
            );
            questionLabel.setText(questions.get(idx));
            answerArea.clear();
            fileLabel.setText("No File Selected");
            uploadedFilePath[0] = "";
            status.setText("");
            prevButton.setDisable(idx == 0);
            nextButton.setDisable(idx == questions.size() - 1);
        };

        // ─── Exam end: only called by TIMER, never by button ─────
        Runnable onExamEnd = () -> Platform.runLater(() -> {

            // ✅ Guard: run only once no matter how many times triggered
            if (examEnded[0]) return;
            examEnded[0] = true;

            if (scheduleTimer != null) scheduleTimer.cancel();

            // ✅ Clear questions.txt only here, triggered by real end time
            try {
                new FileWriter("questions.txt", false).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            answerArea.setEditable(false);
            uploadButton.setDisable(true);
            submitButton.setDisable(true);
            prevButton.setDisable(true);
            nextButton.setDisable(true);
            startButton.setDisable(true);

            timerDisplay.setStyle(
                    "-fx-font-size: 16px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: red;"
            );
            timerDisplay.setText("⏰ Exam ended. Questions cleared.");
            questionLabel.setText("The exam window has closed.");
            status.setText("Exam over. Submitted answers are saved.");
        });

        // ─── Start Button ─────────────────────────────────────────
        startButton.setOnAction(e -> {

            LocalTime now = LocalTime.now();

            // ✅ Before window: just show message, do nothing else
            if (startTime[0] != null && now.isBefore(startTime[0])) {
                status.setText(
                        "Exam hasn't started yet. Starts at " + startTime[0]
                );
                return;
            }

            // ✅ After window: just show message, do NOT call onExamEnd
            if (endTime[0] != null && now.isAfter(endTime[0])) {
                status.setText(
                        "Exam window already closed at " + endTime[0]
                );
                return; // ← just return, questions.txt stays safe
            }

            if (questions.isEmpty()) {
                status.setText("No questions available.");
                return;
            }

            // ── Unlock everything ─────────────────────────────────
            answerArea.setEditable(true);
            uploadButton.setDisable(false);
            submitButton.setDisable(false);
            startButton.setDisable(true);

            updateQuestion.run();

            // ── Start countdown ticker every second ───────────────
            scheduleTimer = new Timer();

            scheduleTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    // ✅ Stop ticking if exam already ended
                    if (examEnded[0]) {
                        scheduleTimer.cancel();
                        return;
                    }

                    LocalTime current = LocalTime.now();

                    // ✅ Only onExamEnd triggers the file clear
                    if (endTime[0] != null
                            && !current.isBefore(endTime[0])) {
                        onExamEnd.run();
                        return;
                    }

                    // Update countdown display
                    if (endTime[0] != null) {

                        long secondsLeft =
                                java.time.Duration
                                        .between(current, endTime[0])
                                        .getSeconds();

                        Platform.runLater(() -> {

                            timerDisplay.setText(
                                    "Time Remaining: " + formatTime(secondsLeft)
                            );

                            // Warning colour under 1 minute
                            if (secondsLeft <= 60) {
                                timerDisplay.setStyle(
                                        "-fx-font-size: 16px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-text-fill: red;"
                                );
                            }
                        });
                    }
                }

            }, 0, 1000);
        });

        // ─── Navigation ───────────────────────────────────────────
        prevButton.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                updateQuestion.run();
            }
        });

        nextButton.setOnAction(e -> {
            if (currentIndex[0] < questions.size() - 1) {
                currentIndex[0]++;
                updateQuestion.run();
            }
        });

        // ─── File Upload ──────────────────────────────────────────
        uploadButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Allowed Files", "*.pdf", "*.png", "*.jpg"
                    )
            );
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                uploadedFilePath[0] = file.getAbsolutePath();
                fileLabel.setText(file.getName());
            }
        });

        // ─── Submit ───────────────────────────────────────────────
        submitButton.setOnAction(e -> {
            String answer = answerArea.getText().trim();
            if (answer.isEmpty()) {
                status.setText("Write an answer before submitting.");
                return;
            }
            Submission s = new Submission(
                    username,
                    questionLabel.getText(),
                    answer,
                    uploadedFilePath[0],
                    0
            );
            FileManager.saveToFile("submissions.txt", s.toString());
            status.setText("✔ Submitted Q" + (currentIndex[0] + 1));
        });

        // ─── Cancel timer on window close ─────────────────────────
        stage.setOnCloseRequest(e -> {
            if (scheduleTimer != null) scheduleTimer.cancel();
        });

        // ─── Layout ───────────────────────────────────────────────
        HBox navBar = new HBox(10, prevButton, nextButton);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                timerDisplay,
                new Separator(),
                startButton,
                new Separator(),
                questionCounter,
                questionLabel,
                answerArea,
                uploadButton,
                fileLabel,
                submitButton,
                navBar,
                status
        );

        Scene scene = new Scene(root, 520, 520);
        stage.setScene(scene);
        stage.setTitle("Attempt Exam - " + username);
        stage.show();
    }

    private String formatTime(long totalSecs) {
        long minutes = totalSecs / 60;
        long seconds = totalSecs % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}