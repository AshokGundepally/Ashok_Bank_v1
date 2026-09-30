package com.ashok.bank.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readStringRequired(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("✗ Input cannot be empty. Please try again.");
        }
    }

    public static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ Input cannot be empty.");
                    continue;
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ Input cannot be empty.");
                    continue;
                }
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("✗ Amount cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid amount.");
            }
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ Input cannot be empty.");
                    continue;
                }
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("✗ Value cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            } else {
                System.out.println("✗ Please enter Y or N.");
            }
        }
    }

    public static String readEmail(String prompt) {
        while (true) {
            String email = readStringRequired(prompt);
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return email;
            }
            System.out.println("✗ Invalid email format.");
        }
    }

    public static String readPhoneNumber(String prompt) {
        while (true) {
            String phone = readStringRequired(prompt);
            if (phone.matches("\\d{10}")) {
                return phone;
            }
            System.out.println("✗ Phone number must be 10 digits.");
        }
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public static void printSeparator() {
        System.out.println("====================================================================");
    }

    public static void printDottedSeparator() {
        System.out.println("--------------------------------------------------------------------");
    }

    public static void printSuccess(String message) {
        System.out.println("✓ " + message);
    }

    public static void printError(String message) {
        System.out.println("✗ " + message);
    }

    public static void printWarning(String message) {
        System.out.println("⚠ " + message);
    }

    public static void printInfo(String message) {
        System.out.println("ℹ " + message);
    }
}
