-- ============================================================================
-- ASHOK BANK OF INDIA (ABI) - SYSTEM SCHEMA SETUP (MS SQL SERVER / T-SQL)
-- ============================================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ashok_bank_db')
BEGIN
    CREATE DATABASE ashok_bank_db;
END;
GO

USE ashok_bank_db;
GO

-- Drop existing tables in reverse foreign-key order for clean re-runs
IF OBJECT_ID('dbo.audit_logs', 'U') IS NOT NULL DROP TABLE dbo.audit_logs;
IF OBJECT_ID('dbo.loan_payments', 'U') IS NOT NULL DROP TABLE dbo.loan_payments;
IF OBJECT_ID('dbo.loans', 'U') IS NOT NULL DROP TABLE dbo.loans;
IF OBJECT_ID('dbo.cards', 'U') IS NOT NULL DROP TABLE dbo.cards;
IF OBJECT_ID('dbo.transactions', 'U') IS NOT NULL DROP TABLE dbo.transactions;
IF OBJECT_ID('dbo.accounts', 'U') IS NOT NULL DROP TABLE dbo.accounts;
IF OBJECT_ID('dbo.employees', 'U') IS NOT NULL DROP TABLE dbo.employees;
IF OBJECT_ID('dbo.branches', 'U') IS NOT NULL DROP TABLE dbo.branches;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
GO

-- 1. USERS TABLE
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone_number NVARCHAR(15) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) DEFAULT 'CUSTOMER',
    created_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT chk_user_role CHECK (role IN ('CUSTOMER', 'TELLER', 'MANAGER', 'ADMIN'))
);

-- 2. BRANCHES TABLE
CREATE TABLE branches (
    branch_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    branch_code NVARCHAR(20) NOT NULL UNIQUE,
    branch_name NVARCHAR(100) NOT NULL,
    ifsc_code NVARCHAR(11) NOT NULL UNIQUE,
    city NVARCHAR(50) NOT NULL,
    state NVARCHAR(50) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE()
);

-- 3. EMPLOYEES TABLE (Self-Referencing Hierarchy)
CREATE TABLE employees (
    employee_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    branch_id BIGINT NOT NULL,
    manager_id BIGINT NULL, -- Self-Join Key
    designation NVARCHAR(50) NOT NULL,
    salary DECIMAL(12, 2) NOT NULL,
    hired_at DATE NOT NULL,
    
    CONSTRAINT fk_emp_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_emp_branch FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    CONSTRAINT fk_emp_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id)
);

-- 4. ACCOUNTS TABLE
CREATE TABLE accounts (
    account_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    account_number NVARCHAR(20) NOT NULL UNIQUE,
    account_type NVARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status NVARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_accounts_branch FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    CONSTRAINT chk_positive_balance CHECK (balance >= 0.00),
    CONSTRAINT chk_acc_type CHECK (account_type IN ('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT')),
    CONSTRAINT chk_acc_status CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED'))
);

-- 5. TRANSACTIONS TABLE
CREATE TABLE transactions (
    transaction_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    source_account_id BIGINT NULL,
    destination_account_id BIGINT NULL,
    transaction_type NVARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    status NVARCHAR(20) DEFAULT 'PENDING',
    created_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT fk_tx_source FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_tx_dest FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id),
    CONSTRAINT chk_valid_amount CHECK (amount > 0.00),
    CONSTRAINT chk_tx_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'INTEREST_CREDIT')),
    CONSTRAINT chk_tx_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

