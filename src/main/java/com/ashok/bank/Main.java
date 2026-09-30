package com.ashok.bank;

import com.ashok.bank.auth.LoginUI;
import com.ashok.bank.auth.UserSession;
import com.ashok.bank.constants.AppConstants;
import com.ashok.bank.customer.CustomerModule;
import com.ashok.bank.manager.ManagerModule;
import com.ashok.bank.teller.TellerModule;
import com.ashok.bank.util.DatabaseConnection;
import com.ashok.bank.util.InputUtil;
import com.ashok.bank.exception.DatabaseException;

public class Main {
    public static void main(String[] args) {
        try {
            // Test database connection
            System.out.println("\nInitializing Bank Management System...");
            DatabaseConnection.testConnection();

            // Display welcome banner
            displayWelcomeBanner();

            // Main application loop
            boolean keepRunning = true;
            LoginUI loginUI = new LoginUI();
            UserSession userSession = UserSession.getInstance();

            while (keepRunning) {
                if (!userSession.isLoggedIn()) {
                    // Login screen
                    displayLoginMenu();
                    int choice = readMenuChoice();

                    switch (choice) {
                        case 1 -> {
                            if (loginUI.login()) {
                                routeToModule(userSession);
                            }
                        }
                        case 2 -> {
                            keepRunning = false;
                            displayGoodbye();
                        }
                        default -> InputUtil.printError("Invalid choice. Please try again.");
                    }
                } else {
                    // If user session is expired, log them out
                    if (userSession.isSessionExpired(AppConstants.SESSION_TIMEOUT)) {
                        InputUtil.printWarning("Your session has expired. Please login again.");
                        loginUI.logout();
                    }
                }
            }

        } catch (DatabaseException e) {
            InputUtil.printError("Database Connection Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            InputUtil.printError("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                DatabaseConnection.closeConnection();
                InputUtil.closeScanner();
            } catch (DatabaseException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private static void displayWelcomeBanner() {
        InputUtil.printSeparator();
        System.out.println("    ASHOK BANK OF INDIA (ABI)");
        System.out.println("    CONSOLE BANKING SYSTEM");
        System.out.println("    Version 1.0");
        InputUtil.printSeparator();
    }

    private static void displayLoginMenu() {
        InputUtil.printDottedSeparator();
        System.out.println("    MAIN MENU");
        InputUtil.printDottedSeparator();
        System.out.println("1. Login");
        System.out.println("2. Exit");
        InputUtil.printDottedSeparator();
    }

    private static int readMenuChoice() {
        return InputUtil.readInt("Select an option: ");
    }

    private static void routeToModule(UserSession userSession) {
        String role = userSession.getUserRole();

        try {
            switch (role) {
                case AppConstants.ROLE_CUSTOMER -> {
                    System.out.println("\n✓ Welcome, " + userSession.getUsername() + "!");
                    CustomerModule customerModule = new CustomerModule();
                    customerModule.start();
                    userSession.logout();
                }
                case AppConstants.ROLE_TELLER -> {
                    System.out.println("\n✓ Welcome, Teller " + userSession.getUsername() + "!");
                    TellerModule tellerModule = new TellerModule();
                    tellerModule.start();
                    userSession.logout();
                }
                case AppConstants.ROLE_MANAGER -> {
                    System.out.println("\n✓ Welcome, Manager " + userSession.getUsername() + "!");
                    ManagerModule managerModule = new ManagerModule();
                    managerModule.start();
                    userSession.logout();
                }
                case AppConstants.ROLE_ADMIN -> {
                    System.out.println("\n✓ Welcome, Admin " + userSession.getUsername() + "!");
                    InputUtil.printWarning("Admin panel not yet implemented.");
                    userSession.logout();
                }
                default -> InputUtil.printError("Unknown role: " + role);
            }
        } catch (Exception e) {
            InputUtil.printError("Error: " + e.getMessage());
            userSession.logout();
        }
    }

    private static void displayGoodbye() {
        InputUtil.printSeparator();
        System.out.println("    Thank you for using Ashok Bank!");
        System.out.println("    Have a great day!");
        InputUtil.printSeparator();
    }
}
