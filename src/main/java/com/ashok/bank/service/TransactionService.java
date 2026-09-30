package com.ashok.bank.service;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.AccountDAO;
import com.ashok.bank.dao.AuditLogDAO;
import com.ashok.bank.dao.TransactionDAO;
import com.ashok.bank.dao.impl.AccountDAOImpl;
import com.ashok.bank.dao.impl.AuditLogDAOImpl;
import com.ashok.bank.dao.impl.TransactionDAOImpl;
import com.ashok.bank.model.Account;
import com.ashok.bank.model.Transaction;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.InsufficientBalanceException;
import com.ashok.bank.exception.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;
    private final AuditLogDAO auditLogDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAOImpl();
        this.accountDAO = new AccountDAOImpl();
        this.auditLogDAO = new AuditLogDAOImpl();
    }

    public long depositAmount(long accountId, double amount)
            throws DatabaseException, ValidationException, InsufficientBalanceException {
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive.");
        }

        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        if (!account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
            throw new ValidationException("Account is not active.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Create transaction record
            long transactionId = transactionDAO.createTransaction(null, accountId, AppConstants.TRANSACTION_DEPOSIT,
                    amount, AppConstants.TRANSACTION_PENDING);

            // Update account balance
            double newBalance = account.getBalance() + amount;
            accountDAO.updateBalance(accountId, newBalance);

            // Update transaction status
            transactionDAO.updateTransactionStatus(transactionId, AppConstants.TRANSACTION_SUCCESS);

            // Create audit log
            auditLogDAO.createAuditLog(transactionId, accountId, AppConstants.TRANSACTION_DEPOSIT, account.getBalance(),
                    newBalance);

            conn.commit();
            return transactionId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DatabaseException("Deposit transaction failed: " + e.getMessage(), e);
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

    public long withdrawAmount(long accountId, double amount)
            throws DatabaseException, ValidationException, InsufficientBalanceException {
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive.");
        }

        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        if (!account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
            throw new ValidationException("Account is not active.");
        }
        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Available: ₹" + account.getBalance());
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Create transaction record
            long transactionId = transactionDAO.createTransaction(accountId, null, AppConstants.TRANSACTION_WITHDRAWAL,
                    amount, AppConstants.TRANSACTION_PENDING);

            // Update account balance
            double newBalance = account.getBalance() - amount;
            accountDAO.updateBalance(accountId, newBalance);

            // Update transaction status
            transactionDAO.updateTransactionStatus(transactionId, AppConstants.TRANSACTION_SUCCESS);

            // Create audit log
            auditLogDAO.createAuditLog(transactionId, accountId, AppConstants.TRANSACTION_WITHDRAWAL,
                    account.getBalance(), newBalance);

            conn.commit();
            return transactionId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DatabaseException("Withdrawal transaction failed: " + e.getMessage(), e);
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

    public long transferMoney(long sourceAccountId, long destinationAccountId, double amount)
            throws DatabaseException, ValidationException, InsufficientBalanceException {
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive.");
        }
        if (sourceAccountId == destinationAccountId) {
            throw new ValidationException("Source and destination accounts must be different.");
        }

        Account sourceAccount = accountDAO.getAccountById(sourceAccountId);
        Account destAccount = accountDAO.getAccountById(destinationAccountId);

        if (sourceAccount == null) {
            throw new ValidationException("Source account not found.");
        }
        if (destAccount == null) {
            throw new ValidationException("Destination account not found.");
        }
        if (!sourceAccount.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
            throw new ValidationException("Source account is not active.");
        }
        if (!destAccount.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
            throw new ValidationException("Destination account is not active.");
        }
        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Available: ₹" + sourceAccount.getBalance());
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Create transaction record
            long transactionId = transactionDAO.createTransaction(sourceAccountId, destinationAccountId,
                    AppConstants.TRANSACTION_TRANSFER, amount, AppConstants.TRANSACTION_PENDING);

            // Update source account balance
            double sourceNewBalance = sourceAccount.getBalance() - amount;
            accountDAO.updateBalance(sourceAccountId, sourceNewBalance);

            // Update destination account balance
            double destNewBalance = destAccount.getBalance() + amount;
            accountDAO.updateBalance(destinationAccountId, destNewBalance);

            // Update transaction status
            transactionDAO.updateTransactionStatus(transactionId, AppConstants.TRANSACTION_SUCCESS);

            // Create audit logs
            auditLogDAO.createAuditLog(transactionId, sourceAccountId, AppConstants.TRANSACTION_TRANSFER,
                    sourceAccount.getBalance(), sourceNewBalance);
            auditLogDAO.createAuditLog(transactionId, destinationAccountId, AppConstants.TRANSACTION_TRANSFER,
                    destAccount.getBalance(), destNewBalance);

            conn.commit();
            return transactionId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DatabaseException("Transfer transaction failed: " + e.getMessage(), e);
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

    public Transaction getTransactionById(long transactionId) throws DatabaseException {
        return transactionDAO.getTransactionById(transactionId);
    }

    public List<Transaction> getLastNTransactions(long accountId, int limit) throws DatabaseException {
        return transactionDAO.getTransactionsByAccountId(accountId, limit);
    }

    public List<Transaction> getTransactionsForMonths(long accountId, int months) throws DatabaseException {
        return transactionDAO.getTransactionsByAccountIdAndMonths(accountId, months);
    }
}
