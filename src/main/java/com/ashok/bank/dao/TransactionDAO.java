package com.ashok.bank.dao;

import com.ashok.bank.model.Transaction;
import com.ashok.bank.exception.DatabaseException;
import java.util.List;

public interface TransactionDAO {
    long createTransaction(Long sourceAccountId, Long destinationAccountId, String transactionType, double amount,
            String status) throws DatabaseException;

    Transaction getTransactionById(long transactionId) throws DatabaseException;

    List<Transaction> getTransactionsByAccountId(long accountId, int limit) throws DatabaseException;

    List<Transaction> getTransactionsByAccountIdAndMonths(long accountId, int months) throws DatabaseException;

    void updateTransactionStatus(long transactionId, String status) throws DatabaseException;

    List<Transaction> getAllTransactionsByDate(String date) throws DatabaseException;

    long countTransactionsByDate(String date) throws DatabaseException;
}
