package com.exam.database;

import java.sql.Connection;

public class main {
    public static void main(String[] args){
        Connection c= DBConnection.getConnection();
        if (c!=null){
            System.out.println("database connected successfully");

        }
        else {
            System.out.println("database is not connecte");
        }
    }

}
