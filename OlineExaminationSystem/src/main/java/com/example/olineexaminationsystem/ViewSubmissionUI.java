package com.example.olineexaminationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class ViewSubmissionUI extends Application {

    @Override
    public void start(Stage stage) {

        TextArea area =
                new TextArea();

        area.setEditable(false);

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    "submissions.txt"
                            )
                    );

            String line;

            while((line = reader.readLine()) != null) {

                area.appendText(line + "\n\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        VBox root = new VBox(10);

        root.setPadding(new Insets(20));

        root.getChildren().add(area);

        Scene scene = new Scene(root, 700, 500);

        stage.setScene(scene);
        stage.setTitle("Submissions");
        stage.show();
    }
}