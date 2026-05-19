package com.exam.utils;

import com.exam.screens.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    // The one primary window of the application
    private static Stage primaryStage;

    // Called once from Main.java to register the stage
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    // Returns the stage (used by ExamScreen to switch to ResultScreen)
    public static Stage getStage() {
        return primaryStage;
    }

    // Switches the window to the requested screen by name
    public static void switchTo(String name) {
        Scene scene;

        switch (name) {
            case "login"     -> scene = new LoginScreen().buildScene();
            case "register"  -> scene = new RegisterScreen().buildScene();
            case "dashboard" -> scene = new StudentDashboardScreen().buildScene();
            case "admin"     -> scene = new AdminScreen().buildScene();
            default -> throw new IllegalArgumentException("Unknown screen: " + name);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}