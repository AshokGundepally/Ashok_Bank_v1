package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.AuditLogDAO;
import com.ashok.bank.model.AuditLog;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public void createAuditLog(long transactionId, long accountId, String actionType, double oldBalance,
            double newBalance) throws DatabaseException {
        String sql = "INSERT INTO audit_logs (transaction_id, account_id, action_type, old_balance, new_balance, logged_at) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            stmt.setLong(2, accountId);
            stmt.setString(3, actionType);
            stmt.setDouble(4, oldBalance);
            stmt.setDouble(5, newBalance);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to create audit log.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating audit log: " + e.getMessage(), e);
        }
    }

    @Override
    public AuditLog getAuditLogById(long logId) throws DatabaseException {
        String sql = "SELECT log_id, transaction_id, account_id, action_type, old_balance, new_balance, logged_at FROM audit_logs WHERE log_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, logId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAuditLog(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching audit log by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<AuditLog> getAuditLogsByAccountId(long accountId) throws DatabaseException {
        String sql = "SELECT log_id, transaction_id, account_id, action_type, old_balance, new_balance, logged_at FROM audit_logs WHERE account_id = ? ORDER BY logged_at DESC";
        List<AuditLog> auditLogs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching audit logs by account ID: " + e.getMessage(), e);
        }
        return auditLogs;
    }

    @Override
    public List<AuditLog> getAllAuditLogs() throws DatabaseException {
        String sql = "SELECT log_id, transaction_id, account_id, action_type, old_balance, new_balance, logged_at FROM audit_logs ORDER BY logged_at DESC";
        List<AuditLog> auditLogs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all audit logs: " + e.getMessage(), e);
        }
        return auditLogs;
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        long logId = rs.getLong("log_id");
        long transactionId = rs.getLong("transaction_id");
        long accountId = rs.getLong("account_id");
        String actionType = rs.getString("action_type");
        double oldBalance = rs.getDouble("old_balance");
        double newBalance = rs.getDouble("new_balance");
        LocalDateTime loggedAt = rs.getTimestamp("logged_at").toLocalDateTime();

        return new AuditLog(logId, transactionId, accountId, actionType, oldBalance, newBalance, loggedAt);
    }
}
