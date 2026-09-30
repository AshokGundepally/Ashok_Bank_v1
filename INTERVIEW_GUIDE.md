# 🎓 Bank Management System - Interview Guide

## Complete Q&A Document for Technical Interviews

---

## Table of Contents

1. [Project Overview Questions](#project-overview-questions)
2. [Object-Oriented Programming (OOP) Questions](#object-oriented-programming-oop-questions)
3. [Database Design & Architecture Questions](#database-design--architecture-questions)
4. [Design Patterns & Architecture Questions](#design-patterns--architecture-questions)
5. [JDBC & Database Connectivity Questions](#jdbc--database-connectivity-questions)
6. [Security & Performance Questions](#security--performance-questions)
7. [Advanced Scenario-Based Questions](#advanced-scenario-based-questions)

---

## 📌 Project Overview Questions

### Q1: Tell me about your project. What is it, and what problem does it solve?

**Answer:**

This is a **Console-Based Bank Management System** built with Java, JDBC, and MS SQL Server. It's designed to simulate real-world banking operations with role-based access control for different user types.

**What it does:**

- Manages user accounts, transactions, and loans
- Handles customer deposits, withdrawals, and fund transfers
- Manages loan applications and approvals
- Maintains audit logs for all financial operations
- Provides role-based dashboards for managers

**Problem it solves:**

1. **Multi-user access control** - Different roles (Customer, Teller, Manager) with specific permissions
2. **Financial transaction integrity** - Transactions are atomic (all-or-nothing using database transactions)
3. **Audit trail** - Every balance change is logged for compliance and security
4. **Data consistency** - Database constraints ensure business rules are always enforced
5. **Performance** - Strategic indexing ensures fast lookups even with large datasets

**Real-world value:**

- Banks need to process thousands of transactions daily while maintaining accuracy
- Regulatory compliance requires complete audit trails
- Different employees need different access levels
- System must handle concurrent operations safely

---

### Q2: What are the key features of your application?

**Answer:**

**For Customers:**

- Login with email/password authentication
- View all accounts with real-time balances
- Deposit money into accounts
- Withdraw money (with balance validation)
- Transfer funds between accounts
- Apply for loans (Home, Auto, Personal, Education)
- View transaction history with date filtering
- Change password securely
- Freeze/unfreeze accounts

**For Tellers:**

- Search customers by account number, email, or phone
- Handle cash deposits and withdrawals
- Generate transaction receipts
- Open new customer accounts
- View pending loan applications
- Manage account status (freeze/unfreeze)
- Dashboard showing today's transactions

**For Managers:**

- Analytics dashboard showing:
  - Total customers
  - Total accounts
  - Today's transactions
  - Branch employees
- View all customers and their accounts
- Approve/reject loan applications
- Auto-credit principal amount to customer account on approval
- View and filter audit logs
- Generate compliance reports

---

### Q3: What technologies did you use and why?

**Answer:**

| Technology             | Purpose               | Why Chosen                                                      |
| ---------------------- | --------------------- | --------------------------------------------------------------- |
| **Java 17**            | Backend language      | Strong OOP support, Type-safe, Enterprise standard              |
| **JDBC**               | Database connectivity | Direct database access without abstraction layers, Full control |
| **MS SQL Server**      | Database              | Enterprise RDBMS, Excellent constraint support, T-SQL power     |
| **Maven**              | Build tool            | Dependency management, Industry standard                        |
| **PreparedStatements** | SQL queries           | Prevent SQL injection, Query caching                            |

**Architecture Justification:**

I used a **3-layer architecture**:

```
UI Layer (Customer/Teller/Manager Modules)
    ↓
Service Layer (Business Logic)
    ↓
DAO Layer (Data Access Objects)
    ↓
Database (SQL Server)
```

**Why this architecture?**

- **Separation of Concerns** - Each layer has single responsibility
- **Testability** - Easy to mock DAOs for testing services
- **Maintainability** - Changes to database don't affect UI
- **Reusability** - Services can be used by multiple UI modules
- **Scalability** - Can replace JDBC with Hibernate/Spring Data later

---

## 💻 Object-Oriented Programming (OOP) Questions

### Q4: Explain how you've used OOP principles (Encapsulation, Inheritance, Polymorphism, Abstraction) in your project.

**Answer:**

#### **1. Encapsulation**

**Example: User Model Class**

```java
public class User {
    private long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String role;
    private LocalDateTime createdAt;

    // Public getters/setters (controlled access)
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) {
        // Validation before setting
        if (role.equals("CUSTOMER") || role.equals("TELLER") ||
            role.equals("MANAGER") || role.equals("ADMIN")) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
```

**Benefits:**

- Internal representation is hidden
- Business logic for validation is encapsulated
- Changes to internal fields don't break external code
- Role validation happens before setting (Data Integrity)

---

#### **2. Abstraction**

**Example: DAO Interface**

```java
public interface UserDAO {
    User getUserByEmail(String email) throws DatabaseException;
    User getUserById(long userId) throws DatabaseException;
    long createUser(String fullName, String email, String phoneNumber,
                    String password, String role) throws DatabaseException;
    void updatePassword(long userId, String newPassword) throws DatabaseException;
}
```

**Implementation:**

```java
public class UserDAOImpl implements UserDAO {
    @Override
    public User getUserByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        // Database connection, prepared statement, result set handling
        // Complex details hidden from caller
    }
}
```

**Benefits:**

- Client code works with `UserDAO` interface, not implementation
- Database details are completely hidden
- Easy to switch implementations (e.g., Oracle to PostgreSQL)
- Tests can use mock implementations

---

#### **3. Inheritance**

**Example: Exception Hierarchy**

```java
public abstract class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}

public class DatabaseException extends BankException {
    public DatabaseException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
}

public class AuthenticationException extends BankException {
    public AuthenticationException(String message) {
        super(message);
    }
}

public class ValidationException extends BankException {
    public ValidationException(String message) {
        super(message);
    }
}
```

**Benefits:**

- Common exception handling: `catch (BankException e)` catches all
- Specific exception handling: `catch (DatabaseException e)` for specific errors
- Hierarchical error handling
- Code reuse through inheritance

---

#### **4. Polymorphism**

**Example: Module Implementation**

```java
// Abstract behavior (implicit interface)
// Each module has start() method with different implementation

public class CustomerModule {
    public void start() {
        // Customer-specific logic
        // Customer can only see their own accounts
    }
}

public class TellerModule {
    public void start() {
        // Teller-specific logic
        // Teller can search any customer
    }
}

public class ManagerModule {
    public void start() {
        // Manager-specific logic
        // Manager can see all data
    }
}

// In Main.java - Polymorphic behavior
public class Main {
    public static void main(String[] args) {
        User user = authenticationService.login(email, password);

        // Same method call, different behavior based on role
        if (user.getRole().equals("CUSTOMER")) {
            new CustomerModule().start();
        } else if (user.getRole().equals("TELLER")) {
            new TellerModule().start();
        } else if (user.getRole().equals("MANAGER")) {
            new ManagerModule().start();
        }
    }
}
```

**Benefits:**

- Same interface (start() method) with different implementations
- Easy to add new roles without changing existing code
- Role-based behavior without if-else statements (good design)
- Open/Closed Principle: Open for extension, Closed for modification

**Better Polymorphic Design:**

```java
public interface UserModule {
    void start();
}

public class CustomerModule implements UserModule {
    @Override
    public void start() { /* customer logic */ }
}

public class TellerModule implements UserModule {
    @Override
    public void start() { /* teller logic */ }
}

public class ManagerModule implements UserModule {
    @Override
    public void start() { /* manager logic */ }
}

// Factory Pattern + Polymorphism
public class ModuleFactory {
    public static UserModule getModule(String role) {
        return switch(role) {
            case "CUSTOMER" -> new CustomerModule();
            case "TELLER" -> new TellerModule();
            case "MANAGER" -> new ManagerModule();
            default -> throw new IllegalArgumentException("Unknown role");
        };
    }
}
```

---

### Q5: Explain the Model classes and their responsibilities.

**Answer:**

**Model Classes = Data Transfer Objects (DTOs)**

They represent database entities and carry data between layers.

```java
// 1. User Model
public class User {
    private long userId;              // Unique identifier (PK)
    private String fullName;          // User's name
    private String email;             // Unique email (UK)
    private String phoneNumber;       // Unique phone (UK)
    private String password;          // Plaintext password
    private String role;              // CUSTOMER/TELLER/MANAGER
    private LocalDateTime createdAt;  // Registration timestamp

    // Getters/Setters with validation
}

// 2. Account Model
public class Account {
    private long accountId;           // Unique identifier (PK)
    private long userId;              // Owner (FK to users)
    private long branchId;            // Branch (FK to branches)
    private String accountNumber;     // Unique account number (UK)
    private String accountType;       // SAVINGS/CHECKING/FIXED_DEPOSIT
    private double balance;           // Current balance
    private String status;            // ACTIVE/FROZEN/CLOSED
    private LocalDateTime createdAt;  // Account opening date

    // Methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new ValidationException("Amount must be positive");
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new InsufficientBalanceException(...);
        this.balance -= amount;
    }
}

// 3. Transaction Model
public class Transaction {
    private long transactionId;           // Unique identifier (PK)
    private long sourceAccountId;         // From account (FK)
    private long destinationAccountId;    // To account (FK)
    private String transactionType;       // DEPOSIT/WITHDRAWAL/TRANSFER
    private double amount;                // Transaction amount
    private String status;                // PENDING/SUCCESS/FAILED
    private LocalDateTime createdAt;      // Transaction timestamp

    // Validation
    public boolean isValid() {
        return amount > 0 &&
               (status.equals("PENDING") || status.equals("SUCCESS") || status.equals("FAILED"));
    }
}

// 4. Loan Model
public class Loan {
    private long loanId;              // Unique identifier (PK)
    private long userId;              // Applicant (FK)
    private String loanType;          // HOME/PERSONAL/AUTO/EDUCATION
    private double principalAmount;   // Loan amount
    private double interestRate;      // Interest percentage
    private int termMonths;           // Loan duration in months
    private String status;            // APPLIED/APPROVED/ACTIVE/REJECTED/CLOSED
    private LocalDateTime createdAt;  // Application date
}

// 5. AuditLog Model
public class AuditLog {
    private long logId;               // Unique identifier (PK)
    private long transactionId;       // Transaction reference (FK)
    private long accountId;           // Account reference (FK)
    private String actionType;        // DEPOSIT/WITHDRAWAL/TRANSFER/APPROVAL
    private double oldBalance;        // Balance before action
    private double newBalance;        // Balance after action
    private LocalDateTime loggedAt;   // When recorded

    // Purpose: Complete audit trail for compliance
}
```

**Responsibilities:**

- ✅ Store data from database
- ✅ Transport data between layers
- ✅ Basic validation (getters/setters)
- ✅ Business logic methods (e.g., withdraw, deposit)
- ✅ Represent database entities exactly

---

### Q6: What is the difference between Model, DAO, and Service layers? Why separate them?

**Answer:**

```
┌─────────────────────────────────────┐
│         UI LAYER                    │
│  (CustomerModule, TellerModule)     │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│         SERVICE LAYER               │
│  (AuthenticationService)            │
│  - Business Logic                   │
│  - Validation                       │
│  - Orchestration                    │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│         DAO LAYER                   │
│  (UserDAOImpl, AccountDAOImpl)        │
│  - SQL Query Execution              │
│  - Result Mapping                   │
│  - Database Operations              │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│         DATABASE LAYER              │
│  (SQL Server)                       │
│  - Tables, Constraints, Indexes     │
└─────────────────────────────────────┘
```

#### **Layer Responsibilities:**

| Layer       | Responsibility      | Example                                                                      |
| ----------- | ------------------- | ---------------------------------------------------------------------------- |
| **Model**   | Data representation | `User user = new User(); user.setEmail("..."); user.setRole("CUSTOMER");`    |
| **DAO**     | Database operations | `User user = userDAO.getUserByEmail("ashok@email.com");`                     |
| **Service** | Business logic      | `authService.login(email, password);` validates password, handles exceptions |
| **UI**      | User interaction    | `CustomerModule.displayMenu();`                                              |

#### **Why Separate?**

**1. Single Responsibility Principle (SRP)**

```java
// ❌ BAD: Mixed concerns
public class UserService {
    public User login(String email, String password) {
        // Database logic
        Connection conn = DriverManager.getConnection(...);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        User user = new User();
        user.setEmail(rs.getString("email"));

        // Business logic
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Invalid password");
        }

        return user;
    }
}

// ✅ GOOD: Separated concerns
public class UserDAOImpl implements UserDAO {
    public User getUserByEmail(String email) throws DatabaseException {
        // ONLY database logic
        Connection conn = DriverManager.getConnection(...);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }
}

public class AuthenticationService {
    private UserDAO userDAO;

    public User login(String email, String password) throws AuthenticationException {
        // ONLY business logic
        User user = userDAO.getUserByEmail(email);
        if (user == null) throw new AuthenticationException("Invalid email or password");
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }
        return user;
    }
}
```

**2. Testability**

```java
// Easy to test service with mock DAO
public class AuthenticationServiceTest {
    @Test
    public void testLoginWithInvalidPassword() {
        // Mock DAO returns a user
        UserDAO mockDAO = mock(UserDAO.class);
        User user = new User();
        user.setPassword("CorrectPassword");
        when(mockDAO.getUserByEmail("test@email.com")).thenReturn(user);

        // Test service logic
        AuthenticationService service = new AuthenticationService(mockDAO);
        assertThrows(AuthenticationException.class,
            () -> service.login("test@email.com", "WrongPassword"));
    }
}
```

**3. Reusability**

```java
// DAO can be used by multiple services
UserDAO userDAO = new UserDAOImpl();

// Used by AuthenticationService
AuthenticationService authService = new AuthenticationService(userDAO);
User user = authService.login(email, password);

// Used by CustomerService
CustomerService custService = new CustomerService(userDAO);
User customer = custService.getCustomerProfile(userId);

// Used by ManagerService
ManagerService mgmtService = new ManagerService(userDAO);
List<User> allCustomers = mgmtService.getAllCustomers();
```

**4. Maintainability**

```java
// Change database from SQL Server to PostgreSQL?
// Only update DAO implementation, services don't change

public class UserDAOImpl implements UserDAO {
    // OLD: MS SQL Server
    String sql = "SELECT user_id, full_name, ... FROM users WHERE email = ?";

    // NEW: PostgreSQL
    String sql = "SELECT user_id, full_name, ... FROM users WHERE email = $1";
    // Service code stays EXACTLY the same!
}
```

**5. Team Development**

- Frontend developer works on UI layer
- Backend developer works on Service layer
- Database developer works on DAO layer
- No merge conflicts, parallel development

---

## 🗄️ Database Design & Architecture Questions

### Q7: Explain your database schema. What tables do you have and what are their relationships?

**Answer:**

**9 Core Tables:**

```
1. USERS (User Accounts)
   - user_id (PK), full_name, email (UK), phone_number (UK),
     password, role (CUSTOMER/TELLER/MANAGER/ADMIN), created_at

2. BRANCHES (Bank Locations)
   - branch_id (PK), branch_code (UK), branch_name,
     ifsc_code (UK), city, state, created_at

3. EMPLOYEES (Teller/Manager Records)
   - employee_id (PK), user_id (FK,UK), branch_id (FK),
     manager_id (FK - Self Join), designation, salary, hired_at

4. ACCOUNTS (Customer Accounts)
   - account_id (PK), user_id (FK), branch_id (FK),
     account_number (UK), account_type (SAVINGS/CHECKING/FIXED_DEPOSIT),
     balance, status (ACTIVE/FROZEN/CLOSED), created_at

5. TRANSACTIONS (Financial Transactions)
   - transaction_id (PK), source_account_id (FK), destination_account_id (FK),
     transaction_type (DEPOSIT/WITHDRAWAL/TRANSFER/INTEREST_CREDIT),
     amount, status (PENDING/SUCCESS/FAILED), created_at

6. AUDIT_LOGS (Compliance Trail)
   - log_id (PK), transaction_id (FK), account_id (FK), action_type,
     old_balance, new_balance, logged_at

7. CARDS (Debit/Credit Cards)
   - card_id (PK), account_id (FK), card_number (UK), card_type (DEBIT/CREDIT),
     cvv_hash, expiry_date, status (ACTIVE/BLOCKED/EXPIRED)

8. LOANS (Loan Applications)
   - loan_id (PK), user_id (FK), loan_type (HOME/PERSONAL/AUTO/EDUCATION),
     principal_amount, interest_rate, term_months,
     status (APPLIED/APPROVED/ACTIVE/REJECTED/CLOSED), created_at

9. LOAN_PAYMENTS (Payment Records)
   - payment_id (PK), loan_id (FK), amount_paid, payment_date,
     payment_method (AUTO_DEBIT/MANUAL)
```

**Relationships (ER Diagram):**

```
USERS (1) ──────────────── (M) ACCOUNTS
  │                          └──→ TRANSACTIONS
  │                          └──→ CARDS
  │                          └──→ AUDIT_LOGS
  │
  ├──────────────────────────────→ (M) LOANS
  │
  └──────────────────────────────→ (M) EMPLOYEES

BRANCHES (1) ────────────── (M) ACCOUNTS
             ────────────── (M) EMPLOYEES

LOANS (1) ────────────────────→ (M) LOAN_PAYMENTS

TRANSACTIONS ←──────────────────→ AUDIT_LOGS
```

**Key Constraints:**

1. **Primary Keys** - Unique identification
2. **Foreign Keys** - Referential integrity (e.g., account must belong to existing user)
3. **Unique Constraints** - No duplicates (email, phone, account_number)
4. **Check Constraints** - Business rules (balance >= 0, valid role, valid status)
5. **Cascading Deletes** - Delete user → Delete user's accounts, loans, transactions

---

### Q8: What are constraints and why are they important? Give examples from your schema.

**Answer:**

**Constraints = Database-Level Rules that Ensure Data Integrity**

#### **Types & Examples:**

**1. Primary Key Constraint (PK)**

```sql
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ...
)
```

**Purpose:**

- Unique identification of each record
- Auto-increment ensures no duplicates
- Fast lookups on user_id

**Example in Code:**

```java
User user = userDAO.getUserById(1);  // Fast because user_id is PK
```

---

**2. Foreign Key Constraint (FK)**

```sql
CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
)
```

**Purpose:**

- Ensures accounts belong to valid users
- If user deleted, their accounts are automatically deleted
- Prevents orphaned records

**Example in Code:**

```java
// This is guaranteed to work - user exists
long accountId = accountDAO.createAccount(userId, branchId, ...);

// If we delete user, cascade delete removes all accounts automatically
userDAO.deleteUser(userId);  // All accounts for this user deleted too
```

---

**3. Unique Constraint (UK)**

```sql
CREATE TABLE users (
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone_number NVARCHAR(15) NOT NULL UNIQUE,
    ...
)

CREATE TABLE accounts (
    account_number NVARCHAR(20) NOT NULL UNIQUE,
    ...
)
```

**Purpose:**

- Prevent duplicate emails (no two users with same email)
- Prevent duplicate account numbers
- Database enforces this, application doesn't need to

**Example in Code:**

```java
// ❌ This will fail if email already exists
userDAO.createUser("John", "ashok.kumar@email.com", "9876543210", "Test@123", "CUSTOMER");
// DatabaseException: Unique constraint violated

// ✅ This will succeed
userDAO.createUser("John", "newemail@email.com", "9876543210", "Test@123", "CUSTOMER");
```

---

**4. Check Constraint**

```sql
-- Constraint on balance
CREATE TABLE accounts (
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_positive_balance CHECK (balance >= 0.00),
    ...
)

-- Constraint on role
CREATE TABLE users (
    role NVARCHAR(20),
    CONSTRAINT chk_user_role CHECK (role IN ('CUSTOMER', 'TELLER', 'MANAGER', 'ADMIN'))
)

-- Constraint on status
CREATE TABLE accounts (
    status NVARCHAR(20),
    CONSTRAINT chk_acc_status CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED'))
)
```

**Purpose:**

- Enforce business rules at database level
- Prevent negative balances
- Prevent invalid status values
- No application bug can violate these rules

**Example in Code:**

```java
// ❌ This will fail - violates check constraint
Account acc = new Account();
acc.setBalance(-1000);
accountDAO.updateBalance(accId, -1000);  // DatabaseException!

// ✅ This will succeed
accountDAO.updateBalance(accId, 1000);  // Positive balance
```

---

**5. Not Null Constraint**

```sql
CREATE TABLE users (
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) DEFAULT 'CUSTOMER',
    ...
)
```

**Purpose:**

- Required fields must always have values
- Prevents incomplete data
- No null email, password, or name

---

#### **Why Constraints Are Crucial:**

**1. Data Integrity**

```
Without constraints:
- User deleted but accounts still exist (orphaned data)
- Duplicate emails in database (broken uniqueness)
- Negative balances (impossible in real banking)

With constraints:
- All data is consistent
- No orphaned records
- Business rules enforced at lowest level
```

**2. Security**

```java
// ❌ Without NOT NULL constraint on password
User hacker = new User();
hacker.setPassword(null);  // Can create user with no password!
userDAO.createUser("Hacker", "hacker@email.com", "9000000000", null, "CUSTOMER");

// ✅ With NOT NULL constraint
userDAO.createUser("Hacker", "hacker@email.com", "9000000000", null, "CUSTOMER");
// DatabaseException: NOT NULL constraint violated!
```

**3. Reduced Code**

```java
// ❌ Without constraints - lots of validation
public void transferFunds(Account from, Account to, double amount) {
    if (from == null) throw new ValidationException("From account null");
    if (to == null) throw new ValidationException("To account null");
    if (amount <= 0) throw new ValidationException("Amount must be positive");
    if (from.getBalance() < amount) throw new InsufficientBalanceException(...);
    if (!from.getStatus().equals("ACTIVE")) throw new ValidationException("From account not active");
    if (!to.getStatus().equals("ACTIVE")) throw new ValidationException("To account not active");
    // 20 more checks...
}

// ✅ With constraints - minimal validation
public void transferFunds(Account from, Account to, double amount) {
    if (amount > from.getBalance()) throw new InsufficientBalanceException(...);
    // Database constraints handle the rest!
}
```

---

### Q9: What are indexes and how do they improve performance?

**Answer:**

**Indexes = Database Structures That Speed Up Data Retrieval**

Think of it like a book's index: Instead of reading every page to find "Transactions", you look up "T" in the index and jump directly to page 156.

#### **Types of Indexes in Your Project:**

**1. Single-Column Indexes**

```sql
CREATE INDEX idx_cards_card_number ON cards(card_number);
CREATE INDEX idx_accounts_acc_num ON accounts(account_number);
CREATE INDEX idx_accounts_user ON accounts(user_id);
```

**Example Performance:**

```
Without index:
SELECT * FROM accounts WHERE account_number = 'ACC123456'
- Scans ALL 100,000 accounts → ~100ms ❌

With index:
SELECT * FROM accounts WHERE account_number = 'ACC123456'
- Uses B-Tree to find directly → ~1ms ✅
- 100x FASTER!
```

**Usage in Code:**

```java
// ✅ FAST - Uses idx_accounts_acc_num
Account acc = accountDAO.getAccountByNumber("ACC1234567890001");

// ✅ FAST - Uses idx_accounts_user
List<Account> accounts = accountDAO.getAccountsByUserId(123);
```

---

**2. Composite Indexes (Multiple Columns)**

```sql
CREATE INDEX idx_tx_account_date ON transactions(source_account_id, created_at);
CREATE INDEX idx_tx_dest_date ON transactions(destination_account_id, created_at);
```

**Why Composite?**

```
Query: Find all transactions for account 123 in last 30 days

SELECT * FROM transactions
WHERE source_account_id = 123
AND created_at > DATEADD(DAY, -30, GETDATE())

With composite index (source_account_id, created_at):
- B-Tree organized by (account_id, date)
- Directly finds account AND filters by date
- ~5ms ✅

Without index:
- Scans all transactions
- Filters manually
- ~500ms ❌
```

**Usage in Code:**

```java
// ✅ FAST - Uses composite index
List<Transaction> txns = transactionDAO.getTransactionsByAccountIdAndMonths(123, 6);

// Internally:
// SELECT * FROM transactions
// WHERE source_account_id = 123
// AND created_at > (NOW - 6 months)
```

---

**3. Implicit Indexes (From Constraints)**

```sql
-- Primary Keys automatically indexed
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY  -- Automatic index!
)

-- Unique Constraints automatically indexed
CREATE TABLE users (
    email UNIQUE  -- Automatic index!
)
```

---

#### **Index Performance Impact Summary:**

| Operation                | Without Index | With Index         | Improvement |
| ------------------------ | ------------- | ------------------ | ----------- |
| Lookup by PK (user_id)   | O(n) ~100ms   | O(log n) ~1ms      | 100x        |
| Lookup by UK (email)     | O(n) ~100ms   | O(log n) ~1ms      | 100x        |
| Range query (date range) | O(n) ~500ms   | O(log n + k) ~10ms | 50x         |
| Foreign key join         | O(n²) ~5000ms | O(n log n) ~100ms  | 50x         |

---

**Query Optimization Examples:**

```java
// ❌ SLOW - Full table scan
String sql = "SELECT * FROM transactions";
// Cost: 5000ms with 1M records

// ✅ FAST - Uses index
String sql = "SELECT * FROM transactions WHERE source_account_id = 123";
// Cost: 10ms with index idx_tx_account_date

// ✅ FAST - Uses composite index with date
String sql = "SELECT * FROM transactions WHERE source_account_id = 123 AND created_at > '2024-01-01'";
// Cost: 5ms (index covers both conditions)
```

---

#### **When to Create Indexes:**

✅ **Create Index When:**

- Column is in WHERE clause frequently
- Column is used in JOINs
- Column is used in ORDER BY
- Range queries on column

❌ **Don't Create Index For:**

- Rarely used columns
- Small tables (< 1000 rows)
- Columns with low selectivity (many duplicates)
- Columns that are frequently updated (index maintenance cost)

---

## 🎨 Design Patterns & Architecture Questions

### Q10: What design patterns have you used in your project?

**Answer:**

#### **1. DAO Pattern (Data Access Object)**

**Problem:** Database operations are tightly coupled with business logic

**Solution:** Abstract database operations behind an interface

```java
// Interface defines contract
public interface UserDAO {
    User getUserByEmail(String email) throws DatabaseException;
    long createUser(String fullName, String email, String phoneNumber,
                    String password, String role) throws DatabaseException;
}

// Implementation hides SQL details
public class UserDAOImpl implements UserDAO {
    @Override
    public User getUserByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        // SQL Server implementation
    }
}

// Business logic doesn't know about database
public class AuthenticationService {
    private UserDAO userDAO;  // Depends on interface, not implementation

    public User login(String email, String password) {
        User user = userDAO.getUserByEmail(email);  // Database hidden
        // Business logic only
    }
}
```

**Benefits:**

- Swap database: PostgreSQL → Oracle (only change DAOImpl)
- Easy to mock for testing
- Database code centralized
- Business logic clean and readable

---

#### **2. Service Pattern (Business Logic Layer)**

**Problem:** Business logic mixed with UI code, hard to test and reuse

**Solution:** Encapsulate business logic in services

```java
public class AccountService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private AuditLogDAO auditLogDAO;

    // Business logic only
    public void transferFunds(long fromAccId, long toAccId, double amount)
            throws ValidationException, DatabaseException {

        // Validation (business rule)
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive");
        }

        // Load data
        Account fromAcc = accountDAO.getAccountById(fromAccId);
        Account toAcc = accountDAO.getAccountById(toAccId);

        // Apply business logic
        if (fromAcc.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds");
        }

        // Update data
        accountDAO.updateBalance(fromAccId, fromAcc.getBalance() - amount);
        accountDAO.updateBalance(toAccId, toAcc.getBalance() + amount);

        // Create transaction record
        transactionDAO.createTransaction(fromAccId, toAccId,
                                        "TRANSFER", amount, "SUCCESS");

        // Audit log
        auditLogDAO.createAuditLog(...);
    }
}

// UI just calls service
public class CustomerModule {
    private AccountService accountService;

    public void doTransfer() {
        double amount = InputUtil.readDouble("Enter amount: ");
        long toAccId = InputUtil.readLong("Enter destination account: ");

        try {
            accountService.transferFunds(customerAccountId, toAccId, amount);
            InputUtil.printSuccess("Transfer successful");
        } catch (ValidationException e) {
            InputUtil.printError("Invalid input: " + e.getMessage());
        }
    }
}
```

**Benefits:**

- Business logic independent of UI
- Easy to test service in isolation
- Reusable across multiple UIs (web, mobile, console)
- Changes to database don't affect business logic

---

#### **3. Singleton Pattern (User Session)**

**Problem:** Multiple instances of UserSession needed, but only one per user at a time

**Solution:** Single instance available application-wide

```java
public class UserSession {
    private static UserSession instance;
    private User currentUser;

    // Private constructor - prevents instantiation
    private UserSession() {}

    // Get the single instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

// Usage
UserSession session = UserSession.getInstance();
session.setCurrentUser(loggedInUser);

// Later, anywhere in code
User user = UserSession.getInstance().getCurrentUser();
String role = user.getRole();
```

**Benefits:**

- One instance only
- Global access without passing around
- Represents current session state
- Easy to clear on logout

---

#### **4. Factory Pattern (Module Creation)**

**Problem:** Need to create different modules based on user role

**Solution:** Factory creates appropriate module

```java
public class ModuleFactory {
    public static void launchModule(String role) {
        switch(role) {
            case "CUSTOMER" -> new CustomerModule().start();
            case "TELLER" -> new TellerModule().start();
            case "MANAGER" -> new ManagerModule().start();
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}

// Usage
User user = authService.login(email, password);
ModuleFactory.launchModule(user.getRole());  // Creates right module
```

**Benefits:**

- Centralized module creation
- Easy to add new roles
- Main code doesn't change when adding roles
- Open/Closed Principle

---

#### **5. Exception Hierarchy Pattern**

**Problem:** Different errors need different handling

**Solution:** Exception hierarchy allows specific catches

```java
public abstract class BankException extends Exception {
    public BankException(String message) { super(message); }
    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class DatabaseException extends BankException { }
public class AuthenticationException extends BankException { }
public class InsufficientBalanceException extends BankException { }
public class ValidationException extends BankException { }

// Usage - specific error handling
try {
    accountService.withdraw(accountId, amount);
} catch (InsufficientBalanceException e) {
    // Handle insufficient funds specifically
    InputUtil.printError("Insufficient balance: " + e.getMessage());
    // Maybe offer loan options
} catch (ValidationException e) {
    // Handle validation errors
    InputUtil.printError("Invalid input: " + e.getMessage());
} catch (DatabaseException e) {
    // Handle database errors
    InputUtil.printError("Database error. Please try again later.");
    logger.error("Database connection failed", e);
} catch (BankException e) {
    // Catch-all for any bank exception
    InputUtil.printError("Operation failed: " + e.getMessage());
}
```

**Benefits:**

- Specific error handling for different scenarios
- Cleaner code than if-else chains
- Error hierarchy shows relationships
- Easy to add new exception types

---

### Q11: Explain the layered architecture of your application.

**Answer:**

```
┌───────────────────────────────────────────────────────┐
│                    UI LAYER                           │
│     (Console Interface - Presentation)                │
│  ┌──────────────┬──────────────┬──────────────┐      │
│  │  Customer    │    Teller    │   Manager    │      │
│  │  Module      │   Module     │   Module     │      │
│  └──────────────┴──────────────┴──────────────┘      │
└─────────┬────────────────────────────────────────────┘
          │
          │ Uses
          ▼
┌───────────────────────────────────────────────────────┐
│                SERVICE LAYER                          │
│     (Business Logic - Core Application Logic)         │
│  ┌──────────────┬──────────────┬──────────────┐      │
│  │Authentication│   Account    │    Loan      │      │
│  │   Service    │   Service    │   Service    │      │
│  └──────────────┴──────────────┴──────────────┘      │
│  • Validation                                         │
│  • Business Rules                                     │
│  • Orchestration                                      │
│  • Exception Handling                                 │
└─────────┬────────────────────────────────────────────┘
          │
          │ Uses
          ▼
┌───────────────────────────────────────────────────────┐
│                DAO LAYER                              │
│     (Data Access - Database Operations)               │
│  ┌──────────┬─────────────┬──────────┬──────────┐   │
│  │  User    │   Account   │Transaction│  Loan   │   │
│  │  DAO     │   DAO       │   DAO    │   DAO   │   │
│  └──────────┴─────────────┴──────────┴──────────┘   │
│  • JDBC Connection                                    │
│  • PreparedStatements                                 │
│  • Result Mapping                                     │
│  • SQL Queries                                        │
└─────────┬────────────────────────────────────────────┘
          │
          │ Queries
          ▼
┌───────────────────────────────────────────────────────┐
│              DATABASE LAYER                           │
│     (Persistent Data Storage)                         │
│              SQL Server                               │
│     ashok_bank_db (9 Tables)                          │
│  • Tables (users, accounts, transactions, etc.)      │
│  • Constraints (PK, FK, UK, CHK)                     │
│  • Indexes (6 strategic indexes)                      │
│  • Transactions                                       │
└───────────────────────────────────────────────────────┘
```

#### **How Data Flows Through Layers:**

**Example: Customer Depositing Money**

```
UI LAYER:
  1. CustomerModule displays menu
  2. User selects "Deposit"
  3. InputUtil reads amount
  4. Calls: accountService.deposit(accountId, amount)
  ↓
SERVICE LAYER:
  5. AccountService.deposit() receives request
  6. Validates: amount > 0, account is ACTIVE, balance + amount won't overflow
  7. Calls: accountDAO.updateBalance(accountId, newBalance)
  8. Calls: transactionDAO.createTransaction(...)
  9. Calls: auditLogDAO.createAuditLog(...)
  10. Returns success message
  ↓
DAO LAYER:
  11. AccountDAOImpl.updateBalance() creates PreparedStatement
  12. Executes: UPDATE accounts SET balance = ? WHERE account_id = ?
  13. Result returned to service
  14. TransactionDAOImpl.createTransaction() creates PreparedStatement
  15. Executes: INSERT INTO transactions (...)
  ↓
DATABASE:
  16. SQL Server executes UPDATE statement
  17. SQL Server executes INSERT statement
  18. Database constraints validated
  19. Indexes updated
  ↓
DAO LAYER:
  20. Returns result set rows affected
  ↓
SERVICE LAYER:
  21. Checks if operations successful
  22. Throws exception if failed
  23. Returns success to UI
  ↓
UI LAYER:
  24. CustomerModule displays success message
  25. Shows new balance
  26. Returns to menu
```

#### **Benefits of Layered Architecture:**

| Benefit                    | How It Helps                                         |
| -------------------------- | ---------------------------------------------------- |
| **Separation of Concerns** | Each layer has one responsibility                    |
| **Maintainability**        | Change database? Only change DAO layer               |
| **Testability**            | Mock service dependencies for unit tests             |
| **Reusability**            | Services used by multiple UIs (console, web, mobile) |
| **Scalability**            | Replace JDBC with Hibernate, Spring, JPA later       |
| **Security**               | Validate at service layer before database            |
| **Performance**            | Optimize each layer independently                    |

---

## 🔌 JDBC & Database Connectivity Questions

### Q12: Explain how JDBC works in your project. What is a PreparedStatement and why is it important?

**Answer:**

#### **JDBC Flow:**

```
Java Code
    ↓
JDBC Driver
    ↓
SQL Server Network Protocol
    ↓
SQL Server Database Engine
    ↓
Database Tables
```

#### **Step-by-Step Connection:**

```java
// Step 1: Load JDBC Driver
Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

// Step 2: Establish Connection
Connection conn = DriverManager.getConnection(
    "jdbc:sqlserver://localhost:1433;databaseName=ashok_bank_db",
    "sa",           // Username
    "MsSql2026"     // Password
);

// Step 3: Create Statement & Execute Query
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, email);  // Parameter 1 = email

// Step 4: Get Results
ResultSet rs = stmt.executeQuery();
if (rs.next()) {
    User user = new User();
    user.setUserId(rs.getLong("user_id"));
    user.setEmail(rs.getString("email"));
    user.setPassword(rs.getString("password"));
}

// Step 5: Close Resources
rs.close();
stmt.close();
conn.close();
```

#### **What is PreparedStatement?**

**PreparedStatement = A pre-compiled SQL statement with placeholders**

```java
// ❌ BAD: String Concatenation (SQL Injection Vulnerable!)
String email = "ashok@email.com";
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
// What if email = "' OR '1'='1"?  SQL becomes:
// SELECT * FROM users WHERE email = '' OR '1'='1'
// Returns ALL users! SECURITY DISASTER!

// ✅ GOOD: PreparedStatement (Safe!)
String email = "ashok@email.com";
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, email);  // email treated as DATA, not SQL code
// Even if email = "' OR '1'='1'", it's just treated as a literal string value
```

#### **Why PreparedStatement is Critical:**

**1. Security - Prevents SQL Injection**

```java
// Attacker tries: email = "' OR '1'='1' --"

// ❌ String concatenation (VULNERABLE):
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
// Results in: SELECT * FROM users WHERE email = '' OR '1'='1' --'
// Returns ALL users! ❌

// ✅ PreparedStatement (SAFE):
String sql = "SELECT * FROM users WHERE email = ?";
stmt.setString(1, email);  // Treats email as literal value
// Queries: SELECT * FROM users WHERE email = '' OR '1'='1' --'
// Looks for exact match: '' OR '1'='1' --
// Returns NO users! ✅
```

**2. Performance - Query Caching**

```java
// ❌ String concatenation (SLOW):
for (String email : emailList) {
    String sql = "SELECT * FROM users WHERE email = '" + email + "'";
    // Database must PARSE each query
    // Different SQL = different execution plans
    // Query cache MISSES
    // 1000 queries = 1000 compiles = SLOW!
}

// ✅ PreparedStatement (FAST):
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
for (String email : emailList) {
    stmt.setString(1, email);
    // Database REUSES compiled query plan
    // Only parameters change
    // Query cache HITS
    // 1000 queries = 1 compile = FAST! ✅
}
```

**3. Type Safety - Parameter Validation**

```java
// PreparedStatement knows parameter types
stmt.setString(1, email);      // Parameter is STRING
stmt.setLong(2, userId);       // Parameter is LONG
stmt.setDouble(3, amount);     // Parameter is DOUBLE

// Database validates types before execution
// Type mismatch = Exception thrown before query runs
```

#### **PreparedStatement in Your Code:**

```java
// From UserDAOImpl.java
public User getUserByEmail(String email) throws DatabaseException {
    String sql = "SELECT user_id, full_name, email, phone_number, password, role, created_at FROM users WHERE email = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);  // ✅ Safe parameter binding

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    } catch (SQLException e) {
        throw new DatabaseException("Error fetching user by email.", e);
    }
    return null;
}

// From AccountDAOImpl.java
public void updateBalance(long accountId, double newBalance) throws DatabaseException {
    String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDouble(1, newBalance);   // ✅ DOUBLE parameter
        stmt.setLong(2, accountId);      // ✅ LONG parameter

        if (stmt.executeUpdate() == 0) {
            throw new DatabaseException("Balance update failed.");
        }
    } catch (SQLException e) {
        throw new DatabaseException("Error updating balance.", e);
    }
}
```

---

### Q13: How do you handle database transactions and ensure data consistency?

**Answer:**

**Transaction = Group of SQL operations that must all succeed or all fail (Atomicity)**

#### **Real-World Example: Fund Transfer**

```
Customer transfers ₹5000 from Account A to Account B

Without transaction:
1. Debit Account A by 5000
2. [SERVER CRASH]
3. Credit Account B by 5000 ← NEVER HAPPENS!
Result: Customer loses 5000 but Account B doesn't receive it! ❌

With transaction:
1. START TRANSACTION
2. Debit Account A by 5000
3. Credit Account B by 5000
4. COMMIT (all or nothing)
Result: Either BOTH succeed or BOTH fail! ✅
```

#### **Implementation in Your Code:**

```java
// From AccountService.java
public void transferFunds(long fromAccId, long toAccId, double amount)
        throws DatabaseException, ValidationException {

    // Validation
    if (amount <= 0) {
        throw new ValidationException("Amount must be positive.");
    }

    Account fromAcc = accountDAO.getAccountById(fromAccId);
    if (fromAcc.getBalance() < amount) {
        throw new InsufficientBalanceException("Insufficient funds.");
    }

    // DATABASE TRANSACTION START
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // ✅ Disable auto-commit

        // Step 1: Debit source account
        double newFromBalance = fromAcc.getBalance() - amount;
        accountDAO.updateBalance(fromAccId, newFromBalance);

        // Step 2: Credit destination account
        Account toAcc = accountDAO.getAccountById(toAccId);
        double newToBalance = toAcc.getBalance() + amount;
        accountDAO.updateBalance(toAccId, newToBalance);

        // Step 3: Create transaction record
        long transactionId = transactionDAO.createTransaction(
            fromAccId, toAccId, "TRANSFER", amount, "SUCCESS"
        );

        // Step 4: Create audit log
        auditLogDAO.createAuditLog(
            transactionId, fromAccId, "TRANSFER",
            fromAcc.getBalance(), newFromBalance
        );

        // ✅ All succeeded - COMMIT
        conn.commit();

    } catch (Exception e) {
        // ❌ Any error - ROLLBACK all changes
        if (conn != null) {
            try {
                conn.rollback();  // Undo all changes
            } catch (SQLException ex) {
                throw new DatabaseException("Rollback failed", ex);
            }
        }
        throw new DatabaseException("Transfer failed: " + e.getMessage(), e);
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);  // Reset to default
            } catch (SQLException e) {
                throw new DatabaseException("Error resetting auto-commit", e);
            }
        }
    }
}
```

#### **Transaction Guarantees (ACID Properties):**

```
A = Atomicity     : All or Nothing
C = Consistency   : Data always valid
I = Isolation     : Concurrent transactions don't interfere
D = Durability    : Committed data persists
```

**Example Guarantee:**

```
Transfer ₹5000, Server Crashes Scenarios:

Scenario 1 - Crash during debit:
  BEFORE: Account A = 10000, Account B = 20000
  [Debit A] CRASH
  AFTER: Account A = 10000, Account B = 20000
  ✅ No money lost (Atomicity)

Scenario 2 - Crash after credit:
  BEFORE: Account A = 10000, Account B = 20000
  [Debit A] [Credit B] CRASH
  AFTER: Account A = 5000, Account B = 25000
  ✅ Transfer succeeded (Durability)

Scenario 3 - Crash during commit:
  Database ensures all-or-nothing on restart
  ✅ Data consistency maintained (Consistency)
```

---

## 🔒 Security & Performance Questions

### Q14: How have you secured your application against common vulnerabilities?

**Answer:**

#### **1. SQL Injection Prevention (PreparedStatements)**

```java
// ❌ VULNERABLE
String email = getInput();  // e.g., "' OR '1'='1"
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
stmt.executeQuery(sql);  // Returns ALL users!

// ✅ SECURE
String email = getInput();
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, email);  // Parameters are escaped
stmt.executeQuery();  // Safe!
```

**All queries in your project use PreparedStatements:**

```java
stmt.setString(1, email);
stmt.setLong(2, userId);
stmt.setDouble(3, amount);
// Parameters NEVER concatenated into SQL strings
```

---

#### **2. Input Validation**

```java
// From AuthenticationService.java
public User login(String email, String password)
        throws AuthenticationException {

    // Validate email is not empty
    if (email == null || email.isBlank()) {
        throw new AuthenticationException("Email cannot be empty.");
    }

    // Validate password is not empty
    if (password == null || password.isBlank()) {
        throw new AuthenticationException("Password cannot be empty.");
    }

    // Lookup user
    User user = userDAO.getUserByEmail(email);
    if (user == null) {
        throw new AuthenticationException("Invalid email or password.");
    }

    // Validate password match
    if (!password.equals(user.getPassword())) {
        throw new AuthenticationException("Invalid email or password.");
    }

    return user;
}

// From InputUtil.java
public static long readLong(String prompt) {
    int maxRetries = 3;
    while (maxRetries > 0) {
        try {
            System.out.print(prompt);
            long value = scanner.nextLong();
            scanner.nextLine();  // Consume newline
            if (value <= 0) {
                System.out.println("❌ Please enter a positive number.");
                maxRetries--;
                continue;
            }
            return value;
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
            scanner.nextLine();
            maxRetries--;
        }
    }
    throw new ValidationException("Maximum input attempts exceeded.");
}

public static double readDouble(String prompt) {
    int maxRetries = 3;
    while (maxRetries > 0) {
        try {
            System.out.print(prompt);
            double value = scanner.nextDouble();
            scanner.nextLine();
            if (value < 0) {
                System.out.println("❌ Amount must be positive.");
                maxRetries--;
                continue;
            }
            return value;
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid amount format.");
            scanner.nextLine();
            maxRetries--;
        }
    }
    throw new ValidationException("Invalid input after 3 attempts.");
}
```

---

#### **3. Database-Level Constraints (Defense in Depth)**

```sql
-- Check constraints prevent invalid data
CREATE TABLE accounts (
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_positive_balance CHECK (balance >= 0.00),  -- No negative!

    status NVARCHAR(20),
    CONSTRAINT chk_acc_status CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    ...
)

CREATE TABLE users (
    role NVARCHAR(20),
    CONSTRAINT chk_user_role CHECK (role IN ('CUSTOMER', 'TELLER', 'MANAGER', 'ADMIN')),
    ...
)

-- NOT NULL prevents incomplete data
CREATE TABLE users (
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    ...
)

-- Foreign Key with CASCADE ensures referential integrity
CREATE TABLE accounts (
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
    -- If user deleted, all their accounts automatically deleted
    -- No orphaned account records possible!
)
```

---

#### **4. Role-Based Access Control (RBAC)**

```java
// Only customers can see their own accounts
public class CustomerModule {
    private long customerId;

    public void viewAccounts() {
        // ✅ SECURE: Show only this customer's accounts
        List<Account> accounts = accountService.getAccountsByUserId(customerId);
        // Cannot access other customer's accounts
    }
}

// Tellers can search customers
public class TellerModule {
    public void searchCustomer() {
        String phoneNumber = InputUtil.readPhoneNumber("Enter phone: ");
        User user = userDAO.getUserByPhone(phoneNumber);
        // ✅ Teller can search any customer
        // But can only perform allowed operations (deposit, withdrawal)
    }
}

// Managers have full access
public class ManagerModule {
    public void viewAllCustomers() {
        // ✅ Manager can view all customers
        // ✅ Manager can approve/reject loans
        // ✅ Manager can view audit logs
    }
}
```

---

#### **5. Exception Handling (No Info Leakage)**

```java
// ❌ BAD: Reveals sensitive info to user
catch (DatabaseException e) {
    System.out.println("Error: " + e.getMessage());
    // Could expose: SQL syntax errors, database structure, etc.
}

// ✅ GOOD: Generic message to user, detailed log internally
catch (DatabaseException e) {
    InputUtil.printError("Database operation failed. Please try again later.");
    logger.error("Database error details: ", e);  // Log for admins only
}

// ❌ BAD: Stack trace exposed
catch (SQLException e) {
    e.printStackTrace();  // Shows full database schema, connection details
}

// ✅ GOOD: Log stack trace, show generic message
catch (SQLException e) {
    logger.error("Database connection error", e);
    InputUtil.printError("System error. Please try again later.");
}
```

---

#### **6. Password Security**

```java
// Current implementation: Plaintext storage (for demo)
// In production, use:

public static String hashPassword(String password) {
    // Use bcrypt or Argon2
    return BCrypt.hashpw(password, BCrypt.gensalt());
}

public static boolean verifyPassword(String password, String hash) {
    return BCrypt.checkpw(password, hash);
}

// Why not simple SHA-256?
// ❌ Rainbow tables can reverse it
// ❌ Same password = same hash (no salt needed)
// ✅ bcrypt: Built-in salt, slow (prevents brute force), upgradeable
```

---

### Q15: How do indexes and constraints improve performance?

**Answer:**

**Performance Improvement = Fast Query Execution + No Invalid Data**

#### **1. Indexes Speed Up Lookups**

```
Index = Sorted data structure (B-Tree) for fast searching

Without Index (Full Table Scan):
- Check row 1: Is this the one?
- Check row 2: Is this the one?
- ...
- Check row 100,000: Is this the one?
- Result: Found! (Average 50,000 comparisons) ~100ms

With Index (B-Tree Search):
- Binary search: Is it in upper half?
- Binary search: Is it in upper quarter?
- ... (log queries)
- Result: Found! (Average 17 comparisons) ~1ms
- 100x FASTER!
```

**Your Project's Indexes:**

```sql
CREATE INDEX idx_accounts_acc_num ON accounts(account_number);
-- Fast: SELECT * FROM accounts WHERE account_number = 'ACC1234567890001'

CREATE INDEX idx_accounts_user ON accounts(user_id);
-- Fast: SELECT * FROM accounts WHERE user_id = 123

CREATE INDEX idx_tx_account_date ON transactions(source_account_id, created_at);
-- Fast: SELECT * FROM transactions
--       WHERE source_account_id = 1 AND created_at > '2024-01-01'
```

---

#### **2. Constraints Prevent Invalid Data (No Cleanup Needed)**

```java
// Without constraints: Must validate everywhere
public void transferFunds(long fromId, long toId, double amount) {
    // Validation logic (20+ checks)
    if (amount <= 0) throw new ValidationException("Amount must be positive");
    if (amount > 999999999) throw new ValidationException("Amount too large");
    if (fromId == toId) throw new ValidationException("Same account");
    if (!accountExists(fromId)) throw new ValidationException("From account missing");
    if (!accountExists(toId)) throw new ValidationException("To account missing");
    // ... more checks
}

// With constraints: Database enforces rules
public void transferFunds(long fromId, long toId, double amount)
        throws DatabaseException {
    // Only application-level validation
    if (amount <= 0) throw new ValidationException("Amount must be positive");

    // Database constraints handle:
    // - Both accounts exist (FK constraint)
    // - Amount > 0 (CHK constraint)
    // - Balance >= 0 (CHK constraint)
    // - Valid statuses (CHK constraint)

    accountDAO.updateBalance(fromId, fromAcc.getBalance() - amount);
    accountDAO.updateBalance(toId, toAcc.getBalance() + amount);
    // DatabaseException thrown if constraints violated
}
```

---

#### **3. Real Performance Numbers (Your Application Scale)**

```
Scenario: 100,000 accounts, 1,000,000 transactions

Query: Find all transactions for account 123 in last 30 days

WITHOUT INDEX:
- Scans 1,000,000 rows sequentially
- Filters by account
- Filters by date
- Execution time: ~800ms (User sees delay)

WITH COMPOSITE INDEX idx_tx_account_date(source_account_id, created_at):
- B-Tree navigates to account 123 subtree
- Further navigates to date range
- Returns results directly
- Execution time: ~5ms (Instantaneous)
- 160x FASTER! ✅
```

---

#### **4. Index Maintenance Cost vs. Benefit**

```
Index Benefit (Read Operations):
- SELECT: 100x faster ✅

Index Cost (Write Operations):
- INSERT: Must update 6 indexes (~5ms instead of 1ms)
- UPDATE: Must update affected indexes
- DELETE: Must update indexes

For Bank System:
- Read-Heavy (Customers check balance, see transactions frequently)
- Write-Moderate (Deposits, withdrawals less frequent than reads)
- INDEXES WORTH IT! ✅

Rule of Thumb:
- If 80% SELECT, 20% INSERT/UPDATE/DELETE → Use indexes
- If 50% INSERT, 50% SELECT → Careful with indexes
- If mostly INSERT only → Minimal indexes
```

---

## 🎯 Advanced Scenario-Based Questions

### Q16: Walk me through a complete transaction scenario. What happens when a customer makes a transfer?

**Answer:**

**Scenario: Customer Ashok transfers ₹5,000 to Priya**

#### **Step-by-Step Execution:**

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 1: AUTHENTICATION                                      │
└─────────────────────────────────────────────────────────────┘

User Input:
  Email: ashok.kumar@email.com
  Password: Test@123

Code Flow:
  1. LoginUI.login() receives input
  2. Calls: authService.login(email, password)

  AuthenticationService.login():
    ├─ Validates: email.isNotBlank()
    ├─ Validates: password.isNotBlank()
    ├─ Calls: userDAO.getUserByEmail(email)
    │   └─ SQL: SELECT * FROM users WHERE email = ?
    │   └─ Returns: User(id=1, name="Ashok Kumar", role="CUSTOMER", ...)
    ├─ Checks: User exists? YES ✓
    ├─ Checks: password.equals(user.getPassword())? YES ✓
    └─ Returns: User object (authenticated)

  3. LoginUI.displayRoleMenu(user)
  4. Creates: new CustomerModule(user)
  5. Calls: customerModule.start()
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 2: CUSTOMER SELECTS TRANSFER                           │
└─────────────────────────────────────────────────────────────┘

Display Menu:
  1. Dashboard
  2. Check Balance
  3. Mini Statement
  4. Deposit
  5. Withdrawal
  6. Fund Transfer        ← Selected
  7. Apply for Loan
  8. Change Password
  9. Logout

Code Flow:
  1. CustomerModule.start() shows menu
  2. User selects: 6 (Fund Transfer)
  3. Calls: CustomerModule.fundTransfer()

  CustomerModule.fundTransfer():
    ├─ Calls: userDAO.getAccountsByUserId(customerId)
    │   └─ SQL: SELECT * FROM accounts WHERE user_id = ?
    │   └─ Returns: [Account(id=1, number="ACC1...001", type="SAVINGS", balance=50000),
    │               Account(id=2, number="ACC1...002", type="CHECKING", balance=100000)]
    ├─ Displays: "Your Accounts:"
    │   Account 1: ACC1234567890001 (SAVINGS) - Balance: ₹50,000
    │   Account 2: ACC1234567890002 (CHECKING) - Balance: ₹100,000
    ├─ Reads: sourceAccountId = 2 (Checking account)
    ├─ Reads: destinationAccountNumber = "ACC1234567890003"
    ├─ Reads: amount = 5000
    └─ Calls: accountService.transferFunds(sourceId=2, destAccNum="ACC1..003", 5000)
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 3: VALIDATION (Service Layer)                          │
└─────────────────────────────────────────────────────────────┘

Code Flow:
  AccountService.transferFunds(sourceId=2, destAccNumber, 5000):

    ✓ Validation 1: Amount > 0?
      5000 > 0? YES ✓
      if (amount <= 0) throw ValidationException

    ✓ Validation 2: Source account exists?
      Calls: accountDAO.getAccountById(2)
      SQL: SELECT * FROM accounts WHERE account_id = ?
      Returns: Account(id=2, balance=100000, status="ACTIVE")
      Account found? YES ✓

    ✓ Validation 3: Source account is ACTIVE?
      status.equals("ACTIVE")? YES ✓
      if (!status.equals("ACTIVE")) throw ValidationException

    ✓ Validation 4: Sufficient balance?
      sourceAccount.balance (100000) >= amount (5000)? YES ✓
      if (balance < amount) throw InsufficientBalanceException

    ✓ Validation 5: Destination account exists?
      Calls: accountDAO.getAccountByNumber("ACC1..003")
      SQL: SELECT * FROM accounts WHERE account_number = ?
      Uses: idx_accounts_acc_num (FAST!)
      Returns: Account(id=3, userId=2, balance=75000, status="ACTIVE")
      Account found? YES ✓

    ✓ Validation 6: Destination account is ACTIVE?
      status.equals("ACTIVE")? YES ✓

    All validations passed! Proceed to transaction.
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 4: DATABASE TRANSACTION (Atomicity)                    │
└─────────────────────────────────────────────────────────────┘

Code Flow:
  Transaction transaction = conn.startTransaction()

  try:
    ├─ Step 4A: DEBIT SOURCE ACCOUNT
    │   From Account 2: 100000 → 95000 (-5000)
    │   Calls: accountDAO.updateBalance(2, 95000)
    │   SQL: UPDATE accounts SET balance = 95000 WHERE account_id = 2
    │   ✓ Executes successfully
    │
    ├─ Step 4B: CREDIT DESTINATION ACCOUNT
    │   To Account 3: 75000 → 80000 (+5000)
    │   Calls: accountDAO.updateBalance(3, 80000)
    │   SQL: UPDATE accounts SET balance = 80000 WHERE account_id = 3
    │   ✓ Executes successfully
    │
    ├─ Step 4C: CREATE TRANSACTION RECORD
    │   Calls: transactionDAO.createTransaction(2, 3, "TRANSFER", 5000, "SUCCESS")
    │   SQL: INSERT INTO transactions
    │        (source_account_id, destination_account_id, transaction_type,
    │         amount, status, created_at)
    │        VALUES (2, 3, 'TRANSFER', 5000, 'SUCCESS', GETDATE())
    │   Returns: transaction_id = 1001
    │   ✓ Executes successfully
    │
    ├─ Step 4D: CREATE AUDIT LOG (Account 2)
    │   Calls: auditLogDAO.createAuditLog(1001, 2, "TRANSFER", 100000, 95000)
    │   SQL: INSERT INTO audit_logs
    │        (transaction_id, account_id, action_type, old_balance, new_balance, logged_at)
    │        VALUES (1001, 2, 'TRANSFER', 100000, 95000, GETDATE())
    │   ✓ Executes successfully
    │
    ├─ Step 4E: CREATE AUDIT LOG (Account 3)
    │   Calls: auditLogDAO.createAuditLog(1001, 3, "TRANSFER_CREDIT", 75000, 80000)
    │   ✓ Executes successfully
    │
    └─ COMMIT TRANSACTION ✓
       All changes permanently saved to database!

  catch (Exception e):
    └─ ROLLBACK TRANSACTION
       All changes undone! Returns to previous state.
       Accounts: 2=100000, 3=75000 (as if nothing happened)
       throw DatabaseException
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 5: RECEIPT GENERATION (UI Layer)                       │
└─────────────────────────────────────────────────────────────┘

Code Flow:
  ReceiptUtil.generateTransferReceipt(sourceAcc, destAcc, 5000, txnId=1001)

  Display:
  ╔═════════════════════════════════════════════════════════════╗
  ║           ASHOK BANK OF INDIA - TRANSFER RECEIPT            ║
  ║═════════════════════════════════════════════════════════════║
  ║ Date/Time: 2026-07-27 17:45:30                              ║
  ║ Transaction ID: 1001                                        ║
  ║─────────────────────────────────────────────────────────────║
  ║ FROM: Ashok Kumar                                           ║
  ║ Account: ACC1234567890002 (CHECKING)                        ║
  ║ ─────────────────────────────────────────────────────────────║
  ║ TO: Priya Singh                                             ║
  ║ Account: ACC1234567890003 (SAVINGS)                         ║
  ║ ─────────────────────────────────────────────────────────────║
  ║ Amount: ₹5,000.00                                           ║
  ║ Status: ✓ SUCCESS                                           ║
  ║ ─────────────────────────────────────────────────────────────║
  ║ FROM Account Balance: ₹95,000.00 (Previous: ₹100,000.00)   ║
  ║ TO Account Balance: ₹80,000.00 (Previous: ₹75,000.00)      ║
  ║ ═════════════════════════════════════════════════════════════║
  ║ ✓ Transfer Completed Successfully                           ║
  ╚═════════════════════════════════════════════════════════════╝

  Print Receipt: ✓
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 6: DATABASE STATE (Verification)                       │
└─────────────────────────────────────────────────────────────┘

Database Queries After Transaction:

SELECT * FROM accounts WHERE account_id IN (2, 3);
╔════════════════════════════════════════════════════════════╗
║ account_id │ balance   │ status │ updated_at              ║
╠════════════════════════════════════════════════════════════╣
║ 2          │ 95000.00  │ ACTIVE │ 2026-07-27 17:45:30    ║
║ 3          │ 80000.00  │ ACTIVE │ 2026-07-27 17:45:30    ║
╚════════════════════════════════════════════════════════════╝

SELECT * FROM transactions WHERE transaction_id = 1001;
╔════════════════════════════════════════════════════════════╗
║ transaction_id │ source │ destination │ amount │ status    ║
╠════════════════════════════════════════════════════════════╣
║ 1001           │ 2      │ 3           │ 5000   │ SUCCESS   ║
╚════════════════════════════════════════════════════════════╝

SELECT * FROM audit_logs WHERE transaction_id = 1001;
╔════════════════════════════════════════════════════════════╗
║ log_id │ transaction_id │ account_id │ old_balance │ new_balance ║
╠════════════════════════════════════════════════════════════╣
║ 5001   │ 1001           │ 2          │ 100000      │ 95000       ║
║ 5002   │ 1001           │ 3          │ 75000       │ 80000       ║
╚════════════════════════════════════════════════════════════╝
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 7: RETURN TO MENU                                      │
└─────────────────────────────────────────────────────────────┘

Display:
  ✓ Transfer successful!

  Continue with menu:
  1. Dashboard
  2. Check Balance
  3. ...
```

---

### Q17: What happens when a manager approves a loan?

**Answer:**

**Scenario: Manager approves ₹10,00,000 HOME loan for Ashok**

#### **Complete Workflow:**

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 1: MANAGER LOGIN & NAVIGATE TO LOAN APPROVALS          │
└─────────────────────────────────────────────────────────────┘

Manager (John) logs in:
  Email: manager@ashokbank.com
  Password: Test@123

ManagerModule displays pending loans:
  Calls: loanService.getAppliedLoans()
  SQL: SELECT * FROM loans WHERE status = 'APPLIED'

  Display:
  Pending Loans:
  1. Loan ID: 101, Customer: Ashok Kumar, Type: HOME,
     Amount: ₹10,00,000, Interest: 7.5%, Term: 10 years
  2. Loan ID: 102, Customer: Rajesh Patel, Type: AUTO,
     Amount: ₹5,00,000, Interest: 8.5%, Term: 5 years

  Manager selects: Loan 101
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 2: APPROVAL DECISION                                   │
└─────────────────────────────────────────────────────────────┘

ManagerModule.approveLoan():
  1. Displays: Loan Details
     ├─ Applicant: Ashok Kumar
     ├─ Loan Type: HOME
     ├─ Amount: ₹10,00,000
     ├─ Interest Rate: 7.5%
     ├─ Term: 120 months
     └─ Status: APPLIED

  2. Manager Action:
     Approve? (Y/N): Y

  3. Calls: loanService.approveLoan(loanId=101, accountId=2)
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 3: DATABASE TRANSACTION (Multi-Step Approval)          │
└─────────────────────────────────────────────────────────────┘

LoanService.approveLoan():

  try:
    ├─ Step 3A: LOAD LOAN DETAILS
    │   Calls: loanDAO.getLoanById(101)
    │   Returns: Loan(id=101, status="APPLIED", principal=10,00,000)
    │   ✓ Loan found
    │
    ├─ Step 3B: VALIDATE LOAN STATUS
    │   Checks: status.equals("APPLIED")?
    │   YES ✓ (Only APPLIED loans can be approved)
    │
    ├─ Step 3C: LOAD CUSTOMER ACCOUNT
    │   Calls: accountDAO.getAccountById(2)
    │   Returns: Account(id=2, balance=95000, status="ACTIVE")
    │   ✓ Account found and ACTIVE
    │
    ├─ Step 3D: START TRANSACTION
    │   conn.setAutoCommit(false)
    │
    ├─ Step 3E: UPDATE LOAN STATUS → APPROVED
    │   Calls: loanDAO.updateLoanStatus(101, "APPROVED")
    │   SQL: UPDATE loans SET status = 'APPROVED' WHERE loan_id = 101
    │   ✓ Loan marked APPROVED
    │
    ├─ Step 3F: CREATE INTEREST_CREDIT TRANSACTION
    │   Calls: transactionDAO.createTransaction(
    │           sourceAccountId=null,
    │           destinationAccountId=2,
    │           type="INTEREST_CREDIT",
    │           amount=10,00,000,
    │           status="SUCCESS")
    │   SQL: INSERT INTO transactions (destination_account_id, transaction_type,
    │                                   amount, status, created_at)
    │        VALUES (2, 'INTEREST_CREDIT', 10,00,000, 'SUCCESS', GETDATE())
    │   Returns: transaction_id = 1002
    │   ✓ Transaction record created
    │
    ├─ Step 3G: CREDIT ACCOUNT WITH LOAN AMOUNT
    │   Calls: accountDAO.updateBalance(accountId=2, newBalance=11,00,000)
    │   Calculation: 95,000 + 10,00,000 = 11,00,000
    │   SQL: UPDATE accounts SET balance = 1100000 WHERE account_id = 2
    │   ✓ Account credited
    │
    ├─ Step 3H: CREATE AUDIT LOG
    │   Calls: auditLogDAO.createAuditLog(
    │           transactionId=1002,
    │           accountId=2,
    │           action="LOAN_APPROVAL",
    │           oldBalance=95000,
    │           newBalance=1100000)
    │   SQL: INSERT INTO audit_logs (transaction_id, account_id, action_type,
    │                                 old_balance, new_balance, logged_at)
    │        VALUES (1002, 2, 'LOAN_APPROVAL', 95000, 1100000, GETDATE())
    │   ✓ Audit trail created
    │
    ├─ Step 3I: UPDATE LOAN STATUS → ACTIVE
    │   Calls: loanDAO.updateLoanStatus(101, "ACTIVE")
    │   SQL: UPDATE loans SET status = 'ACTIVE' WHERE loan_id = 101
    │   ✓ Loan now ACTIVE
    │
    └─ COMMIT TRANSACTION ✓
       All changes permanently saved!

  catch (Exception e):
    └─ ROLLBACK TRANSACTION
       Loan status remains APPLIED
       Account balance remains 95000
       No transaction or audit records created
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 4: DATABASE VERIFICATION                               │
└─────────────────────────────────────────────────────────────┘

Check Loan Table:
SELECT * FROM loans WHERE loan_id = 101;
╔═══════════════════════════════════════════════════════════╗
║ loan_id │ user_id │ type │ principal │ status │ created_at ║
╠═══════════════════════════════════════════════════════════╣
║ 101     │ 1       │ HOME │ 1000000   │ ACTIVE │ 2026-07-27 ║
╚═══════════════════════════════════════════════════════════╝

Check Account Balance:
SELECT account_id, balance FROM accounts WHERE account_id = 2;
╔═══════════════════════════════════════╗
║ account_id │ balance                 ║
╠═══════════════════════════════════════╣
║ 2          │ 1100000.00 (was 95000)  ║
╚═══════════════════════════════════════╝

Check Transactions:
SELECT * FROM transactions WHERE transaction_id = 1002;
╔════════════════════════════════════════════════════════════╗
║ transaction_id │ destination_account_id │ amount │ status ║
╠════════════════════════════════════════════════════════════╣
║ 1002           │ 2                      │ 1000000│ SUCCESS║
╚════════════════════════════════════════════════════════════╝
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 5: MANAGER SEES CONFIRMATION                           │
└─────────────────────────────────────────────────────────────┘

Display:
  ✓ Loan Approved Successfully!

  Loan Details:
  ├─ Loan ID: 101
  ├─ Customer: Ashok Kumar
  ├─ Amount: ₹10,00,000
  ├─ Status: ACTIVE
  ├─ Credit To Account: ACC1234567890002
  ├─ New Balance: ₹11,00,000
  └─ Transaction ID: 1002

  ✓ Principal amount credited to customer account
```

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 6: CUSTOMER SEES UPDATED BALANCE                       │
└─────────────────────────────────────────────────────────────┘

Next time Ashok logs in and checks balance:

CustomerModule.displayDashboard():
  Calls: accountService.getAccountsByUserId(1)

  Display:
  Your Accounts:
  1. ACC1234567890001 (SAVINGS)
     Balance: ₹50,000.00

  2. ACC1234567890002 (CHECKING)
     Balance: ₹11,00,000.00  ← UPDATED! (was 95,000)

  View Transaction History:
  Transaction 1002: INTEREST_CREDIT +₹10,00,000 (2026-07-27 17:45:45)
```

---

## Summary: Key Takeaways

**Your project demonstrates:**

✅ **Proper OOP** - Encapsulation, Abstraction, Inheritance, Polymorphism  
✅ **Database Design** - 9 normalized tables with constraints  
✅ **Security** - PreparedStatements, input validation, RBAC  
✅ **Performance** - Strategic indexes, composite indexes  
✅ **Transactions** - ACID compliance, atomicity guarantee  
✅ **Architecture** - 3-layer design with clear separation  
✅ **Error Handling** - Exception hierarchy, meaningful messages  
✅ **Audit Trail** - Complete compliance logging

---

_This interview guide covers the complete technical depth of your Bank Management System project. Good luck with your interviews!_ 🎓✨
