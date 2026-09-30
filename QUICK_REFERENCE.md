# Ashok Bank Management System - Quick Reference

## 🚀 Quick Start (3 Steps)

### Step 1: Setup Database (5 minutes)

```sql
-- Open SQL Server Management Studio
-- Execute schema.sql file
sqlcmd -S localhost -U sa -P Mssql206 -i schema.sql
```

### Step 2: Update DB Password

Edit: `src/main/java/com/ashok/bank/constants/AppConstants.java`

```java
public static final String DB_PASSWORD = "Mssql206";
```

### Step 3: Build & Run (1 minute)

```bash
# Windows
build-and-run.bat

# Linux/Mac
./build-and-run.sh

# Manual
mvn clean compile exec:java -Dexec.mainClass="com.ashok.bank.Main"
```

---

## 🔐 Test Login Credentials

| Role     | Email                 | Password |
| -------- | --------------------- | -------- |
| Customer | ashok.kumar@email.com | Test@123 |
| Teller   | teller@ashokbank.com  | Test@123 |
| Manager  | manager@ashokbank.com | Test@123 |

---

## 📂 Important Files

| File                      | Purpose                                |
| ------------------------- | -------------------------------------- |
| `pom.xml`                 | Maven dependencies & build config      |
| `schema.sql`              | Database schema & sample data          |
| `AppConstants.java`       | All configuration constants            |
| `Main.java`               | Application entry point                |
| `LoginUI.java`            | Login screen                           |
| `CustomerModule.java`     | Customer operations                    |
| `TellerModule.java`       | Teller operations                      |
| `ManagerModule.java`      | Manager operations                     |
| `TransactionService.java` | Transaction logic with commit/rollback |
| `README.md`               | Full documentation                     |

---

## ⚙️ Database Configuration

**File:** `src/main/java/com/ashok/bank/constants/AppConstants.java`

```java
// Database connection settings
public static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=ashok_bank_db;encrypt=true;trustServerCertificate=true";
public static final String DB_USER = "sa";
public static final String DB_PASSWORD = "Mssql206";  // ← Update this
```

---

## 🔧 Maven Commands

```bash
# Clean build
mvn clean

# Compile
mvn compile

# Package as JAR
mvn package

# Run application
mvn exec:java -Dexec.mainClass="com.ashok.bank.Main"

# Full build and run
mvn clean compile package exec:java -Dexec.mainClass="com.ashok.bank.Main"

# Skip tests and build faster
mvn clean compile exec:java -DskipTests -Dexec.mainClass="com.ashok.bank.Main"
```

---

## 🐛 Troubleshooting

### Database Connection Error

```
Error: "Failed to establish database connection"
```

**Solution:**

1. Verify SQL Server is running: `netstat -an | findstr :1433`
2. Check credentials in `AppConstants.java`
3. Test connection: `sqlcmd -S localhost -U sa -P Mssql206`

### Maven Compilation Error

```
[ERROR] 'dependencies.dependency.version' for ... is missing
```

**Solution:**

```bash
mvn clean dependency:resolve
mvn clean compile
```

### "Class not found" Error

```
Error: ClassNotFoundException: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**Solution:**

- Wait for Maven to download dependencies (first run is slow)
- Delete `.m2/repository/com/microsoft` and rebuild
- Ensure SQL Server JDBC driver is in `pom.xml`

### Application Hangs at Startup

```
Possible issue: Database connection timeout
```

**Solution:**

1. Check if SQL Server is accepting connections
2. Verify firewall isn't blocking port 1433
3. Ensure `trustServerCertificate=true` in connection string

### "Invalid email or password" Error

```
Error: Even with correct credentials
```

**Solution:**

1. Verify user exists in database: `SELECT * FROM users WHERE email = 'ashok.kumar@email.com';`
2. Reset test data by re-running `schema.sql`
3. Ensure password hashing in code matches database

---

## 📊 Data Model Quick View

```
┌─────────────────────────────────────────────┐
│ users (Customers, Tellers, Managers)       │
│ - user_id (PK)                             │
│ - email (UNIQUE)                           │
│ - phone_number (UNIQUE)                    │
│ - password_hash (SHA-256 with salt)        │
│ - role (CUSTOMER, TELLER, MANAGER)        │
└─────────────────────────────────────────────┘
            ↓ (1:Many)
