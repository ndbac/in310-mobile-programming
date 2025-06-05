package com.example.unimate.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timers")
public class Timer {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String username; // To associate timers with users
    
    @NonNull
    private String title;
    
    private String description;
    
    private long durationMinutes; // Timer duration in minutes
    
    private long createdAt;
    
    private long startedAt; // When the timer was started (0 if not started)
    
    private boolean isActive; // Whether the timer is currently running
    
    private boolean isCompleted; // Whether the timer has finished
    
    public Timer(@NonNull String username, @NonNull String title, String description, long durationMinutes) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdAt = System.currentTimeMillis();
        this.startedAt = 0;
        this.isActive = false;
        this.isCompleted = false;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @NonNull
    public String getUsername() {
        return username;
    }
    
    public void setUsername(@NonNull String username) {
        this.username = username;
    }
    
    @NonNull
    public String getTitle() {
        return title;
    }
    
    public void setTitle(@NonNull String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    // Helper methods
    public long getDurationMillis() {
        return durationMinutes * 60 * 1000;
    }
    
    public long getRemainingTimeMillis() {
        if (!isActive || startedAt == 0) {
            return getDurationMillis();
        }
        
        long elapsed = System.currentTimeMillis() - startedAt;
        long remaining = getDurationMillis() - elapsed;
        return Math.max(0, remaining);
    }
    
    public boolean isExpired() {
        return isActive && getRemainingTimeMillis() <= 0;
    }
} 