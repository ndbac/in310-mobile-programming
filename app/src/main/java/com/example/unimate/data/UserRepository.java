package com.example.unimate.data;

import android.app.Application;

public class UserRepository {
    
    private final UserDao userDao;
    
    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }
    
    public boolean registerUser(String username, String password, String email) {
        try {
            User existingUser = userDao.getUserByUsername(username);
            if (existingUser != null) {
                return false; // User already exists
            }
            
            User newUser = new User(username, password, email);
            userDao.insert(newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public User loginUser(String username, String password) {
        return userDao.getUser(username, password);
    }
    
    public boolean isFirstUser() {
        return userDao.getUserCount() == 0;
    }
} 