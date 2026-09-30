package com.ashok.bank.service;

import com.ashok.bank.dao.UserDAO;
import com.ashok.bank.dao.impl.UserDAOImpl;
import com.ashok.bank.model.User;
import com.ashok.bank.exception.AuthenticationException;
import com.ashok.bank.exception.DatabaseException;
import com.ashok.bank.exception.ValidationException;

public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAOImpl();
    }

    public User login(String email, String password)
            throws AuthenticationException, DatabaseException {

        if (email == null || email.isBlank()) {
            throw new AuthenticationException("Email cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            throw new AuthenticationException("Invalid email or password.");
        }

        // Simple password verification
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    public void changePassword(long userId,
            String oldPassword,
            String newPassword)
            throws AuthenticationException, DatabaseException, ValidationException {

        if (oldPassword == null || oldPassword.isBlank()) {
            throw new ValidationException("Old password cannot be empty.");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("New password cannot be empty.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new ValidationException("New password must be different from old password.");
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            throw new AuthenticationException("User not found.");
        }

        if (!oldPassword.equals(user.getPassword())) {
            throw new AuthenticationException("Old password is incorrect.");
        }

        userDAO.updatePassword(userId, newPassword);
    }

    public boolean emailExists(String email) throws DatabaseException {
        return userDAO.emailExists(email);
    }

    public boolean phoneExists(String phone) throws DatabaseException {
        return userDAO.phoneExists(phone);
    }
}