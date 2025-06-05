package com.example.unimate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unimate.data.User;
import com.example.unimate.data.UserRepository;

public class AuthViewModel extends AndroidViewModel {
    
    private final UserRepository repository;
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }
    
    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Username and password cannot be empty");
            return;
        }
        
        User user = repository.loginUser(username, password);
        if (user != null) {
            loggedInUser.setValue(user);
        } else {
            errorMessage.setValue("Invalid username or password");
        }
    }
    
    public void register(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Username and password cannot be empty");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords don't match");
            return;
        }
        
        boolean registered = repository.registerUser(username, password, email);
        if (registered) {
            User user = new User(username, password, email);
            loggedInUser.setValue(user);
        } else {
            errorMessage.setValue("Username already exists");
        }
    }
    
    public boolean isFirstTimeUser() {
        return repository.isFirstUser();
    }
} 