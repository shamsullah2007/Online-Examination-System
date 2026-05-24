package com.exam.screens;

import com.exam.models.Exam;
import com.exam.utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ResultScreen {

    private final int  score;
    private final Exam exam;

    public ResultScreen(int score, Exam exam) {
        this.score = score;
        this.exam  = exam;
    }

    public Scene buildScene() {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F4F0;");

        // Exam title
        Label titleLabel = new Label(exam.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Circle score display
        Circle circle = new Circle(55);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.web("#534AB7"));
        circle.setStrokeWidth(3);

        double percentage = (double) score / exam.getTotalMarks() * 100;

        Label scoreLabel = new Label(score + "/" + exam.getTotalMarks());
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.web("#534AB7"));

        Label pctLabel = new Label(String.format("%.1f%%", percentage));
        pctLabel.setFont(Font.font("System", 12));
        pctLabel.setTextFill(Color.web("#888780"));

        VBox scoreVBox = new VBox(2, scoreLabel, pctLabel);
        scoreVBox.setAlignment(Pos.CENTER);

        StackPane scoreCircle = new StackPane(circle, scoreVBox);

        // Pass / Fail label
        boolean passed = score >= exam.getPassMarks();

        Label resultLabel = new Label(passed ? "PASSED" : "FAILED");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        resultLabel.setTextFill(
                passed ? Color.web("#0F6E56") : Color.web("#A32D2D")
        );

        // Stat cards row
        HBox statsRow = buildStatsRow(percentage);

        // Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #C0BDED;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9 20;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> SceneManager.switchTo("dashboard"));

        root.getChildren().addAll(
                titleLabel, scoreCircle, resultLabel, statsRow, backBtn
        );

        return new Scene(root, 980, 700);
    }

    // ── STAT CARDS ────────────────────────────────────────────────
    private HBox buildStatsRow(double percentage) {
        VBox card1 = statCard("Your Score",
                score + " / " + exam.getTotalMarks());
        VBox card2 = statCard("Percentage",
                String.format("%.1f%%", percentage));
        VBox card3 = statCard("Pass Mark",
                String.valueOf(exam.getPassMarks()));

        HBox row = new HBox(20, card1, card2, card3);
        row.setAlignment(Pos.CENTER);

        return row;
    }

    private VBox statCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 12));
        titleLabel.setTextFill(Color.web("#888780"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        VBox card = new VBox(4, titleLabel, valueLabel);
        card.setPadding(new Insets(14, 24, 14, 24));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #EEECFC;" +
                        "-fx-background-radius: 10;"
        );

        return card;
    }
}