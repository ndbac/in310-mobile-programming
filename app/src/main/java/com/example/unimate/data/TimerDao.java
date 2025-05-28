package com.example.unimate.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimerDao {
    
    @Insert
    void insert(Timer timer);
    
    @Update
    void update(Timer timer);
    
    @Delete
    void delete(Timer timer);
    
    @Query("SELECT * FROM timers WHERE username = :username ORDER BY createdAt DESC")
    List<Timer> getAllTimersForUser(String username);
    
    @Query("SELECT * FROM timers WHERE username = :username AND isActive = 1")
    List<Timer> getActiveTimersForUser(String username);
    
    @Query("SELECT * FROM timers WHERE username = :username AND isCompleted = 1 ORDER BY createdAt DESC")
    List<Timer> getCompletedTimersForUser(String username);
    
    @Query("SELECT * FROM timers WHERE id = :id")
    Timer getTimerById(int id);
    
    @Query("DELETE FROM timers WHERE username = :username")
    void deleteAllTimersForUser(String username);
    
    @Query("UPDATE timers SET isActive = :isActive, startedAt = :startedAt WHERE id = :id")
    void updateTimerStatus(int id, boolean isActive, long startedAt);
    
    @Query("UPDATE timers SET isCompleted = :isCompleted, isActive = 0 WHERE id = :id")
    void markTimerCompleted(int id, boolean isCompleted);
    
    @Query("SELECT * FROM timers WHERE isActive = 1")
    List<Timer> getAllActiveTimers();
} 