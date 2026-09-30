package com.ashok.bank.auth;

import com.ashok.bank.service.AuthenticationService;
import com.ashok.bank.model.User;
import com.ashok.bank.util.InputUtil;
import com.ashok.bank.exception.AuthenticationException;
import com.ashok.bank.exception.DatabaseException;

public class LoginUI {
    private final AuthenticationService authService;
    private final UserSession userSession;

    public LoginUI() {
        this.authService = new AuthenticationService();
        this.userSession = UserSession.getInstance();
    }

    public boolean login() {
        InputUtil.printSeparator();
        System.out.println("    ASHOK BANK OF INDIA - LOGIN");
        InputUtil.printDottedSeparator();

        try {
            String email = InputUtil.readEmail("Enter Email: ");
            String password = InputUtil.readStringRequired("Enter Password: ");

            User user = authService.login(email, password);
            userSession.login(user);

            InputUtil.printSuccess("Login successful! Welcome, " + user.getFullName());
            return true;

        } catch (AuthenticationException e) {
            InputUtil.printError(e.getMessage());
            return false;
        } catch (DatabaseException e) {
            InputUtil.printError("Database error: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        userSession.logout();
        InputUtil.printSuccess("Logged out successfully!");
    }

    public boolean isUserLoggedIn() {
        return userSession.isLoggedIn();
    }

    public String getCurrentUserRole() {
        return userSession.getUserRole();
    }
}
