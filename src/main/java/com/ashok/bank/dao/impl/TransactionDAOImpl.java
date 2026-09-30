package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.TransactionDAO;
import com.ashok.bank.model.Transaction;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public long createTransaction(Long sourceAccountId, Long destinationAccountId, String transactionType,
            double amount, String status) throws DatabaseException {
        String sql = "INSERT INTO transactions (source_account_id, destination_account_id, transaction_type, amount, status, created_at) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (sourceAccountId != null) {
                stmt.setLong(1, sourceAccountId);
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            if (destinationAccountId != null) {
                stmt.setLong(2, destinationAccountId);
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            stmt.setString(3, transactionType);
            stmt.setDouble(4, amount);
            stmt.setString(5, status);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to create transaction.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating transaction: " + e.getMessage(), e);
        }
        throw new DatabaseException("Failed to retrieve generated transaction ID.");
    }

    @Override
    public Transaction getTransactionById(long transactionId) throws DatabaseException {
        String sql = "SELECT transaction_id, source_account_id, destination_account_id, transaction_type, amount, status, created_at FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching transaction by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(long accountId, int limit) throws DatabaseException {
        String sql = "SELECT TOP (?) transaction_id, source_account_id, destination_account_id, transaction_type, amount, status, created_at FROM transactions WHERE source_account_id = ? OR destination_account_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setLong(2, accountId);
            stmt.setLong(3, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching transactions by account ID: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccountIdAndMonths(long accountId, int months) throws DatabaseException {
        String sql = "SELECT transaction_id, source_account_id, destination_account_id, transaction_type, amount, status, created_at FROM transactions WHERE (source_account_id = ? OR destination_account_id = ?) AND created_at >= DATEADD(MONTH, ?, GETDATE()) ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setLong(2, accountId);
            stmt.setInt(3, -months);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching transactions by account and months: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public void updateTransactionStatus(long transactionId, String status) throws DatabaseException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, transactionId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Transaction not found or status update failed.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating transaction status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getAllTransactionsByDate(String date) throws DatabaseException {
        String sql = "SELECT transaction_id, source_account_id, destination_account_id, transaction_type, amount, status, created_at FROM transactions WHERE CONVERT(DATE, created_at) = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching transactions by date: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public long countTransactionsByDate(String date) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE CONVERT(DATE, created_at) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting transactions by date: " + e.getMessage(), e);
        }
        return 0;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        long transactionId = rs.getLong("transaction_id");
        Long sourceAccountId = rs.getObject("source_account_id") != null ? rs.getLong("source_account_id") : null;
        Long destinationAccountId = rs.getObject("destination_account_id") != null
                ? rs.getLong("destination_account_id")
                : null;
        String transactionType = rs.getString("transaction_type");
        double amount = rs.getDouble("amount");
        String status = rs.getString("status");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new Transaction(transactionId, sourceAccountId, destinationAccountId, transactionType, amount, status,
                createdAt);
    }
}
