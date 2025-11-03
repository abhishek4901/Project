package com.employee.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // Load configuration from environment variables or defaults
    private static final String HOST = getEnvOrDefault("DB_HOST", "localhost");
    private static final String PORT = getEnvOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = getEnvOrDefault("DB_NAME", "employee_mgmt");
    private static final String USER = getEnvOrDefault("DB_USER", "root");
    private static final String PASS = getEnvOrDefault("DB_PASS", "abhishek");

    static {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private static String getEnvOrDefault(String key, String def) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? def : value;
    }

    // Get connection to database (auto-create DB & tables if needed)
    public static Connection getConnection() throws SQLException {
        String baseUrl = String.format("jdbc:mysql://%s:%s/?useSSL=false&allowPublicKeyRetrieval=true", HOST, PORT);

        // Step 1: Ensure database exists
        try (Connection conn = DriverManager.getConnection(baseUrl, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`");
        }

        // Step 2: Connect to the database
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true", HOST, PORT, DB_NAME);
        Connection connection = DriverManager.getConnection(dbUrl, USER, PASS);

        // Step 3: Ensure tables exist
        ensureTables(connection);

        return connection;
    }

    private static void ensureTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Employees table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) NOT NULL UNIQUE, " +
                "department VARCHAR(100) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Users table for login
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(50) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Optional: Insert default admin if table empty
            stmt.executeUpdate(
                "INSERT IGNORE INTO users (id, username, password) VALUES (1, 'admin', 'admin123')"
            );
        }
    }
}
