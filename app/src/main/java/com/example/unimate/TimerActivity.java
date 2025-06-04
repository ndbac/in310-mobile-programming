package com.example.unimate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.Timer;
import com.example.unimate.data.TimerRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class TimerActivity extends AppCompatActivity implements TimerAdapter.OnTimerItemClickListener {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    
    private RecyclerView recyclerTimers;
    private View layoutEmptyState;
    private TimerAdapter timerAdapter;
    private TimerRepository timerRepository;
    private String username;
    
    // Filter buttons
    private Button btnAllTimers, btnActiveTimers, btnCompletedTimers;
    private FilterType currentFilter = FilterType.ALL;
    
    private enum FilterType {
        ALL, ACTIVE, COMPLETED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        
        // Get username from SharedPreferences
        username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("current_user", "");
        
        if (username.isEmpty()) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Request notification permission for Android 13+
        requestNotificationPermission();
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadTimers();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadTimers(); // Refresh timers when returning to activity
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerAdapter != null) {
            timerAdapter.stopUpdating();
        }
    }
    
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. You won't receive timer completion alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void initViews() {
        recyclerTimers = findViewById(R.id.recycler_timers);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        btnAllTimers = findViewById(R.id.btn_all_timers);
        btnActiveTimers = findViewById(R.id.btn_active_timers);
        btnCompletedTimers = findViewById(R.id.btn_completed_timers);
        
        timerRepository = new TimerRepository(this);
    }
    
    private void setupRecyclerView() {
        timerAdapter = new TimerAdapter(this);
        recyclerTimers.setLayoutManager(new LinearLayoutManager(this));
        recyclerTimers.setAdapter(timerAdapter);
    }
    
    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Add timer button
        findViewById(R.id.btn_add_timer).setOnClickListener(v -> showAddTimerDialog());
        
        // Filter buttons
        btnAllTimers.setOnClickListener(v -> setFilter(FilterType.ALL));
        btnActiveTimers.setOnClickListener(v -> setFilter(FilterType.ACTIVE));
        btnCompletedTimers.setOnClickListener(v -> setFilter(FilterType.COMPLETED));
        
        // Quick timer buttons
        findViewById(R.id.btn_quick_5).setOnClickListener(v -> createQuickTimer(5));
        findViewById(R.id.btn_quick_15).setOnClickListener(v -> createQuickTimer(15));
        findViewById(R.id.btn_quick_25).setOnClickListener(v -> createQuickTimer(25));
        findViewById(R.id.btn_quick_60).setOnClickListener(v -> createQuickTimer(60));
    }
    
    private void setFilter(FilterType filterType) {
        currentFilter = filterType;
        
        // Update button colors
        resetFilterButtonColors();
        switch (filterType) {
            case ALL:
                btnAllTimers.setTextColor(getColor(R.color.black));
                break;
            case ACTIVE:
                btnActiveTimers.setTextColor(getColor(R.color.black));
                break;
            case COMPLETED:
                btnCompletedTimers.setTextColor(getColor(R.color.black));
                break;
        }
        
        loadTimers();
    }
    
    private void resetFilterButtonColors() {
        int defaultColor = getColor(android.R.color.darker_gray);
        btnAllTimers.setTextColor(defaultColor);
        btnActiveTimers.setTextColor(defaultColor);
        btnCompletedTimers.setTextColor(defaultColor);
    }
    
    private void loadTimers() {
        List<Timer> timers;
        
        switch (currentFilter) {
            case ACTIVE:
                timers = timerRepository.getActiveTimersForUser(username);
                break;
            case COMPLETED:
                timers = timerRepository.getCompletedTimersForUser(username);
                break;
            default:
                timers = timerRepository.getAllTimersForUser(username);
                break;
        }
        
        timerAdapter.setTimerList(timers);
        
        // Show/hide empty state
        if (timers.isEmpty()) {
            recyclerTimers.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerTimers.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
    
    private void createQuickTimer(int minutes) {
        String title = minutes + " Minute Timer";
        Timer newTimer = new Timer(username, title, "Quick study session", minutes);
        timerRepository.insertTimer(newTimer);
        
        // If we're showing all or active timers, add to the adapter
        if (currentFilter == FilterType.ALL || currentFilter == FilterType.ACTIVE) {
            timerAdapter.addTimer(newTimer);
            recyclerTimers.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
        
        Toast.makeText(this, "Timer created: " + title, Toast.LENGTH_SHORT).show();
    }
    
    private void showAddTimerDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_timer, null);
        TextInputEditText etTitle = dialogView.findViewById(R.id.et_timer_title);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_timer_description);
        SeekBar seekBarDuration = dialogView.findViewById(R.id.seekbar_duration);
        TextView tvDurationDisplay = dialogView.findViewById(R.id.tv_duration_display);
        
        // Set up duration seekbar
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = Math.max(1, progress); // Minimum 1 minute
                tvDurationDisplay.setText(minutes + " min");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        dialogView.findViewById(R.id.btn_cancel_timer).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btn_create_timer).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int duration = Math.max(1, seekBarDuration.getProgress());
            
            if (title.isEmpty()) {
                etTitle.setError("Timer name is required");
                return;
            }
            
            Timer newTimer = new Timer(username, title, description, duration);
            timerRepository.insertTimer(newTimer);
            
            // If we're showing all or active timers, add to the adapter
            if (currentFilter == FilterType.ALL || currentFilter == FilterType.ACTIVE) {
                timerAdapter.addTimer(newTimer);
                recyclerTimers.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
            
            Toast.makeText(this, "Timer created successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    @Override
    public void onTimerStart(Timer timer) {
        timer.setActive(true);
        timer.setStartedAt(System.currentTimeMillis());
        timerRepository.updateTimer(timer);
        timerAdapter.updateTimer(timer);
        
        // Start timer service with API level compatibility
        Intent serviceIntent = new Intent(this, TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        
        Toast.makeText(this, "Timer started: " + timer.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTimerPause(Timer timer) {
        // Calculate remaining time and update duration
        long remainingTime = timer.getRemainingTimeMillis();
        long remainingMinutes = Math.max(1, remainingTime / (60 * 1000));
        
        timer.setActive(false);
        timer.setStartedAt(0);
        timer.setDurationMinutes(remainingMinutes);
        timerRepository.updateTimer(timer);
        timerAdapter.updateTimer(timer);
        
        Toast.makeText(this, "Timer paused: " + timer.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTimerStop(Timer timer) {
        timer.setActive(false);
        timer.setStartedAt(0);
        timerRepository.updateTimer(timer);
        timerAdapter.updateTimer(timer);
        
        Toast.makeText(this, "Timer stopped: " + timer.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTimerDelete(Timer timer) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Timer")
                .setMessage("Are you sure you want to delete this timer?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    timerRepository.deleteTimer(timer);
                    timerAdapter.removeTimer(timer);
                    
                    // Check if list is now empty
                    if (timerAdapter.getItemCount() == 0) {
                        recyclerTimers.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                    
                    Toast.makeText(this, "Timer deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onTimerCompleted(Timer timer) {
        // Update timer in database
        timerRepository.updateTimer(timer);
        
        // Show completion message
        Toast.makeText(this, "🎉 " + timer.getTitle() + " completed!", Toast.LENGTH_LONG).show();
        
        // If we're currently viewing active timers and this timer completed,
        // we might need to refresh the list
        if (currentFilter == FilterType.ACTIVE) {
            loadTimers(); // Refresh to remove completed timer from active list
        }
    }
} 