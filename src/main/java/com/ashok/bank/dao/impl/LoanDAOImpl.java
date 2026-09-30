package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.LoanDAO;
import com.ashok.bank.model.Loan;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    @Override
    public long createLoan(long userId, String loanType, double principalAmount, double interestRate, int termMonths)
            throws DatabaseException {
        String sql = "INSERT INTO loans (user_id, loan_type, principal_amount, interest_rate, term_months, status, created_at) VALUES (?, ?, ?, ?, ?, 'APPLIED', GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);
            stmt.setString(2, loanType);
            stmt.setDouble(3, principalAmount);
            stmt.setDouble(4, interestRate);
            stmt.setInt(5, termMonths);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to create loan.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating loan: " + e.getMessage(), e);
        }
        throw new DatabaseException("Failed to retrieve generated loan ID.");
    }

    @Override
    public Loan getLoanById(long loanId) throws DatabaseException {
        String sql = "SELECT loan_id, user_id, loan_type, principal_amount, interest_rate, term_months, status, created_at FROM loans WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, loanId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching loan by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Loan> getLoansByUserId(long userId) throws DatabaseException {
        String sql = "SELECT loan_id, user_id, loan_type, principal_amount, interest_rate, term_months, status, created_at FROM loans WHERE user_id = ? ORDER BY created_at DESC";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching loans by user ID: " + e.getMessage(), e);
        }
        return loans;
    }

    @Override
    public List<Loan> getLoansByStatus(String status) throws DatabaseException {
        String sql = "SELECT loan_id, user_id, loan_type, principal_amount, interest_rate, term_months, status, created_at FROM loans WHERE status = ? ORDER BY created_at DESC";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching loans by status: " + e.getMessage(), e);
        }
        return loans;
    }

    @Override
    public void updateLoanStatus(long loanId, String status) throws DatabaseException {
        String sql = "UPDATE loans SET status = ? WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, loanId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Loan not found or status update failed.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating loan status: " + e.getMessage(), e);
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        long loanId = rs.getLong("loan_id");
        long userId = rs.getLong("user_id");
        String loanType = rs.getString("loan_type");
        double principalAmount = rs.getDouble("principal_amount");
        double interestRate = rs.getDouble("interest_rate");
        int termMonths = rs.getInt("term_months");
        String status = rs.getString("status");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new Loan(loanId, userId, loanType, principalAmount, interestRate, termMonths, status, createdAt);
    }
}
