package com.ashok.bank.util;

public class PasswordUtil {

    // Store password as plaintext (no hashing)
    public static String hashPassword(String password) {
        return password;
    }

    // Verify password with simple string comparison
    public static boolean verifyPassword(String password, String storedPassword) {
        return password.equals(storedPassword);
    }

    // Validate password strength
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasUpperCase && hasLowerCase && hasDigit;
    }

}
