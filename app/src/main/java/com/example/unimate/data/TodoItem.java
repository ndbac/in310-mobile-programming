package com.example.unimate.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_items")
public class TodoItem {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String username; // To associate todos with users
    
    @NonNull
    private String title;
    
    private String description;
    
    private boolean isCompleted;
    
    private long createdAt;
    
    private long dueDate; // Optional due date (timestamp)
    
    public TodoItem(@NonNull String username, @NonNull String title, String description) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
        this.dueDate = 0; // No due date by default
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
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
} 