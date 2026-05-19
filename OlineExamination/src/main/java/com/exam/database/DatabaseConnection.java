package com.exam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection=null;
    private DatabaseConnection(){}

    public static Connection getConnconection(){
        try {
            if (connection==null || connection.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/onlineexaminationsystem" +
                        "?useSSL=false&serverTimezone=UTC";
                connection= DriverManager.getConnection(url,"root","shasmullah@2007");
                System.out.println("connected to mysql successfully");

            }


        }catch (SQLException e){

            System.err.println("DB Connection failed: " + e.getMessage());
        }
        return connection;



    }

}
