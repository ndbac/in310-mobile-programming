package com.example.unimate.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    
    @PrimaryKey
    @NonNull
    private String username;
    
    @NonNull
    private String password;
    
    private String email;
    
    public User(@NonNull String username, @NonNull String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    @NonNull
    public String getUsername() {
        return username;
    }
    
    public void setUsername(@NonNull String username) {
        this.username = username;
    }
    
    @NonNull
    public String getPassword() {
        return password;
    }
    
    public void setPassword(@NonNull String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 