-- 6. AUDIT LOGS TABLE
CREATE TABLE audit_logs (
    log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    action_type NVARCHAR(50) NOT NULL,
    old_balance DECIMAL(15, 2) NOT NULL,
    new_balance DECIMAL(15, 2) NOT NULL,
    logged_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT fk_audit_tx FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    CONSTRAINT fk_audit_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- 7. CARDS TABLE
CREATE TABLE cards (
    card_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    account_id BIGINT NOT NULL,
    card_number NVARCHAR(16) NOT NULL UNIQUE,
    card_type NVARCHAR(20) NOT NULL,
    cvv_hash NVARCHAR(255) NOT NULL,
    expiry_date DATE NOT NULL,
    status NVARCHAR(20) DEFAULT 'ACTIVE',
    
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    CONSTRAINT chk_card_type CHECK (card_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_card_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED'))
);

-- 8. LOANS TABLE
CREATE TABLE loans (
    loan_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    loan_type NVARCHAR(20) NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    term_months INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'APPLIED',
    created_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_loan_type CHECK (loan_type IN ('HOME', 'PERSONAL', 'AUTO', 'EDUCATION')),
    CONSTRAINT chk_loan_status CHECK (status IN ('APPLIED', 'APPROVED', 'ACTIVE', 'CLOSED', 'DEFAULTED', 'REJECTED'))
);

-- 9. LOAN PAYMENTS TABLE
CREATE TABLE loan_payments (
    payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    amount_paid DECIMAL(15, 2) NOT NULL,
    payment_date DATETIME2 DEFAULT GETDATE(),
    payment_method NVARCHAR(20) DEFAULT 'AUTO_DEBIT',

    CONSTRAINT fk_payments_loan FOREIGN KEY (loan_id) REFERENCES loans(loan_id),
    CONSTRAINT chk_pay_method CHECK (payment_method IN ('AUTO_DEBIT', 'MANUAL'))
);

-- INDEXES
CREATE INDEX idx_cards_card_number ON cards(card_number);
CREATE INDEX idx_accounts_acc_num ON accounts(account_number);
CREATE INDEX idx_tx_account_date ON transactions(source_account_id, created_at);
CREATE INDEX idx_tx_dest_date ON transactions(destination_account_id, created_at);
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_branch ON accounts(branch_id);
GO

-- ============================================================================
-- SAMPLE DATA INSERTION
-- ============================================================================

-- Insert Main Branch
INSERT INTO branches (branch_code, branch_name, ifsc_code, city, state)
VALUES ('ABI001', 'Ashok Main Branch', 'ABIB0001001', 'Bangalore', 'Karnataka');

-- Insert Sample Customers
-- Password for all test customers: Test@123 (pre-hashed with salt)
-- Hash generated using: PasswordUtil.hashPassword("Test@123")
INSERT INTO users (full_name, email, phone_number, password_hash, role)
VALUES 
('Ashok Kumar', 'ashok.kumar@email.com', '9876543210', 'jZae727K08KSvvVvgto6Zw=='+ CAST(NEWID() AS VARCHAR(36)), 'CUSTOMER'),
('Priya Singh', 'priya.singh@email.com', '9876543211', 'jZae727K08KSvvVvgto6Zw=='+ CAST(NEWID() AS VARCHAR(36)), 'CUSTOMER'),
('Rajesh Patel', 'rajesh.patel@email.com', '9876543212', 'jZae727K08KSvvVvgto6Zw=='+ CAST(NEWID() AS VARCHAR(36)), 'CUSTOMER');

-- Insert Sample Teller
INSERT INTO users (full_name, email, phone_number, password_hash, role)
VALUES ('Teller Smith', 'teller@ashokbank.com', '9111111111', 'jZae727K08KSvvVvgto6Zw=='+ CAST(NEWID() AS VARCHAR(36)), 'TELLER');

-- Insert Sample Manager
INSERT INTO users (full_name, email, phone_number, password_hash, role)
VALUES ('Manager John', 'manager@ashokbank.com', '9222222222', 'jZae727K08KSvvVvgto6Zw=='+ CAST(NEWID() AS VARCHAR(36)), 'MANAGER');

-- Insert Sample Accounts
INSERT INTO accounts (user_id, branch_id, account_number, account_type, balance, status)
VALUES 
(1, 1, 'ACC1234567890001', 'SAVINGS', 50000.00, 'ACTIVE'),
(1, 1, 'ACC1234567890002', 'CHECKING', 100000.00, 'ACTIVE'),
(2, 1, 'ACC1234567890003', 'SAVINGS', 75000.00, 'ACTIVE'),
(3, 1, 'ACC1234567890004', 'FIXED_DEPOSIT', 500000.00, 'ACTIVE');

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Verify database and tables
SELECT 'Database Setup Complete!' AS Status;

-- Check users
SELECT COUNT(*) AS Total_Users FROM users;

-- Check accounts
SELECT COUNT(*) AS Total_Accounts FROM accounts;

-- Check branches
SELECT COUNT(*) AS Total_Branches FROM branches;

-- Display all users with roles
SELECT user_id, full_name, email, role FROM users;

-- Display all accounts with balances
SELECT a.account_id, a.account_number, u.full_name, a.account_type, a.balance, a.status
FROM accounts a
INNER JOIN users u ON a.user_id = u.user_id;

GO
