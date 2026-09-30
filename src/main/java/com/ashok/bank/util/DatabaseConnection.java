package com.ashok.bank.util;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    static {
        try {
            Class.forName(AppConstants.DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQL Server JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        AppConstants.DB_URL,
                        AppConstants.DB_USER,
                        AppConstants.DB_PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to establish database connection: " + e.getMessage(), e);
        }
    }

    public static void closeConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to close database connection: " + e.getMessage(), e);
        }
    }

    public static void testConnection() throws DatabaseException {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection established successfully!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Connection test failed: " + e.getMessage(), e);
        }
    }
}
