package com.ashok.bank.dao;

import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.model.User;

public interface UserDAO {

        User getUserByEmail(String email) throws DatabaseException;

        User getUserById(long userId) throws DatabaseException;

        long createUser(String fullName,
                        String email,
                        String phoneNumber,
                        String password,
                        String role)
                        throws DatabaseException;

        void updatePassword(long userId,
                        String newPassword)
                        throws DatabaseException;

        boolean emailExists(String email) throws DatabaseException;

        boolean phoneExists(String phone) throws DatabaseException;

        User getUserByPhone(String phoneNumber) throws DatabaseException;

        long countUsersByRole(String role) throws DatabaseException;
}