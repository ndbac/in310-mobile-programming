package com.example.unimate.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);
    
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUser(String username, String password);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
} 