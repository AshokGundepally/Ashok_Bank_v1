package com.ashok.bank.constants;

public class AppConstants {
    // Database Configuration
    public static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=ashok_bank_db;encrypt=true;trustServerCertificate=true";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "MsSql2026";
    public static final String DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // User Roles
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_TELLER = "TELLER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Account Status
    public static final String ACCOUNT_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_FROZEN = "FROZEN";
    public static final String ACCOUNT_CLOSED = "CLOSED";

    // Account Types
    public static final String ACCOUNT_SAVINGS = "SAVINGS";
    public static final String ACCOUNT_CHECKING = "CHECKING";
    public static final String ACCOUNT_FIXED_DEPOSIT = "FIXED_DEPOSIT";

    // Transaction Status
    public static final String TRANSACTION_PENDING = "PENDING";
    public static final String TRANSACTION_SUCCESS = "SUCCESS";
    public static final String TRANSACTION_FAILED = "FAILED";

    // Transaction Types
    public static final String TRANSACTION_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_TRANSFER = "TRANSFER";
    public static final String TRANSACTION_INTEREST_CREDIT = "INTEREST_CREDIT";

    // Loan Status
    public static final String LOAN_APPLIED = "APPLIED";
    public static final String LOAN_APPROVED = "APPROVED";
    public static final String LOAN_ACTIVE = "ACTIVE";
    public static final String LOAN_CLOSED = "CLOSED";
    public static final String LOAN_DEFAULTED = "DEFAULTED";
    public static final String LOAN_REJECTED = "REJECTED";

    // Loan Types
    public static final String LOAN_HOME = "HOME";
    public static final String LOAN_PERSONAL = "PERSONAL";
    public static final String LOAN_AUTO = "AUTO";
    public static final String LOAN_EDUCATION = "EDUCATION";

    // Card Status
    public static final String CARD_ACTIVE = "ACTIVE";
    public static final String CARD_BLOCKED = "BLOCKED";
    public static final String CARD_EXPIRED = "EXPIRED";

    // Card Types
    public static final String CARD_DEBIT = "DEBIT";
    public static final String CARD_CREDIT = "CREDIT";

    // Payment Methods
    public static final String PAYMENT_AUTO_DEBIT = "AUTO_DEBIT";
    public static final String PAYMENT_MANUAL = "MANUAL";

    // Session
    public static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    // Receipt Constants
    public static final String BANK_NAME = "ASHOK BANK OF INDIA (ABI)";
    public static final String RECEIPT_SEPARATOR = "------------------------------------";
}
