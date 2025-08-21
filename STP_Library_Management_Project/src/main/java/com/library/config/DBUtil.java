package com.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL  = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "@AKKIkumar8113";// your MySQL password

    static {
        try {
            // Load MySQL driver only once
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ MySQL JDBC Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connection gave...");
        Connection con=null;
        try {
            con= DriverManager.getConnection(URL, USER, PASS);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return con;
    }
}
