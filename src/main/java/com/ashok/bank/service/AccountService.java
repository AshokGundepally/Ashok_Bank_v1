package com.ashok.bank.service;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.AccountDAO;
import com.ashok.bank.dao.UserDAO;
import com.ashok.bank.dao.impl.AccountDAOImpl;
import com.ashok.bank.dao.impl.UserDAOImpl;
import com.ashok.bank.model.Account;
import com.ashok.bank.model.User;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.ValidationException;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private final AccountDAO accountDAO;
    private final UserDAO userDAO;

    public AccountService() {
        this.accountDAO = new AccountDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    public Account getAccountById(long accountId) throws DatabaseException {
        return accountDAO.getAccountById(accountId);
    }

    public Account getAccountByNumber(String accountNumber) throws DatabaseException, ValidationException {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new ValidationException("Account number cannot be empty.");
        }
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        return account;
    }

    public List<Account> getAccountsByUserId(long userId) throws DatabaseException {
        return accountDAO.getAccountsByUserId(userId);
    }

    public void validateAccountIsActive(long accountId) throws DatabaseException, ValidationException {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        if (!account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
            throw new ValidationException(
                    "Your account is inactive. Please visit your branch to reactivate your account.");
        }
    }

    public boolean isAccountActive(long accountId) throws DatabaseException {
        Account account = accountDAO.getAccountById(accountId);
        return account != null && account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE);
    }

    public long createAccount(long userId, long branchId, String accountType)
            throws DatabaseException, ValidationException {
        if (accountType == null || accountType.isEmpty()) {
            throw new ValidationException("Account type cannot be empty.");
        }
        String accountNumber = generateUniqueAccountNumber();
        return accountDAO.createAccount(userId, branchId, accountNumber, accountType);
    }

    public String generateUniqueAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();
    }

    public void freezeAccount(long accountId) throws DatabaseException, ValidationException {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        accountDAO.updateAccountStatus(accountId, AppConstants.ACCOUNT_FROZEN);
    }

    public void unfreezeAccount(long accountId) throws DatabaseException, ValidationException {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new ValidationException("Account not found.");
        }
        accountDAO.updateAccountStatus(accountId, AppConstants.ACCOUNT_ACTIVE);
    }

    public Account getCustomerPrimaryAccount(long userId) throws DatabaseException, ValidationException {
        List<Account> accounts = accountDAO.getAccountsByUserId(userId);
        if (accounts.isEmpty()) {
            throw new ValidationException("No accounts found for this user.");
        }
        return accounts.get(0);
    }

    public long countTotalAccounts() throws DatabaseException {
        return accountDAO.countTotalAccounts();
    }

    public long countAccountsByBranchId(long branchId) throws DatabaseException {
        return accountDAO.countAccountsByBranchId(branchId);
    }
}
