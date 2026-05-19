package com.exam.screens;

import com.exam.dao.UserDAO;
import com.exam.models.User;
import com.exam.utils.SceneManager;
import com.exam.utils.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen {

    private final UserDAO userDAO = new UserDAO();

    public Scene buildScene() {

        // ── Main wrapper ──────────────────────────────────────────
        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #F5F4F0;");

        // ── App title ─────────────────────────────────────────────
        Label appTitle = new Label("ExamPortal");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        appTitle.setTextFill(Color.web("#534AB7"));

        Label appSubtitle = new Label("Online Examination System");
        appSubtitle.setFont(Font.font("System", 13));
        appSubtitle.setTextFill(Color.web("#888780"));

        // ── White card ────────────────────────────────────────────
        VBox card = new VBox(12);
        card.setPadding(new Insets(28));
        card.setMaxWidth(400);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E0DFF8;" +
                        "-fx-border-radius: 12;"
        );

        // ── Role toggle ───────────────────────────────────────────
        Label roleLabel = new Label("Role");
        roleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        ToggleGroup roleGroup = new ToggleGroup();

        ToggleButton studentBtn = new ToggleButton("Student");
        studentBtn.setToggleGroup(roleGroup);
        studentBtn.setSelected(true); // default selection
        studentBtn.setPrefWidth(100);
        styleToggle(studentBtn, true);

        ToggleButton adminBtn = new ToggleButton("Admin");
        adminBtn.setToggleGroup(roleGroup);
        adminBtn.setPrefWidth(100);
        styleToggle(adminBtn, false);

        // Update button styles when selection changes
        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == studentBtn) {
                styleToggle(studentBtn, true);
                styleToggle(adminBtn, false);
            } else if (newVal == adminBtn) {
                styleToggle(adminBtn, true);
                styleToggle(studentBtn, false);
            }
            // Prevent deselecting both buttons
            if (newVal == null) oldVal.setSelected(true);
        });

        HBox toggleBox = new HBox(0, studentBtn, adminBtn);

        // ── Email field ───────────────────────────────────────────
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        TextField emailField = styledField("Enter your email");

        // ── Password field ────────────────────────────────────────
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(fieldStyle());

        // ── Error label ───────────────────────────────────────────
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #A32D2D; -fx-font-size: 11;");
        errorLabel.setWrapText(true);

        // ── Sign In button ────────────────────────────────────────
        Button signInBtn = primaryBtn("Sign In");
        signInBtn.setMaxWidth(Double.MAX_VALUE);

        signInBtn.setOnAction(e -> {
            String email    = emailField.getText().trim();
            String password = passwordField.getText();

            // Validate fields not empty
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            // Attempt login
            User user = userDAO.login(email, password);

            if (user == null) {
                errorLabel.setText("Incorrect email or password.");
                return;
            }

            // Check selected role matches the user's actual role
            String selectedRole = (roleGroup.getSelectedToggle() == studentBtn)
                    ? "student" : "admin";

            if (!user.getRole().equals(selectedRole)) {
                errorLabel.setText("Wrong role selected for this account.");
                return;
            }

            // Login successful — save user in session
            SessionManager.getInstance().login(user);

            // Navigate to correct dashboard
            if (user.getRole().equals("admin")) {
                SceneManager.switchTo("admin");
            } else {
                SceneManager.switchTo("dashboard");
            }
        });

        // ── Register link ─────────────────────────────────────────
        Label noAccount = new Label("No account?");
        noAccount.setTextFill(Color.web("#888780"));

        Hyperlink registerLink = new Hyperlink("Register");
        registerLink.setStyle("-fx-text-fill: #534AB7;");
        registerLink.setOnAction(e -> SceneManager.switchTo("register"));

        HBox registerBox = new HBox(4, noAccount, registerLink);
        registerBox.setAlignment(Pos.CENTER);

        // ── Assemble card ─────────────────────────────────────────
        card.getChildren().addAll(
                roleLabel, toggleBox,
                emailLabel, emailField,
                passLabel, passwordField,
                errorLabel,
                signInBtn,
                registerBox
        );

        // ── Assemble root ─────────────────────────────────────────
        root.getChildren().addAll(appTitle, appSubtitle, card);

        return new Scene(root, 980, 700);
    }

    // ── Style helpers ─────────────────────────────────────────────

    private void styleToggle(ToggleButton btn, boolean active) {
        if (active) {
            btn.setStyle(
                    "-fx-background-color: #534AB7;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: #EEEDFE;" +
                            "-fx-text-fill: #534AB7;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
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