package com.example.olineexaminationsystem;

import java.io.*;

public class FileManager {

    public static void saveToFile(String fileName,
                                  String data) {

        try {

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter(fileName, true)
                    );

            writer.write(data);
            writer.newLine();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}