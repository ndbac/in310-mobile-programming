package com.example.unimate.data;

import android.content.Context;

import java.util.List;

public class TimerRepository {
    
    private TimerDao timerDao;
    
    public TimerRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        timerDao = database.timerDao();
    }
    
    public void insertTimer(Timer timer) {
        timerDao.insert(timer);
    }
    
    public void updateTimer(Timer timer) {
        timerDao.update(timer);
    }
    
    public void deleteTimer(Timer timer) {
        timerDao.delete(timer);
    }
    
    public List<Timer> getAllTimersForUser(String username) {
        return timerDao.getAllTimersForUser(username);
    }
    
    public List<Timer> getActiveTimersForUser(String username) {
        return timerDao.getActiveTimersForUser(username);
    }
    
    public List<Timer> getCompletedTimersForUser(String username) {
        return timerDao.getCompletedTimersForUser(username);
    }
    
    public Timer getTimerById(int id) {
        return timerDao.getTimerById(id);
    }
    
    public void updateTimerStatus(int id, boolean isActive, long startedAt) {
        timerDao.updateTimerStatus(id, isActive, startedAt);
    }
    
    public void markTimerCompleted(int id, boolean isCompleted) {
        timerDao.markTimerCompleted(id, isCompleted);
    }
    
    public void deleteAllTimersForUser(String username) {
        timerDao.deleteAllTimersForUser(username);
    }
    
    public List<Timer> getAllActiveTimers() {
        return timerDao.getAllActiveTimers();
    }
} 