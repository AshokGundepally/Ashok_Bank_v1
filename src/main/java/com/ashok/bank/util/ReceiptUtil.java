package com.ashok.bank.util;

import com.ashok.bank.constants.AppConstants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptUtil {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void printDeposit(String customerName, String maskedAccountNumber, double amount,
            double balanceRemaining, long transactionId) {
        System.out.println("\n" + AppConstants.RECEIPT_SEPARATOR);
        System.out.println(AppConstants.BANK_NAME);
        System.out.println("DEPOSIT RECEIPT");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Customer Name      : " + customerName);
        System.out.println("Account Number     : " + maskedAccountNumber);
        System.out.println("Amount             : ₹ " + String.format("%.2f", amount));
        System.out.println("Balance Remaining  : ₹ " + String.format("%.2f", balanceRemaining));
        System.out.println("Transaction ID     : " + transactionId);
        System.out.println("Date               : " + LocalDateTime.now().format(dateFormatter));
        System.out.println("Status             : SUCCESS");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Thank you for banking with us!\n");
    }

    public static void printWithdrawal(String customerName, String maskedAccountNumber, double amount,
            double balanceRemaining, long transactionId) {
        System.out.println("\n" + AppConstants.RECEIPT_SEPARATOR);
        System.out.println(AppConstants.BANK_NAME);
        System.out.println("WITHDRAWAL RECEIPT");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Customer Name      : " + customerName);
        System.out.println("Account Number     : " + maskedAccountNumber);
        System.out.println("Amount             : ₹ " + String.format("%.2f", amount));
        System.out.println("Balance Remaining  : ₹ " + String.format("%.2f", balanceRemaining));
        System.out.println("Transaction ID     : " + transactionId);
        System.out.println("Date               : " + LocalDateTime.now().format(dateFormatter));
        System.out.println("Status             : SUCCESS");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Thank you for banking with us!\n");
    }

    public static void printTransfer(String senderName, String senderMaskedAccount, String receiverName,
            String receiverMaskedAccount, double amount, double balanceRemaining, long transactionId) {
        System.out.println("\n" + AppConstants.RECEIPT_SEPARATOR);
        System.out.println(AppConstants.BANK_NAME);
        System.out.println("FUND TRANSFER RECEIPT");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("From               : " + senderName);
        System.out.println("Account Number     : " + senderMaskedAccount);
        System.out.println("To                 : " + receiverName);
        System.out.println("Account Number     : " + receiverMaskedAccount);
        System.out.println("Amount             : ₹ " + String.format("%.2f", amount));
        System.out.println("Balance Remaining  : ₹ " + String.format("%.2f", balanceRemaining));
        System.out.println("Transaction ID     : " + transactionId);
        System.out.println("Date               : " + LocalDateTime.now().format(dateFormatter));
        System.out.println("Status             : SUCCESS");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Thank you for banking with us!\n");
    }

    public static void printLoanApproval(String customerName, String loanType, double loanAmount, long transactionId) {
        System.out.println("\n" + AppConstants.RECEIPT_SEPARATOR);
        System.out.println(AppConstants.BANK_NAME);
        System.out.println("LOAN APPROVAL RECEIPT");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Customer Name      : " + customerName);
        System.out.println("Loan Type          : " + loanType);
        System.out.println("Loan Amount        : ₹ " + String.format("%.2f", loanAmount));
        System.out.println("Transaction ID     : " + transactionId);
        System.out.println("Date               : " + LocalDateTime.now().format(dateFormatter));
        System.out.println("Status             : APPROVED & CREDITED");
        System.out.println(AppConstants.RECEIPT_SEPARATOR);
        System.out.println("Thank you for banking with us!\n");
    }

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber.length() <= 4) {
            return accountNumber;
        }
        String last4 = accountNumber.substring(accountNumber.length() - 4);
        return "XXXX" + last4;
    }
}
