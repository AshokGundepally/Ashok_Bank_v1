# IDE Configuration

## VS Code Setup

### 1. Install Extensions

- Java Extension Pack (Microsoft)
- Maven for Java (Microsoft)
- SQL Server (mssql)
- Debugger for Java (Microsoft)

### 2. Import Project

- Open VS Code
- File → Open Folder → Select project folder
- Maven will auto-download dependencies

### 3. Build & Run

- Right-click on Main.java → Run
- Or use Terminal: `mvn clean compile exec:java -Dexec.mainClass="com.ashok.bank.Main"`

## IntelliJ IDEA Setup

### 1. Open Project

- File → Open → Select project folder
- IntelliJ will auto-detect Maven configuration

### 2. Configure JDK

- File → Project Structure
- Set Project SDK to Java 17 or higher
- Ensure Language level is set to 17

### 3. Build & Run

- Right-click Main.java → Run Main.main()
- Or use: Shift+F10

## Command Line (Maven)

### Basic Commands

```bash
# Clean previous builds
mvn clean

# Compile the project
mvn compile

# Run the application
mvn exec:java -Dexec.mainClass="com.ashok.bank.Main"

# Package as JAR
mvn package

# Run tests
mvn test

# Full build and run
mvn clean compile exec:java -Dexec.mainClass="com.ashok.bank.Main"
```

## Database Setup in SQL Server

### Using SQL Server Management Studio (SSMS)

1. **Connect to Server**
   - Server name: `localhost` (or your server name)
   - Authentication: SQL Server Authentication
   - Login: `sa`
   - Password: `Mssql206`

2. **Execute Schema**
   - New Query
   - Paste the SQL schema script
   - Execute (F5 or Ctrl+E)

3. **Verify Database**

   ```sql
   -- Check if database exists
   SELECT name FROM sys.databases WHERE name = 'ashok_bank_db';

   -- Check tables
   USE ashok_bank_db;
   SELECT name FROM sys.tables;
   ```

### Using PowerShell/Command Line

```powershell
# Test connection
sqlcmd -S localhost -U sa -P Mssql206

# Create database
sqlcmd -S localhost -U sa -P Mssql206 -i schema.sql

# Run query
sqlcmd -S localhost -U sa -P Mssql206 -Q "SELECT name FROM sys.databases;"
```

## Debugging

### Enable Debug Mode

1. Set breakpoints in IDE
2. Run with debugger (F5 in VS Code)
3. Inspect variables at breakpoints

### Common Debug Points

- `Main.java` - Application startup
- `LoginUI.java` - Authentication flow
- `CustomerModule.java` - Customer operations
- `TransactionService.java` - Transaction handling

### Console Output Analysis

- Green ✓ messages = Success
- Red ✗ messages = Errors
- Yellow ⚠ messages = Warnings
- Blue ℹ messages = Information

---

For detailed setup guide, see README.md
