package com.adminpanel.util;

import com.adminpanel.model.User;

/**
 * Holds the current application state including the logged-in user.
 */
public class ApplicationState {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
