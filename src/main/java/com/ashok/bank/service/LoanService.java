package com.ashok.bank.service;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.AccountDAO;
import com.ashok.bank.dao.AuditLogDAO;
import com.ashok.bank.dao.LoanDAO;
import com.ashok.bank.dao.TransactionDAO;
import com.ashok.bank.dao.impl.AccountDAOImpl;
import com.ashok.bank.dao.impl.AuditLogDAOImpl;
import com.ashok.bank.dao.impl.LoanDAOImpl;
import com.ashok.bank.dao.impl.TransactionDAOImpl;
import com.ashok.bank.model.Account;
import com.ashok.bank.model.Loan;
import com.ashok.bank.model.Transaction;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LoanService {
    private final LoanDAO loanDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final AuditLogDAO auditLogDAO;

    public LoanService() {
        this.loanDAO = new LoanDAOImpl();
        this.accountDAO = new AccountDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
        this.auditLogDAO = new AuditLogDAOImpl();
    }

    public long applyForLoan(long userId, String loanType, double principalAmount, int termMonths)
            throws DatabaseException, ValidationException {
        if (principalAmount <= 0) {
            throw new ValidationException("Loan amount must be positive.");
        }
        if (termMonths <= 0) {
            throw new ValidationException("Term must be positive.");
        }
        if (!isValidLoanType(loanType)) {
            throw new ValidationException("Invalid loan type.");
        }

        double interestRate = getInterestRate(loanType);
        return loanDAO.createLoan(userId, loanType, principalAmount, interestRate, termMonths);
    }

    public Loan getLoanById(long loanId) throws DatabaseException, ValidationException {
        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            throw new ValidationException("Loan not found.");
        }
        return loan;
    }

    public List<Loan> getLoansByUserId(long userId) throws DatabaseException {
        return loanDAO.getLoansByUserId(userId);
    }

    public List<Loan> getAppliedLoans() throws DatabaseException {
        return loanDAO.getLoansByStatus(AppConstants.LOAN_APPLIED);
    }

    public void approveLoan(long loanId, long userPrimaryAccountId) throws DatabaseException, ValidationException {
        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            throw new ValidationException("Loan not found.");
        }
        if (!loan.getStatus().equals(AppConstants.LOAN_APPLIED)) {
            throw new ValidationException("Only APPLIED loans can be approved.");
        }

        Account account = accountDAO.getAccountById(userPrimaryAccountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update loan status to APPROVED
            loanDAO.updateLoanStatus(loanId, AppConstants.LOAN_APPROVED);

            // Credit amount to account
            long transactionId = transactionDAO.createTransaction(null, userPrimaryAccountId,
                    AppConstants.TRANSACTION_INTEREST_CREDIT, loan.getPrincipalAmount(),
                    AppConstants.TRANSACTION_SUCCESS);

            double newBalance = account.getBalance() + loan.getPrincipalAmount();
            accountDAO.updateBalance(userPrimaryAccountId, newBalance);

            auditLogDAO.createAuditLog(transactionId, userPrimaryAccountId, "LOAN_APPROVAL", account.getBalance(),
                    newBalance);

            // Update loan status to ACTIVE
            loanDAO.updateLoanStatus(loanId, AppConstants.LOAN_ACTIVE);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DatabaseException("Loan approval failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new DatabaseException("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }

    public void rejectLoan(long loanId) throws DatabaseException, ValidationException {
        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            throw new ValidationException("Loan not found.");
        }
        if (!loan.getStatus().equals(AppConstants.LOAN_APPLIED)) {
            throw new ValidationException("Only APPLIED loans can be rejected.");
        }
        loanDAO.updateLoanStatus(loanId, AppConstants.LOAN_REJECTED);
    }

    public long countTransactionsByDate(String date) throws DatabaseException {
        java.util.List<Transaction> transactions = transactionDAO.getAllTransactionsByDate(date);
        return transactions.size();
    }

    private boolean isValidLoanType(String loanType) {
        return loanType.equals(AppConstants.LOAN_HOME) ||
                loanType.equals(AppConstants.LOAN_PERSONAL) ||
                loanType.equals(AppConstants.LOAN_AUTO) ||
                loanType.equals(AppConstants.LOAN_EDUCATION);
    }

    private double getInterestRate(String loanType) {
        return switch (loanType) {
            case AppConstants.LOAN_HOME -> 7.5;
            case AppConstants.LOAN_AUTO -> 8.5;
            case AppConstants.LOAN_PERSONAL -> 10.0;
            case AppConstants.LOAN_EDUCATION -> 6.5;
            default -> 9.0;
        };
    }
}
