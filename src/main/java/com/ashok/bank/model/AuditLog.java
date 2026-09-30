package com.ashok.bank.model;

import java.time.LocalDateTime;

public class AuditLog {
    private long logId;
    private long transactionId;
    private long accountId;
    private String actionType;
    private double oldBalance;
    private double newBalance;
    private LocalDateTime loggedAt;

    public AuditLog(long logId, long transactionId, long accountId, String actionType, double oldBalance, double newBalance, LocalDateTime loggedAt) {
        this.logId = logId;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.actionType = actionType;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.loggedAt = loggedAt;
    }

    public AuditLog() {}

    // Getters and Setters
    public long getLogId() { return logId; }
    public void setLogId(long logId) { this.logId = logId; }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public double getOldBalance() { return oldBalance; }
    public void setOldBalance(double oldBalance) { this.oldBalance = oldBalance; }

    public double getNewBalance() { return newBalance; }
    public void setNewBalance(double newBalance) { this.newBalance = newBalance; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", transactionId=" + transactionId +
                ", actionType='" + actionType + '\'' +
                ", oldBalance=" + oldBalance +
                ", newBalance=" + newBalance +
                ", loggedAt=" + loggedAt +
                '}';
    }
}
