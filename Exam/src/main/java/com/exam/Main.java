package com.exam;

import com.exam.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Register the window with SceneManager so all screens can use it
        SceneManager.setStage(stage);

        stage.setTitle("ExamPortal");
        stage.setWidth(980);
        stage.setHeight(700);
        stage.setMinWidth(820);
        stage.setMinHeight(600);

        // Start at the login screen
        SceneManager.switchTo("login");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}