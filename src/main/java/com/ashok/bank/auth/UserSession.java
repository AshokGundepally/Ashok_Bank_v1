package com.ashok.bank.auth;

import com.ashok.bank.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private long loginTime;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
    }

    public void logout() {
        this.currentUser = null;
        this.loginTime = 0;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public long getUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public boolean isSessionExpired(long sessionTimeout) {
        if (!isLoggedIn()) {
            return true;
        }
        return (System.currentTimeMillis() - loginTime) > sessionTimeout;
    }
}
