package com.exam.utils;

import com.exam.models.User;

public class SessionManager {

    // Single instance shared across the whole application
    private static SessionManager instance;

    // The currently logged-in user (null if nobody is logged in)
    private User currentUser;

    // Private constructor — nobody can call new SessionManager()
    private SessionManager() {}

    // Returns the one and only SessionManager instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Called after successful login — stores the user
    public void login(User user) {
        this.currentUser = user;
    }

    // Called on logout — clears the stored user
    public void logout() {
        this.currentUser = null;
    }

    // Returns the logged-in user object (or null if not logged in)
    public User getCurrentUser() {
        return currentUser;
    }

    // Returns true if someone is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Returns true if the logged-in user is an admin
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole().equals("admin");
    }
}