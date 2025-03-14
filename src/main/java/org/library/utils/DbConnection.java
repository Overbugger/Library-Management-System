package org.library.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

//    Database Credentials
    private static final String URL = "jdbc:postgresql://ep-crimson-sound-a9hi40hn-pooler.gwc.azure.neon.tech:5432/TestDB?sslmode=require";
    private static final String USER = "TestDB_owner";
    private static final String PASSWORD = "npg_g7zEXSmVv8bu";

    private DbConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
