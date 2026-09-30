package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.AccountDAO;
import com.ashok.bank.model.Account;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public Account getAccountById(long accountId) throws DatabaseException {
        String sql = "SELECT account_id, user_id, branch_id, account_number, account_type, balance, status, created_at FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching account by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Account getAccountByNumber(String accountNumber) throws DatabaseException {
        String sql = "SELECT account_id, user_id, branch_id, account_number, account_type, balance, status, created_at FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching account by number: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Account> getAccountsByUserId(long userId) throws DatabaseException {
        String sql = "SELECT account_id, user_id, branch_id, account_number, account_type, balance, status, created_at FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching accounts by user ID: " + e.getMessage(), e);
        }
        return accounts;
    }

    @Override
    public long createAccount(long userId, long branchId, String accountNumber, String accountType)
            throws DatabaseException {
        String sql = "INSERT INTO accounts (user_id, branch_id, account_number, account_type, balance, status, created_at) VALUES (?, ?, ?, ?, 0.00, 'ACTIVE', GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, branchId);
            stmt.setString(3, accountNumber);
            stmt.setString(4, accountType);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to create account.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating account: " + e.getMessage(), e);
        }
        throw new DatabaseException("Failed to retrieve generated account ID.");
    }

    @Override
    public void updateBalance(long accountId, double newBalance) throws DatabaseException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setLong(2, accountId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Account not found or balance update failed.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating balance: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAccountStatus(long accountId, String status) throws DatabaseException {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, accountId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Account not found or status update failed.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating account status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Account> getAccountsByBranchId(long branchId) throws DatabaseException {
        String sql = "SELECT account_id, user_id, branch_id, account_number, account_type, balance, status, created_at FROM accounts WHERE branch_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching accounts by branch ID: " + e.getMessage(), e);
        }
        return accounts;
    }

    @Override
    public long countAccountsByBranchId(long branchId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE branch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting accounts by branch ID: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public long countTotalAccounts() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting total accounts: " + e.getMessage(), e);
        }
        return 0;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        long accountId = rs.getLong("account_id");
        long userId = rs.getLong("user_id");
        long branchId = rs.getLong("branch_id");
        String accountNumber = rs.getString("account_number");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String status = rs.getString("status");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new Account(accountId, userId, branchId, accountNumber, accountType, balance, status, createdAt);
    }
}
