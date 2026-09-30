package com.ashok.bank.dao;

import com.ashok.bank.model.AuditLog;
import com.ashok.bank.exception.DatabaseException;
import java.util.List;

public interface AuditLogDAO {
    void createAuditLog(long transactionId, long accountId, String actionType, double oldBalance, double newBalance)
            throws DatabaseException;

    AuditLog getAuditLogById(long logId) throws DatabaseException;

    List<AuditLog> getAuditLogsByAccountId(long accountId) throws DatabaseException;

    List<AuditLog> getAllAuditLogs() throws DatabaseException;
}
