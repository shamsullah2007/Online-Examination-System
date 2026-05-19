package com.exam.screens;

import com.exam.dao.UserDAO;
import com.exam.models.User;
import com.exam.utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegisterScreen {

    private final UserDAO userDAO = new UserDAO();

    public Scene buildScene() {

        // ── Main wrapper ──────────────────────────────────────────
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F4F0;");

        // ── White card ────────────────────────────────────────────
        VBox card = new VBox(12);
        card.setPadding(new Insets(28));
        card.setMaxWidth(480);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E0DFF8;" +
                        "-fx-border-radius: 12;"
        );

        // ── Title ─────────────────────────────────────────────────
        Label title = new Label("Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        // ── First & Last name row ─────────────────────────────────
        TextField firstNameField = styledField("First name");
        TextField lastNameField  = styledField("Last name");

        VBox firstBox = new VBox(4, boldLabel("First Name"), firstNameField);
        VBox lastBox  = new VBox(4, boldLabel("Last Name"),  lastNameField);

        HBox nameRow = new HBox(10, firstBox, lastBox);
        HBox.setHgrow(firstBox, Priority.ALWAYS);
        HBox.setHgrow(lastBox,  Priority.ALWAYS);

        // ── Student ID ────────────────────────────────────────────
        TextField studentIdField = styledField("e.g. CS-2021-001");

        // ── Email ─────────────────────────────────────────────────
        TextField emailField = styledField("your@email.com");

        // ── Department dropdown ───────────────────────────────────
        ComboBox<String> departmentBox = new ComboBox<>();
        departmentBox.getItems().addAll(
                "Computer Science",
                "Software Engineering",
                "Electrical Engineering",
                "Mathematics",
                "Physics"
        );
        departmentBox.setValue("Computer Science");
        departmentBox.setMaxWidth(Double.MAX_VALUE);
        departmentBox.setStyle(fieldStyle());

        // ── Password row ──────────────────────────────────────────
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Min 8 characters");
        passwordField.setStyle(fieldStyle());

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Repeat password");
        confirmField.setStyle(fieldStyle());

        VBox passBox    = new VBox(4, boldLabel("Password"),         passwordField);
        VBox confirmBox = new VBox(4, boldLabel("Confirm Password"), confirmField);

        HBox passRow = new HBox(10, passBox, confirmBox);
        HBox.setHgrow(passBox,    Priority.ALWAYS);
        HBox.setHgrow(confirmBox, Priority.ALWAYS);

        // ── Terms checkbox ────────────────────────────────────────
        CheckBox termsCheck = new CheckBox(
                "I agree to the academic integrity policy"
        );
        termsCheck.setStyle("-fx-font-size: 12;");

        // ── Error label ───────────────────────────────────────────
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #A32D2D; -fx-font-size: 11;");
        errorLabel.setWrapText(true);

        // ── Create Account button ─────────────────────────────────
        Button createBtn = primaryBtn("Create Account");
        createBtn.setMaxWidth(Double.MAX_VALUE);

        createBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName  = lastNameField.getText().trim();
            String studentId = studentIdField.getText().trim();
            String email     = emailField.getText().trim();
            String dept      = departmentBox.getValue();
            String password  = passwordField.getText();
            String confirm   = confirmField.getText();

            // ── Validation ────────────────────────────────────────
            if (firstName.isEmpty() || lastName.isEmpty() ||
                    email.isEmpty()     || password.isEmpty()) {
                errorLabel.setText("Please fill in all required fields.");
                return;
            }

            if (!password.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }

            if (password.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters.");
                return;
            }

            if (!termsCheck.isSelected()) {
                errorLabel.setText("You must agree to the integrity policy.");
                return;
            }

            if (userDAO.emailExists(email)) {
                errorLabel.setText("This email is already registered.");
                return;
            }

            // ── Register ──────────────────────────────────────────
            User newUser = new User(
                    firstName, lastName, studentId,
                    email, password, dept, "student"
            );

            boolean success = userDAO.register(newUser);

            if (success) {
                SceneManager.switchTo("login");
            } else {
                errorLabel.setText("Registration failed. Please try again.");
            }
        });

        // ── Sign in link ──────────────────────────────────────────
        Label alreadyLabel = new Label("Already registered?");
        alreadyLabel.setTextFill(Color.web("#888780"));

        Hyperlink signInLink = new Hyperlink("Sign in");
        signInLink.setStyle("-fx-text-fill: #534AB7;");
        signInLink.setOnAction(e -> SceneManager.switchTo("login"));

        HBox signInBox = new HBox(4, alreadyLabel, signInLink);
        signInBox.setAlignment(Pos.CENTER);

        // ── Assemble card ─────────────────────────────────────────
        card.getChildren().addAll(
                title,
                nameRow,
                boldLabel("Student ID"),  studentIdField,
                boldLabel("Email"),       emailField,
                boldLabel("Department"),  departmentBox,
                passRow,
                termsCheck,
                errorLabel,
                createBtn,
                signInBox
        );

        root.getChildren().add(card);

        return new Scene(root, 980, 700);
    }

    // ── Style helpers ─────────────────────────────────────────────

    private Label boldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        return lbl;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: #534AB7;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9 20;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    private String fieldStyle() {
        return "-fx-background-color: #F5F4F0;" +
                "-fx-border-color: #D0CFF8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 12;";
    }
}