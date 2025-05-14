package com.example.unimate;

import android.content.Intent;
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
        
        // Set up logout button click listener
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
        // Clear any saved login state if needed
        // ...
        
        // Navigate back to login screen
        navigateToLogin();
    }
}