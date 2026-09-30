package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.UserDAO;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.model.User;
import com.ashok.bank.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDAOImpl implements UserDAO {

    @Override
    public User getUserByEmail(String email) throws DatabaseException {

        String sql = "SELECT user_id, full_name, email, phone_number, password, role, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching user by email.", e);
        }

        return null;
    }

    @Override
    public User getUserById(long userId) throws DatabaseException {

        String sql = "SELECT user_id, full_name, email, phone_number, password, role, created_at FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching user by ID.", e);
        }

        return null;
    }

    @Override
    public long createUser(String fullName,
            String email,
            String phoneNumber,
            String password,
            String role) throws DatabaseException {

        String sql = "INSERT INTO users(full_name,email,phone_number,password,role,created_at) VALUES(?,?,?,?,?,GETDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, password);
            stmt.setString(5, role);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new DatabaseException("Failed to create user.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating user.", e);
        }

        throw new DatabaseException("Failed to retrieve generated user id.");
    }

    @Override
    public void updatePassword(long userId,
            String newPassword) throws DatabaseException {

        String sql = "UPDATE users SET password=? WHERE user_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setLong(2, userId);

            if (stmt.executeUpdate() == 0) {
                throw new DatabaseException("Password update failed.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error updating password.", e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DatabaseException {

        String sql = "SELECT COUNT(*) FROM users WHERE email=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking email.", e);
        }
    }

    @Override
    public boolean phoneExists(String phone) throws DatabaseException {

        String sql = "SELECT COUNT(*) FROM users WHERE phone_number=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking phone.", e);
        }
    }

    @Override
    public User getUserByPhone(String phoneNumber) throws DatabaseException {
        String sql = "SELECT user_id, full_name, email, phone_number, password_hash AS password, role, created_at FROM users WHERE phone_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching user by phone number.", e);
        }
        return null;
    }

    @Override
    public long countUsersByRole(String role) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting users by role.", e);
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }
}