package com.adminpanel;

/**
 * Main entry point for the Admin Panel application.
 * This launches the Login screen which then routes to either
 * DashboardApp (for admins) or ClientApp (for customers).
 */
public class DashboardApp {
    public static void main(String[] args) {
        com.adminpanel.auth.LoginApp.main(args);
    }
}
