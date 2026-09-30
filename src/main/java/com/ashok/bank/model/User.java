package com.ashok.bank.model;

import java.time.LocalDateTime;

public class User {

    private long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password; // Changed from passwordHash
    private String role;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(long userId, String fullName, String email,
            String phoneNumber, String password,
            String role, LocalDateTime createdAt) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // =======================
    // Getters and Setters
    // =======================

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() { // Changed
        return password;
    }

    public void setPassword(String password) { // Changed
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}