package com.example.unimate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.unimate.databinding.ActivityLoginBinding;
import com.example.unimate.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Check if first-time user, redirect to register
        if (authViewModel.isFirstTimeUser()) {
            navigateToRegister();
        }
        
        setupObservers();
        setupClickListeners();
    }
    
    private void setupObservers() {
        authViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                // User logged in successfully
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                
                // Save current user in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().putString("current_user", user.getUsername()).apply();
                
                // Navigate to main activity
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("USER_NAME", user.getUsername());
                startActivity(intent);
                finish();
            }
        });
        
        authViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        
        binding.tvRegister.setOnClickListener(v -> navigateToRegister());
    }
    
    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        authViewModel.login(username, password);
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
} 