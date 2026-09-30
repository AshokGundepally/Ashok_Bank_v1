package com.ashok.bank.dao;

import com.ashok.bank.model.Loan;
import com.ashok.bank.exception.DatabaseException;
import java.util.List;

public interface LoanDAO {
    long createLoan(long userId, String loanType, double principalAmount, double interestRate, int termMonths)
            throws DatabaseException;

    Loan getLoanById(long loanId) throws DatabaseException;

    List<Loan> getLoansByUserId(long userId) throws DatabaseException;

    List<Loan> getLoansByStatus(String status) throws DatabaseException;

    void updateLoanStatus(long loanId, String status) throws DatabaseException;
}
