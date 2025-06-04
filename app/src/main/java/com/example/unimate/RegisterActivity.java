package com.example.unimate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.unimate.databinding.ActivityRegisterBinding;
import com.example.unimate.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    
    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        setupObservers();
        setupClickListeners();
    }
    
    private void setupObservers() {
        authViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                // User registered successfully
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                
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
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        
        binding.tvLogin.setOnClickListener(v -> navigateToLogin());
    }
    
    private void attemptRegister() {
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        
        authViewModel.register(username, password, confirmPassword, email);
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
} 