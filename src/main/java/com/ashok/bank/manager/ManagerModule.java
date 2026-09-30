package com.ashok.bank.manager;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.AuditLogDAO;
import com.ashok.bank.dao.impl.AuditLogDAOImpl;
import com.ashok.bank.dao.impl.UserDAOImpl;
import com.ashok.bank.model.AuditLog;
import com.ashok.bank.model.Loan;
import com.ashok.bank.model.Account;
import com.ashok.bank.service.AccountService;
import com.ashok.bank.service.LoanService;
import com.ashok.bank.util.InputUtil;
import com.ashok.bank.util.ReceiptUtil;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.ValidationException;
import java.util.List;

public class ManagerModule {
    private final AccountService accountService;
    private final LoanService loanService;
    private final AuditLogDAO auditLogDAO;
    private final UserDAOImpl userDAO;

    public ManagerModule() {
        this.accountService = new AccountService();
        this.loanService = new LoanService();
        this.auditLogDAO = new AuditLogDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = InputUtil.readInt("Select an option: ");

            switch (choice) {
                case 1 -> displayDashboard();
                case 2 -> viewCustomers();
                case 3 -> approveLoan();
                case 4 -> rejectLoan();
                case 5 -> viewAuditLogs();
                case 6 -> {
                    running = false;
                    InputUtil.printSuccess("Logging out...");
                }
                default -> InputUtil.printError("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        InputUtil.printDottedSeparator();
        System.out.println("    MANAGER MENU");
        InputUtil.printDottedSeparator();
        System.out.println("1. Dashboard");
        System.out.println("2. View Customers");
        System.out.println("3. Approve Loan");
        System.out.println("4. Reject Loan");
        System.out.println("5. View Audit Logs");
        System.out.println("6. Logout");
        InputUtil.printDottedSeparator();
    }

    private void displayDashboard() {
        try {
            InputUtil.printSeparator();
            System.out.println("    MANAGER DASHBOARD");
            InputUtil.printDottedSeparator();

            // Count total customers (users with CUSTOMER role)
            long totalCustomers = userDAO.countUsersByRole(AppConstants.ROLE_CUSTOMER);
            System.out.println("Total Customers    : " + totalCustomers);

            // Count total accounts
            long totalAccounts = accountService.countTotalAccounts();
            System.out.println("Total Accounts     : " + totalAccounts);

            // Count today's transactions
            java.time.LocalDate today = java.time.LocalDate.now();
            long todayTransactions = loanService.countTransactionsByDate(today.toString());
            System.out.println("Today's Transactions: " + todayTransactions);

            List<Loan> pendingLoans = loanService.getAppliedLoans();
            System.out.println("Pending Loans      : " + pendingLoans.size());

            // Count branch employees (users with TELLER or MANAGER role)
            long branchEmployees = userDAO.countUsersByRole(AppConstants.ROLE_TELLER) +
                    userDAO.countUsersByRole(AppConstants.ROLE_MANAGER);
            System.out.println("Branch Employees   : " + branchEmployees);
            InputUtil.printSeparator();
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void viewCustomers() {
        try {
            InputUtil.printSeparator();
            System.out.println("    CUSTOMER LIST");
            InputUtil.printDottedSeparator();
            System.out.println("Search by Account Number to view details:");
            String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");

            Account account = accountService.getAccountByNumber(accountNumber);
            displayCustomerAndAccountDetails(account);

        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void displayCustomerAndAccountDetails(Account account) {
        try {
            var user = userDAO.getUserById(account.getUserId());
            InputUtil.printSeparator();
            System.out.println("    CUSTOMER INFORMATION");
            InputUtil.printDottedSeparator();
            System.out.println("Name               : " + user.getFullName());
            System.out.println("Email              : " + user.getEmail());
            System.out.println("Phone              : " + user.getPhoneNumber());
            System.out.println("Account Number     : " + account.getAccountNumber());
            System.out.println("Account Type       : " + account.getAccountType());
            System.out.println("Account Status     : " + account.getStatus());
            System.out.println("Current Balance    : ₹ " + String.format("%.2f", account.getBalance()));
            InputUtil.printSeparator();
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void approveLoan() {
        try {
            InputUtil.printSeparator();
            System.out.println("    APPROVE LOAN");
            InputUtil.printDottedSeparator();

            List<Loan> pendingLoans = loanService.getAppliedLoans();
            if (pendingLoans.isEmpty()) {
                InputUtil.printInfo("No pending loans.");
                return;
            }

            // Display pending loans
            System.out.println("PENDING LOANS:");
            for (Loan loan : pendingLoans) {
                System.out.printf("LoanID: %d | Type: %s | Amount: ₹%.2f | Status: %s%n",
                        loan.getLoanId(), loan.getLoanType(), loan.getPrincipalAmount(), loan.getStatus());
            }

            long loanId = InputUtil.readLong("Enter Loan ID to approve: ");
            Loan loan = loanService.getLoanById(loanId);

            var customer = userDAO.getUserById(loan.getUserId());
            var primaryAccount = accountService.getCustomerPrimaryAccount(loan.getUserId());

            System.out.println("\nApproving loan for: " + customer.getFullName());
            System.out.println("Loan Amount: ₹" + loan.getPrincipalAmount());

            boolean confirm = InputUtil.readYesNo("Confirm approval?");
            if (!confirm) {
                InputUtil.printInfo("Approval cancelled.");
                return;
            }

            loanService.approveLoan(loanId, primaryAccount.getAccountId());

            ReceiptUtil.printLoanApproval(customer.getFullName(), loan.getLoanType(), loan.getPrincipalAmount(),
                    loanId);

        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void rejectLoan() {
        try {
            InputUtil.printSeparator();
            System.out.println("    REJECT LOAN");
            InputUtil.printDottedSeparator();

            List<Loan> pendingLoans = loanService.getAppliedLoans();
            if (pendingLoans.isEmpty()) {
                InputUtil.printInfo("No pending loans.");
                return;
            }

            System.out.println("PENDING LOANS:");
            for (Loan loan : pendingLoans) {
                System.out.printf("LoanID: %d | Type: %s | Amount: ₹%.2f%n",
                        loan.getLoanId(), loan.getLoanType(), loan.getPrincipalAmount());
            }

            long loanId = InputUtil.readLong("Enter Loan ID to reject: ");
            Loan loan = loanService.getLoanById(loanId);

            var customer = userDAO.getUserById(loan.getUserId());

            System.out.println("\nRejecting loan for: " + customer.getFullName());
            boolean confirm = InputUtil.readYesNo("Confirm rejection?");
            if (!confirm) {
                InputUtil.printInfo("Rejection cancelled.");
                return;
            }

            loanService.rejectLoan(loanId);
            InputUtil.printSuccess("Loan rejected successfully!");

        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void viewAuditLogs() {
        try {
            InputUtil.printSeparator();
            System.out.println("    AUDIT LOGS");
            InputUtil.printDottedSeparator();

            List<AuditLog> logs = auditLogDAO.getAllAuditLogs();

            if (logs.isEmpty()) {
                InputUtil.printInfo("No audit logs found.");
                return;
            }

            // Display first 50 audit logs
            int limit = Math.min(50, logs.size());
            System.out.printf("%-8s %-12s %-12s %-12s %-15s%n", "LogID", "TxnID", "OldBalance", "NewBalance", "Action");
            InputUtil.printDottedSeparator();
            for (int i = 0; i < limit; i++) {
                AuditLog log = logs.get(i);
                System.out.printf("%-8d %-12d ₹%-11.2f ₹%-11.2f %-15s%n",
                        log.getLogId(), log.getTransactionId(), log.getOldBalance(), log.getNewBalance(),
                        log.getActionType());
            }
            InputUtil.printSeparator();
            System.out.println("Showing " + limit + " of " + logs.size() + " records.");

        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }
}
