package com.ashok.bank.customer;

import com.ashok.bank.auth.UserSession;
import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.impl.UserDAOImpl;
import com.ashok.bank.model.Account;
import com.ashok.bank.model.Loan;
import com.ashok.bank.model.Transaction;
import com.ashok.bank.model.User;
import com.ashok.bank.service.AccountService;
import com.ashok.bank.service.AuthenticationService;
import com.ashok.bank.service.LoanService;
import com.ashok.bank.service.TransactionService;
import com.ashok.bank.util.InputUtil;
import com.ashok.bank.util.ReceiptUtil;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.InsufficientBalanceException;
import com.ashok.bank.exception.ValidationException;
import java.util.List;

public class CustomerModule {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final AuthenticationService authService;
    private final UserSession userSession;

    public CustomerModule() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        this.loanService = new LoanService();
        this.authService = new AuthenticationService();
        this.userSession = UserSession.getInstance();
    }

    public void start() {
        try {
            long userId = userSession.getUserId();
            Account primaryAccount = accountService.getCustomerPrimaryAccount(userId);

            // Verify account status
            if (!primaryAccount.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
                InputUtil.printError("Your account is inactive. Please visit your branch to reactivate your account.");
                return;
            }

            boolean running = true;
            while (running) {
                displayMenu();
                int choice = InputUtil.readInt("Select an option: ");

                switch (choice) {
                    case 1 -> displayDashboard();
                    case 2 -> checkBalance();
                    case 3 -> viewMiniStatement();
                    case 4 -> transferMoney();
                    case 5 -> applyForLoan();
                    case 6 -> viewLoanStatus();
                    case 7 -> changePassword();
                    case 8 -> {
                        running = false;
                        InputUtil.printSuccess("Logging out...");
                    }
                    default -> InputUtil.printError("Invalid option. Please try again.");
                }
            }
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void displayMenu() {
        InputUtil.printDottedSeparator();
        System.out.println("    CUSTOMER MENU");
        InputUtil.printDottedSeparator();
        System.out.println("1. Dashboard");
        System.out.println("2. Check Balance");
        System.out.println("3. Mini Statement");
        System.out.println("4. Transfer Money");
        System.out.println("5. Apply for Loan");
        System.out.println("6. Loan Status");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        InputUtil.printDottedSeparator();
    }

    private void displayDashboard() {
        try {
            User user = userSession.getCurrentUser();
            Account account = accountService.getCustomerPrimaryAccount(user.getUserId());

            InputUtil.printSeparator();
            System.out.println("    CUSTOMER DASHBOARD");
            InputUtil.printDottedSeparator();
            System.out.println("Customer Name      : " + user.getFullName());
            System.out.println("Account Number     : " + account.getAccountNumber());
            System.out.println("Account Type       : " + account.getAccountType());
            System.out.println("Current Balance    : ₹ " + String.format("%.2f", account.getBalance()));
            System.out.println("Account Status     : " + account.getStatus());
            InputUtil.printSeparator();
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void checkBalance() {
        try {
            User user = userSession.getCurrentUser();
            Account account = accountService.getCustomerPrimaryAccount(user.getUserId());

            InputUtil.printSeparator();
            System.out.println("    BALANCE INQUIRY");
            InputUtil.printDottedSeparator();
            System.out.println("Available Balance  : ₹ " + String.format("%.2f", account.getBalance()));
            System.out.println("Account Status     : " + account.getStatus());
            System.out.println("Account Type       : " + account.getAccountType());
            InputUtil.printSeparator();
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void viewMiniStatement() {
        try {
            User user = userSession.getCurrentUser();
            Account account = accountService.getCustomerPrimaryAccount(user.getUserId());

            InputUtil.printDottedSeparator();
            System.out.println("SELECT TRANSACTION PERIOD:");
            System.out.println("1. Last 10 Transactions");
            System.out.println("2. Last 6 Months");
            System.out.println("3. Last 12 Months");
            int choice = InputUtil.readInt("Select: ");

            List<Transaction> transactions = switch (choice) {
                case 1 -> transactionService.getLastNTransactions(account.getAccountId(), 10);
                case 2 -> transactionService.getTransactionsForMonths(account.getAccountId(), 6);
                case 3 -> transactionService.getTransactionsForMonths(account.getAccountId(), 12);
                default -> {
                    InputUtil.printError("Invalid choice.");
                    yield List.of();
                }
            };

            displayTransactionsTable(transactions);
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void transferMoney() {
        try {
            User user = userSession.getCurrentUser();
            Account senderAccount = accountService.getCustomerPrimaryAccount(user.getUserId());

            InputUtil.printSeparator();
            System.out.println("    FUND TRANSFER");
            InputUtil.printDottedSeparator();

            String receiverAccNumber = InputUtil.readStringRequired("Enter Receiver Account Number: ");
            Account receiverAccount = accountService.getAccountByNumber(receiverAccNumber);

            User receiverUser = new UserDAOImpl().getUserById(receiverAccount.getUserId());

            System.out.println("\nReceiver Name      : " + receiverUser.getFullName());
            boolean confirm = InputUtil.readYesNo("Do you want to continue?");

            if (!confirm) {
                InputUtil.printInfo("Transfer cancelled.");
                return;
            }

            double amount = InputUtil.readDouble("Enter Amount: ");

            long transactionId = transactionService.transferMoney(senderAccount.getAccountId(),
                    receiverAccount.getAccountId(), amount);

            Account updatedSenderAccount = accountService.getAccountById(senderAccount.getAccountId());
            ReceiptUtil.printTransfer(user.getFullName(),
                    ReceiptUtil.maskAccountNumber(senderAccount.getAccountNumber()),
                    receiverUser.getFullName(), ReceiptUtil.maskAccountNumber(receiverAccount.getAccountNumber()),
                    amount, updatedSenderAccount.getBalance(), transactionId);

        } catch (DatabaseException | ValidationException | InsufficientBalanceException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void applyForLoan() {
        try {
            User user = userSession.getCurrentUser();

            InputUtil.printSeparator();
            System.out.println("    APPLY FOR LOAN");
            InputUtil.printDottedSeparator();
            System.out.println("1. Home Loan");
            System.out.println("2. Education Loan");
            System.out.println("3. Auto Loan");
            System.out.println("4. Personal Loan");

            String loanType = switch (InputUtil.readInt("Select Loan Type: ")) {
                case 1 -> AppConstants.LOAN_HOME;
                case 2 -> AppConstants.LOAN_EDUCATION;
                case 3 -> AppConstants.LOAN_AUTO;
                case 4 -> AppConstants.LOAN_PERSONAL;
                default -> {
                    InputUtil.printError("Invalid choice.");
                    yield null;
                }
            };

            if (loanType == null) {
                return;
            }

            double amount = InputUtil.readDouble("Enter Loan Amount: ");
            int months = InputUtil.readInt("Enter Tenure (in months): ");

            long loanId = loanService.applyForLoan(user.getUserId(), loanType, amount, months);

            InputUtil.printSuccess("Loan application submitted successfully!");
            System.out.println("Loan ID : " + loanId);
            System.out.println("Status  : APPLIED");
            InputUtil.printInfo("Your application is under review. Please check back later.");
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void viewLoanStatus() {
        try {
            User user = userSession.getCurrentUser();
            List<Loan> loans = loanService.getLoansByUserId(user.getUserId());

            if (loans.isEmpty()) {
                InputUtil.printInfo("No loans found.");
                return;
            }

            InputUtil.printSeparator();
            System.out.println("    LOAN STATUS");
            InputUtil.printDottedSeparator();
            System.out.printf("%-8s %-12s %-12s %-8s %-8s %-12s%n", "Loan ID", "Type", "Principal", "Rate", "Months",
                    "Status");
            InputUtil.printDottedSeparator();
            for (Loan loan : loans) {
                System.out.printf("%-8d %-12s ₹%-11.2f %-8.2f%% %-8d %-12s%n",
                        loan.getLoanId(), loan.getLoanType(), loan.getPrincipalAmount(),
                        loan.getInterestRate(), loan.getTermMonths(), loan.getStatus());
            }
            InputUtil.printSeparator();
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void changePassword() {
        try {
            String oldPassword = InputUtil.readStringRequired("Enter Old Password: ");
            String newPassword = InputUtil.readStringRequired("Enter New Password: ");
            String confirmPassword = InputUtil.readStringRequired("Confirm New Password: ");

            if (!newPassword.equals(confirmPassword)) {
                InputUtil.printError("Passwords do not match.");
                return;
            }

            User user = userSession.getCurrentUser();
            authService.changePassword(user.getUserId(), oldPassword, newPassword);

            InputUtil.printSuccess("Password changed successfully!");
        } catch (DatabaseException | ValidationException | com.ashok.bank.exception.AuthenticationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void displayTransactionsTable(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            InputUtil.printInfo("No transactions found.");
            return;
        }

        InputUtil.printSeparator();
        System.out.println("    MINI STATEMENT");
        InputUtil.printDottedSeparator();
        System.out.printf("%-6s %-12s %-10s %-10s %-12s%n", "TxnID", "Type", "Amount", "Status", "Date");
        InputUtil.printDottedSeparator();
        for (Transaction txn : transactions) {
            System.out.printf("%-6d %-12s ₹%-9.2f %-10s %-12s%n",
                    txn.getTransactionId(), txn.getTransactionType(), txn.getAmount(),
                    txn.getStatus(), txn.getCreatedAt().toLocalDate());
        }
        InputUtil.printSeparator();
    }
}