┌─────────────────────────────────────────────┐
│ accounts (Customer's bank accounts)        │
│ - account_id (PK)                          │
│ - account_number (UNIQUE)                  │
│ - balance (DECIMAL)                        │
│ - status (ACTIVE, FROZEN, CLOSED)          │
└─────────────────────────────────────────────┘
            ↓ (1:Many)
┌─────────────────────────────────────────────┐
│ transactions (Deposits, Withdrawals, etc)  │
│ - transaction_id (PK)                      │
│ - source_account_id (FK)                   │
│ - destination_account_id (FK)              │
│ - transaction_type (DEPOSIT, WITHDRAWAL)   │
│ - amount (DECIMAL)                         │
│ - status (PENDING, SUCCESS, FAILED)        │
└─────────────────────────────────────────────┘
            ↓ (1:Many)
┌─────────────────────────────────────────────┐
│ audit_logs (Transaction audit trail)       │
│ - log_id (PK)                              │
│ - transaction_id (FK)                      │
│ - old_balance, new_balance                 │
│ - action_type                              │
└─────────────────────────────────────────────┘
```

---

## 🎯 Key Operations & Flow

### Customer Transfer Money

```
1. Customer Login → Email + Password
2. Select "Transfer Money"
3. Enter Receiver Account Number
4. Confirm Receiver Name
5. Enter Amount
6. Confirm Transfer
7. TRANSACTION START:
   - Deduct from sender account
   - Add to receiver account
   - Create transaction record
   - Create 2 audit logs
   - COMMIT
8. Print Receipt
```

### Teller Deposit Cash

```
1. Teller Login
2. Select "Cash Deposit"
3. Enter Account Number
4. Enter Deposit Amount
5. TRANSACTION START:
   - Add to account balance
   - Create transaction record
   - Create audit log
   - COMMIT
6. Print Receipt with Customer Name & Masked Account
```

### Manager Approve Loan

```
1. Manager Login
2. Select "Approve Loan"
3. View all APPLIED loans
4. Select Loan ID
5. Confirm approval
6. TRANSACTION START:
   - Update loan status to APPROVED
   - Credit amount to customer's account
   - Create transaction record
   - Create audit log
   - Update loan status to ACTIVE
   - COMMIT
7. Print Loan Approval Receipt
```

---

## ✅ Validation Rules

| Operation       | Validation                                           |
| --------------- | ---------------------------------------------------- |
| Login           | Email exists, password correct                       |
| Deposit         | Amount > 0, Account ACTIVE                           |
| Withdrawal      | Amount > 0, Balance sufficient, Account ACTIVE       |
| Transfer        | Amount > 0, Both accounts ACTIVE, Different accounts |
| Loan Apply      | Amount > 0, Valid loan type, Months > 0              |
| Loan Approve    | Loan status = APPLIED                                |
| Password Change | Old password correct, New ≠ Old                      |
| Email           | Valid format (contains @)                            |
| Phone           | Exactly 10 digits                                    |

---

## 🔒 Security Highlights

| Feature                  | Implementation                     |
| ------------------------ | ---------------------------------- |
| SQL Injection Prevention | PreparedStatement only, no raw SQL |
| Password Security        | SHA-256 with random salt           |
| Access Control           | Role-based authorization check     |
| Data Integrity           | Transactions with rollback         |
| Audit Trail              | All balance changes logged         |
| Session Timeout          | 30 minutes auto-logout             |
| Account Status           | ACTIVE/FROZEN/CLOSED validation    |

---

## 📝 Project Statistics

| Metric              | Count |
| ------------------- | ----- |
| Total Classes       | 30+   |
| Total Methods       | 150+  |
| Database Tables     | 9     |
| DAO Operations      | 50+   |
| Validation Rules    | 20+   |
| Exception Types     | 5     |
| Code Lines          | 2000+ |
| Documentation Pages | 5     |

---

## 🎓 Interview Talking Points

**On JDBC:**

- "Used PreparedStatement for SQL injection prevention"
- "Implemented connection management with DatabaseConnection utility"
- "Handled ResultSet mapping to model objects"

**On Transactions:**

- "Implemented ACID properties for financial operations"
- "Used Connection.commit() and rollback() for atomicity"
- "Created audit logs for transaction traceability"

**On Database Design:**

- "Applied normalization principles"
- "Used foreign keys with CASCADE delete"
- "Created indexes for query performance"

**On Architecture:**

- "3-layer architecture: UI → Service → DAO → DB"
- "DAO pattern for data abstraction"
- "Service layer for business logic"

---

## 📞 Support

If you encounter issues:

1. Check the console error message
2. Verify database connection with `sqlcmd`
3. Review README.md for detailed troubleshooting
4. Check schema.sql for data issues
5. Review class documentation in code

---

**Happy Banking! 🏦**

For detailed information, refer to README.md and PROJECT_SUMMARY.md
