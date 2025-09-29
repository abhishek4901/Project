package com.employee.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DEFAULT_HOST = getEnvOrDefault("DB_HOST", "localhost");
    private static final String DEFAULT_PORT = getEnvOrDefault("DB_PORT", "3306");
    private static final String DEFAULT_DB = getEnvOrDefault("DB_NAME", "employee_mgmt");
    private static final String DEFAULT_USER = getEnvOrDefault("DB_USER", "root");
    private static final String DEFAULT_PASS = getEnvOrDefault("DB_PASS", "abhishek");
    
    static {
        // Ensure the MySQL driver is loaded (useful for some environments)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Proceed anyway; DriverManager may still locate the driver via SPI, but log for clarity
            System.err.println("MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private static String getEnvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

    public static Connection getConnection() throws SQLException {
        String baseUrl = String.format("jdbc:mysql://%s:%s/", DEFAULT_HOST, DEFAULT_PORT);
        // Ensure database exists
        try (Connection conn = DriverManager.getConnection(baseUrl + "?useSSL=false&allowPublicKeyRetrieval=true", DEFAULT_USER, DEFAULT_PASS);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DEFAULT_DB + "`");
        }
        String dbUrl = baseUrl + DEFAULT_DB + "?useSSL=false&allowPublicKeyRetrieval=true";
        Connection connection = DriverManager.getConnection(dbUrl, DEFAULT_USER, DEFAULT_PASS);
        ensureTables(connection);
        return connection;
    }

    private static void ensureTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) NOT NULL UNIQUE, " +
                "department VARCHAR(100) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
        }
    }
}
