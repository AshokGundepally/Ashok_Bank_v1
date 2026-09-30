package com.ashok.bank.model;

import java.time.LocalDateTime;

public class Loan {
    private long loanId;
    private long userId;
    private String loanType;
    private double principalAmount;
    private double interestRate;
    private int termMonths;
    private String status;
    private LocalDateTime createdAt;

    public Loan(long loanId, long userId, String loanType, double principalAmount, double interestRate, int termMonths,
            String status, LocalDateTime createdAt) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Loan() {
    }

    // Getters and Setters
    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", loanType='" + loanType + '\'' +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", termMonths=" + termMonths +
                ", status='" + status + '\'' +
                '}';
    }
}
