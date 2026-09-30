package com.ashok.bank.teller;

import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.dao.impl.UserDAOImpl;
import com.ashok.bank.model.Account;
import com.ashok.bank.model.Loan;
import com.ashok.bank.model.Transaction;
import com.ashok.bank.model.User;
import com.ashok.bank.service.AccountService;
import com.ashok.bank.service.LoanService;
import com.ashok.bank.service.TransactionService;
import com.ashok.bank.util.InputUtil;
import com.ashok.bank.util.ReceiptUtil;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.InsufficientBalanceException;
import com.ashok.bank.exception.ValidationException;
import java.time.LocalDate;
import java.util.List;

public class TellerModule {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final UserDAOImpl userDAO;

    public TellerModule() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        this.loanService = new LoanService();
        this.userDAO = new UserDAOImpl();
    }

    public void start() throws DatabaseException {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = InputUtil.readInt("Select an option: ");

            switch (choice) {
                case 1 -> displayDashboard();
                case 2 -> searchCustomer();
                case 3 -> cashDeposit();
                case 4 -> cashWithdrawal();
                case 5 -> fundTransfer();
                case 6 -> openNewAccount();
                case 7 -> freezeAccount();
                case 8 -> unfreezeAccount();
                case 9 -> viewLoanRequests();
                case 10 -> {
                    running = false;
                    InputUtil.printSuccess("Logging out...");
                }
                default -> InputUtil.printError("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        InputUtil.printDottedSeparator();
        System.out.println("    TELLER MENU");
        InputUtil.printDottedSeparator();
        System.out.println("1. Dashboard");
        System.out.println("2. Search Customer");
        System.out.println("3. Cash Deposit");
        System.out.println("4. Cash Withdrawal");
        System.out.println("5. Fund Transfer");
        System.out.println("6. Open New Account");
        System.out.println("7. Freeze Account");
        System.out.println("8. Unfreeze Account");
        System.out.println("9. View Loan Requests");
        System.out.println("10. Logout");
        InputUtil.printDottedSeparator();
    }

    private void displayDashboard() {
        try {
            LocalDate today = LocalDate.now();
            List<Transaction> todayTxns = transactionService.getLastNTransactions(1, 1000);
            long deposits = todayTxns.stream()
                    .filter(t -> t.getTransactionType().equals(AppConstants.TRANSACTION_DEPOSIT)
                            && t.getCreatedAt().toLocalDate().equals(today))
                    .count();
            long withdrawals = todayTxns.stream()
                    .filter(t -> t.getTransactionType().equals(AppConstants.TRANSACTION_WITHDRAWAL)
                            && t.getCreatedAt().toLocalDate().equals(today))
                    .count();
            long transfers = todayTxns.stream()
                    .filter(t -> t.getTransactionType().equals(AppConstants.TRANSACTION_TRANSFER)
                            && t.getCreatedAt().toLocalDate().equals(today))
                    .count();

            InputUtil.printSeparator();
            System.out.println("    TELLER DASHBOARD");
            InputUtil.printDottedSeparator();
            System.out.println("Today's Deposits   : " + deposits);
            System.out.println("Today's Withdrawals: " + withdrawals);
            System.out.println("Today's Transfers  : " + transfers);
            InputUtil.printSeparator();
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void searchCustomer() throws DatabaseException {
        InputUtil.printSeparator();
        System.out.println("    SEARCH CUSTOMER");
        InputUtil.printDottedSeparator();
        System.out.println("1. Search by Account Number");
        System.out.println("2. Search by Phone Number");
        System.out.println("3. Search by Email");

        try {
            int choice = InputUtil.readInt("Select search type: ");
            switch (choice) {
                case 1 -> searchByAccountNumber();
                case 2 -> searchByPhoneNumber();
                case 3 -> searchByEmail();
                default -> InputUtil.printError("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            InputUtil.printError("Invalid input.");
        }
    }

    private void searchByAccountNumber() throws DatabaseException {
        String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");
        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            User user = userDAO.getUserById(account.getUserId());
            displayCustomerDetails(user, account);
        } catch (ValidationException | DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void searchByPhoneNumber() throws DatabaseException {
        String phoneNumber = InputUtil.readPhoneNumber("Enter Phone Number: ");
        try {
            User user = userDAO.getUserByPhone(phoneNumber);
            if (user == null) {
                InputUtil.printError("User not found.");
                return;
            }
            List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
            if (!accounts.isEmpty()) {
                displayCustomerDetails(user, accounts.get(0));
            } else {
                InputUtil.printError("No accounts found for this user.");
            }
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void searchByEmail() throws DatabaseException {
        String email = InputUtil.readEmail("Enter Email: ");
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                InputUtil.printError("User not found.");
                return;
            }
            List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
            if (!accounts.isEmpty()) {
                displayCustomerDetails(user, accounts.get(0));
            }
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void displayCustomerDetails(User user, Account account) {
        InputUtil.printSeparator();
        System.out.println("    CUSTOMER DETAILS");
        InputUtil.printDottedSeparator();
        System.out.println("Name               : " + user.getFullName());
        System.out.println("Email              : " + user.getEmail());
        System.out.println("Phone              : " + user.getPhoneNumber());
        System.out.println("Account Number     : " + account.getAccountNumber());
        System.out.println("Account Type       : " + account.getAccountType());
        System.out.println("Account Status     : " + account.getStatus());
        System.out.println("Current Balance    : ₹ " + String.format("%.2f", account.getBalance()));
        InputUtil.printSeparator();
    }

    private void cashDeposit() {
        try {
            String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");
            Account account = accountService.getAccountByNumber(accountNumber);
            User user = userDAO.getUserById(account.getUserId());

            if (!account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
                InputUtil.printError("Account is not active.");
                return;
            }

            double amount = InputUtil.readDouble("Enter Deposit Amount: ");
            long transactionId = transactionService.depositAmount(account.getAccountId(), amount);

            Account updatedAccount = accountService.getAccountById(account.getAccountId());
            ReceiptUtil.printDeposit(user.getFullName(), ReceiptUtil.maskAccountNumber(account.getAccountNumber()),
                    amount, updatedAccount.getBalance(), transactionId);

        } catch (DatabaseException | ValidationException | InsufficientBalanceException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void cashWithdrawal() {
        try {
            String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");
            Account account = accountService.getAccountByNumber(accountNumber);
            User user = userDAO.getUserById(account.getUserId());

            if (!account.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
                InputUtil.printError("Account is not active.");
                return;
            }

            double amount = InputUtil.readDouble("Enter Withdrawal Amount: ");
            long transactionId = transactionService.withdrawAmount(account.getAccountId(), amount);

            Account updatedAccount = accountService.getAccountById(account.getAccountId());
            ReceiptUtil.printWithdrawal(user.getFullName(), ReceiptUtil.maskAccountNumber(account.getAccountNumber()),
                    amount, updatedAccount.getBalance(), transactionId);

        } catch (DatabaseException | ValidationException | InsufficientBalanceException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void fundTransfer() {
        try {
            InputUtil.printSeparator();
            System.out.println("    FUND TRANSFER");

            String senderAccNumber = InputUtil.readStringRequired("Enter Sender Account Number: ");
            Account senderAccount = accountService.getAccountByNumber(senderAccNumber);
            User senderUser = userDAO.getUserById(senderAccount.getUserId());

            if (!senderAccount.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
                InputUtil.printError("Sender account is not active.");
                return;
            }

            String receiverAccNumber = InputUtil.readStringRequired("Enter Receiver Account Number: ");
            Account receiverAccount = accountService.getAccountByNumber(receiverAccNumber);
            User receiverUser = userDAO.getUserById(receiverAccount.getUserId());

            if (!receiverAccount.getStatus().equals(AppConstants.ACCOUNT_ACTIVE)) {
                InputUtil.printError("Receiver account is not active.");
                return;
            }

            System.out.println("\nReceiver Name      : " + receiverUser.getFullName());
            boolean confirm = InputUtil.readYesNo("Proceed?");

            if (!confirm) {
                InputUtil.printInfo("Transfer cancelled.");
                return;
            }

            double amount = InputUtil.readDouble("Enter Amount: ");
            long transactionId = transactionService.transferMoney(senderAccount.getAccountId(),
                    receiverAccount.getAccountId(), amount);

            Account updatedSenderAccount = accountService.getAccountById(senderAccount.getAccountId());
            ReceiptUtil.printTransfer(senderUser.getFullName(),
                    ReceiptUtil.maskAccountNumber(senderAccount.getAccountNumber()),
                    receiverUser.getFullName(), ReceiptUtil.maskAccountNumber(receiverAccount.getAccountNumber()),
                    amount, updatedSenderAccount.getBalance(), transactionId);

        } catch (DatabaseException | ValidationException | InsufficientBalanceException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void openNewAccount() {
        try {
            String email = InputUtil.readEmail("Enter Customer Email: ");
            User user = userDAO.getUserByEmail(email);

            if (user == null) {
                InputUtil.printError("Customer not found.");
                return;
            }

            System.out.println("\nAccount Types: SAVINGS, CHECKING, FIXED_DEPOSIT");
            String accountType = InputUtil.readStringRequired("Enter Account Type: ");

            long accountId = accountService.createAccount(user.getUserId(), 1, accountType);
            Account newAccount = accountService.getAccountById(accountId);

            InputUtil.printSuccess("New account created successfully!");
            System.out.println("Account Number : " + newAccount.getAccountNumber());
            System.out.println("Account Type   : " + newAccount.getAccountType());
            System.out.println("Status         : " + newAccount.getStatus());
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void freezeAccount() {
        try {
            String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");
            Account account = accountService.getAccountByNumber(accountNumber);
            accountService.freezeAccount(account.getAccountId());
            InputUtil.printSuccess("Account frozen successfully!");
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void unfreezeAccount() {
        try {
            String accountNumber = InputUtil.readStringRequired("Enter Account Number: ");
            Account account = accountService.getAccountByNumber(accountNumber);
            accountService.unfreezeAccount(account.getAccountId());
            InputUtil.printSuccess("Account unfrozen successfully!");
        } catch (DatabaseException | ValidationException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }

    private void viewLoanRequests() {
        try {
            List<Loan> loans = loanService.getAppliedLoans();

            if (loans.isEmpty()) {
                InputUtil.printInfo("No pending loan applications.");
                return;
            }

            InputUtil.printSeparator();
            System.out.println("    PENDING LOAN REQUESTS");
            InputUtil.printDottedSeparator();
            System.out.printf("%-8s %-12s %-12s %-8s %-12s%n", "LoanID", "Type", "Principal", "Months", "Status");
            InputUtil.printDottedSeparator();
            for (Loan loan : loans) {
                System.out.printf("%-8d %-12s ₹%-11.2f %-8d %-12s%n",
                        loan.getLoanId(), loan.getLoanType(), loan.getPrincipalAmount(), loan.getTermMonths(),
                        loan.getStatus());
            }
            InputUtil.printSeparator();
            InputUtil.printInfo("Teller can only verify documents. Loan approval is manager's responsibility.");
        } catch (DatabaseException e) {
            InputUtil.printError("Error: " + e.getMessage());
        }
    }
}
