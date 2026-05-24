package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class LoginUI extends Application {

    @Override
    public void start(Stage stage) {

        Label title =
                new Label("Login System");

        TextField usernameField =
                new TextField();

        usernameField.setPromptText("Username");

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText("Password");

        Button loginButton =
                new Button("Login");

        Label status =
                new Label();

        loginButton.setOnAction(e -> {

            String inputUser =
                    usernameField.getText();

            String inputPass =
                    passwordField.getText();

            try {

                BufferedReader reader =
                        new BufferedReader(
                                new FileReader("users.txt")
                        );

                String line;

                while((line = reader.readLine()) != null) {

                    String[] data = line.split(",");

                    String username = data[0];
                    String password = data[1];
                    String role = data[2];

                    if(username.equals(inputUser)
                            && password.equals(inputPass)) {

                        if(role.equals("admin")) {

                            new AdminDashboard().start(new Stage());

                        } else {

                            new StudentDashboard(username)
                                    .start(new Stage());
                        }

                        stage.close();
                        return;
                    }
                }

                status.setText("Invalid Login");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button registerButton = new Button("Go to Register");

        registerButton.setOnAction(e -> {
            try {
                new RegisterUI().start(new Stage());  // Open Register window
                stage.close();                         // Close Login window
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
                loginButton,
                registerButton,
                status
        );

        Scene scene = new Scene(root, 400, 300);

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

}