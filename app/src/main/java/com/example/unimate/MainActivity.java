package com.example.unimate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unimate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Get username from intent
        if (getIntent() != null && getIntent().hasExtra("USER_NAME")) {
            username = getIntent().getStringExtra("USER_NAME");
            binding.tvWelcomeMessage.setText("Welcome, " + username + "!");
        } else {
            // If no user is logged in, go to login screen
            navigateToLogin();
        }
        
        // Set up click listeners for menu items
        setupMenuListeners();
    }
    
    private void setupMenuListeners() {
        // Timer card click listener
        binding.cardTimer.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimerActivity.class);
            startActivity(intent);
        });
        
        // To-Do list card click listener
        binding.cardTodo.setOnClickListener(v -> {
            Intent intent = new Intent(this, TodoActivity.class);
            startActivity(intent);
        });
        
        // Dictionary card click listener
        binding.cardDictionary.setOnClickListener(v -> {
            Intent intent = new Intent(this, DictionaryActivity.class);
            startActivity(intent);
        });
        
        // Study Tracking card click listener
        binding.cardStudyTracking.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudyTrackingActivity.class);
            startActivity(intent);
        });
        
        // Logout button click listener
        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            logout();
        });
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void logout() {
        // Clear saved login state
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        // Navigate back to login screen
        navigateToLogin();
    }
}