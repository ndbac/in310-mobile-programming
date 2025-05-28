package com.example.unimate.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions")
public class StudySession {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String username; // To associate sessions with users
    
    @NonNull
    private String subject; // Subject being studied
    
    private String description; // Optional description/notes
    
    private long startTime; // When the session started
    
    private long endTime; // When the session ended
    
    private long durationMinutes; // Duration in minutes
    
    private String sessionType; // "timer", "manual", "pomodoro"
    
    private int rating; // User rating 1-5 stars for session quality
    
    private boolean isCompleted; // Whether the session was completed or interrupted
    
    public StudySession(@NonNull String username, @NonNull String subject, String description, 
                       long startTime, long endTime, long durationMinutes, String sessionType) {
        this.username = username;
        this.subject = subject;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.rating = 0;
        this.isCompleted = true;
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
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(@NonNull String subject) {
        this.subject = subject;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public long getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getSessionType() {
        return sessionType;
    }
    
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    // Helper methods
    public String getFormattedDuration() {
        if (durationMinutes < 60) {
            return durationMinutes + " min";
        } else {
            long hours = durationMinutes / 60;
            long minutes = durationMinutes % 60;
            return hours + "h " + minutes + "m";
        }
    }
    
    public String getDateString() {
        return android.text.format.DateFormat.format("MMM dd, yyyy", startTime).toString();
    }
    
    public String getTimeString() {
        return android.text.format.DateFormat.format("HH:mm", startTime).toString();
    }
} 