package com.ashok.bank.dao;

import com.ashok.bank.model.Branch;
import com.ashok.bank.exception.DatabaseException;
import java.util.List;

public interface BranchDAO {
    Branch getBranchById(long branchId) throws DatabaseException;

    Branch getBranchByCode(String branchCode) throws DatabaseException;

    List<Branch> getAllBranches() throws DatabaseException;
}
