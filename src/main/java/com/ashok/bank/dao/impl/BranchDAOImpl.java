package com.ashok.bank.dao.impl;

import com.ashok.bank.dao.BranchDAO;
import com.ashok.bank.model.Branch;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAOImpl implements BranchDAO {

    @Override
    public Branch getBranchById(long branchId) throws DatabaseException {
        String sql = "SELECT branch_id, branch_code, branch_name, ifsc_code, city, state FROM branches WHERE branch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching branch by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Branch getBranchByCode(String branchCode) throws DatabaseException {
        String sql = "SELECT branch_id, branch_code, branch_name, ifsc_code, city, state FROM branches WHERE branch_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branchCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching branch by code: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Branch> getAllBranches() throws DatabaseException {
        String sql = "SELECT branch_id, branch_code, branch_name, ifsc_code, city, state FROM branches ORDER BY branch_name";
        List<Branch> branches = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(mapResultSetToBranch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all branches: " + e.getMessage(), e);
        }
        return branches;
    }

    private Branch mapResultSetToBranch(ResultSet rs) throws SQLException {
        long branchId = rs.getLong("branch_id");
        String branchCode = rs.getString("branch_code");
        String branchName = rs.getString("branch_name");
        String ifscCode = rs.getString("ifsc_code");
        String city = rs.getString("city");
        String state = rs.getString("state");

        return new Branch(branchId, branchCode, branchName, ifscCode, city, state);
    }
}
