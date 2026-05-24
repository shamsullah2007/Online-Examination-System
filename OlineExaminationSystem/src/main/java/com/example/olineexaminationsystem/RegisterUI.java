package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterUI extends Application {

    @Override
    public void start(Stage stage) {

        Label title =
                new Label("Student Registration");

        TextField usernameField =
                new TextField();

        usernameField.setPromptText("Username");

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText("Password");

        Button registerButton =
                new Button("Register");

        Button loginButton = new Button("Go to Login");


        Label status =
                new Label();

        registerButton.setOnAction(e -> {

            User user =
                    new User(
                            usernameField.getText(),
                            passwordField.getText(),
                            "student"
                    );

            FileManager.saveToFile(
                    "users.txt",
                    user.toString()
            );

            status.setText("Registered Successfully");

            usernameField.clear();
            passwordField.clear();
        });

        loginButton.setOnAction(e -> {
            try {
                new LoginUI().start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10);

        root.setPadding(new Insets(20));

        root.getChildren().addAll(
                title,
                usernameField,
                passwordField,
                registerButton,
                loginButton,
                status
        );

        Scene scene = new Scene(root, 400, 300);

        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}