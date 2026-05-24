package com.exam.screens;

import com.exam.dao.AttemptDAO;
import com.exam.dao.QuestionDAO;
import com.exam.models.Exam;
import com.exam.models.Option;
import com.exam.models.Question;
import com.exam.utils.SceneManager;
import com.exam.utils.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

public class ExamScreen {

    private final Exam            exam;
    private final List<Question>  questions;
    private final AttemptDAO      attemptDAO = new AttemptDAO();
    private       int             attemptId;
    private       int             currentIndex = 0;

    // Timer state
    private int      secondsLeft;
    private Timeline timeline;

    // UI elements updated dynamically
    private Label       timerLabel;
    private Label       questionNumberLabel;
    private Label       questionTextLabel;
    private VBox        optionsBox;
    private ProgressBar progressBar;
    private Button      prevBtn;
    private Button      nextBtn;

    public ExamScreen(Exam exam) {
        this.exam      = exam;
        this.questions = new QuestionDAO().getQuestionsWithOptions(exam.getId());
        this.secondsLeft = exam.getDurationMinutes() * 60;

        // Start the attempt record in DB
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        this.attemptId = attemptDAO.startAttempt(userId, exam.getId());
    }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F4F0;");

        root.setTop(buildTopBar());
        root.setCenter(buildCenter());
        root.setBottom(buildBottomBar());

        // Show first question
        if (!questions.isEmpty()) {
            renderQuestion(0);
        }

        // Start countdown timer
        startTimer();

        return new Scene(root, 980, 700);
    }

    // ── TOP BAR ───────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox topBar = new HBox(16);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label(exam.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 15));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLeftLabel = new Label("Time left: ");
        timeLeftLabel.setFont(Font.font("System", 13));

        timerLabel = new Label(formatTime(secondsLeft));
        timerLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        timerLabel.setTextFill(Color.web("#534AB7"));

        topBar.getChildren().addAll(
                titleLabel, spacer, timeLeftLabel, timerLabel
        );

        return topBar;
    }

    // ── CENTER CONTENT ────────────────────────────────────────────
    private VBox buildCenter() {
        VBox center = new VBox(14);
        center.setPadding(new Insets(24));

        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #534AB7;");

        questionNumberLabel = new Label("Question 1 of " + questions.size());
        questionNumberLabel.setTextFill(Color.web("#888780"));
        questionNumberLabel.setFont(Font.font("System", 12));

        questionTextLabel = new Label("");
        questionTextLabel.setWrapText(true);
        questionTextLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        optionsBox = new VBox(8);

        center.getChildren().addAll(
                progressBar, questionNumberLabel,
                questionTextLabel, optionsBox
        );

        return center;
    }

    // ── BOTTOM BAR ────────────────────────────────────────────────
    private HBox buildBottomBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 24, 14, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white;");

        prevBtn = new Button("← Previous");
        prevBtn.setStyle(outlineBtnStyle());
        prevBtn.setOnAction(e -> renderQuestion(currentIndex - 1));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nextBtn = new Button("Next →");
        nextBtn.setStyle(primaryBtnStyle());
        nextBtn.setOnAction(e -> renderQuestion(currentIndex + 1));

        Button submitBtn = new Button("Submit Exam");
        submitBtn.setStyle(
                "-fx-background-color: #0F6E56;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9 20;" +
                        "-fx-cursor: hand;"
        );
        submitBtn.setOnAction(e -> submitExam());

        bar.getChildren().addAll(prevBtn, spacer, nextBtn, submitBtn);

        return bar;
    }

    // ── RENDER QUESTION ───────────────────────────────────────────
    private void renderQuestion(int index) {
        currentIndex = index;
        Question q   = questions.get(index);

        // Update progress and labels
        progressBar.setProgress((double)(index + 1) / questions.size());
        questionNumberLabel.setText(
                "Question " + (index + 1) + " of " + questions.size()
        );
        questionTextLabel.setText(q.getText());

        // Rebuild options as radio buttons
        optionsBox.getChildren().clear();
        ToggleGroup group = new ToggleGroup();

        for (Option opt : q.getOptions()) {
            RadioButton rb = new RadioButton(opt.getOptionText());
            rb.setToggleGroup(group);
            rb.setUserData(opt.getId());
            rb.setWrapText(true);
            rb.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 10 14;" +
                            "-fx-border-color: #D0CFF8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );

            // Pre-select if student already answered this question
            if (opt.getId() == q.getSelectedOptionId()) {
                rb.setSelected(true);
            }

            // Save answer when student clicks a radio button
            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    q.setSelectedOptionId((int) rb.getUserData());
                }
            });

            optionsBox.getChildren().add(rb);
        }

        // Disable prev on first question, next on last
        prevBtn.setDisable(index == 0);
        nextBtn.setDisable(index == questions.size() - 1);
    }

    // ── TIMER ─────────────────────────────────────────────────────
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            timerLabel.setText(formatTime(secondsLeft));

            // Turn red when 5 minutes remain
            if (secondsLeft <= 300) {
                timerLabel.setTextFill(Color.web("#A32D2D"));
            }

            // Auto-submit when time runs out
            if (secondsLeft <= 0) {
                submitExam();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Converts seconds to HH:MM:SS display format
    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    // ── SUBMIT EXAM ───────────────────────────────────────────────
    private void submitExam() {
        timeline.stop();

        // Count unanswered questions
        long unanswered = questions.stream()
                .filter(q -> !q.isAnswered())
                .count();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Submit Exam");
        alert.setHeaderText("Are you sure you want to submit?");
        alert.setContentText(unanswered + " question(s) unanswered.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Save answers and get score
                int score = attemptDAO.submitAttempt(
                        attemptId, questions, exam.getPassMarks()
                );

                // Navigate to result screen
                ResultScreen rs = new ResultScreen(score, exam);
                SceneManager.getStage().setScene(rs.buildScene());

            } else {
                // Resume timer if cancelled
                timeline.play();
            }
        });
    }

    // ── STYLE HELPERS ─────────────────────────────────────────────
    private String primaryBtnStyle() {
        return "-fx-background-color: #534AB7;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9 20;" +
                "-fx-cursor: hand;";
    }

    private String outlineBtnStyle() {
        return "-fx-background-color: transparent;" +
                "-fx-border-color: #C0BDED;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 7 16;" +
                "-fx-cursor: hand;";
    }
}