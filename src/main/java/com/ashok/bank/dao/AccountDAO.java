package com.ashok.bank.dao;

import com.ashok.bank.model.Account;
import com.ashok.bank.exception.DatabaseException;
import java.util.List;

public interface AccountDAO {
    Account getAccountById(long accountId) throws DatabaseException;

    Account getAccountByNumber(String accountNumber) throws DatabaseException;

    List<Account> getAccountsByUserId(long userId) throws DatabaseException;

    long createAccount(long userId, long branchId, String accountNumber, String accountType) throws DatabaseException;

    void updateBalance(long accountId, double newBalance) throws DatabaseException;

    void updateAccountStatus(long accountId, String status) throws DatabaseException;

    List<Account> getAccountsByBranchId(long branchId) throws DatabaseException;

    long countAccountsByBranchId(long branchId) throws DatabaseException;

    long countTotalAccounts() throws DatabaseException;
}
