package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentDashboard extends Application {

    private String username;

    public StudentDashboard(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage stage) {

        Label welcome =
                new Label("Welcome " + username);

        Button attemptButton =
                new Button("Attempt Question");

        attemptButton.setOnAction(e -> {

            try {

                new AttemptQuestionUI(username)
                        .start(new Stage());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10);

        root.setPadding(new Insets(20));

        root.getChildren().addAll(
                welcome,
                attemptButton
        );

        Scene scene = new Scene(root, 400, 250);

        stage.setScene(scene);
        stage.setTitle("Student Dashboard");
        stage.show();
    }
}