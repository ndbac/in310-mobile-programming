package com.example.unimate.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudySessionDao {
    
    @Insert
    void insert(StudySession session);
    
    @Update
    void update(StudySession session);
    
    @Delete
    void delete(StudySession session);
    
    @Query("SELECT * FROM study_sessions WHERE username = :username ORDER BY startTime DESC")
    List<StudySession> getAllSessionsForUser(String username);
    
    @Query("SELECT * FROM study_sessions WHERE username = :username AND startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    List<StudySession> getSessionsForUserInDateRange(String username, long startDate, long endDate);
    
    @Query("SELECT * FROM study_sessions WHERE username = :username AND subject = :subject ORDER BY startTime DESC")
    List<StudySession> getSessionsForSubject(String username, String subject);
    
    @Query("SELECT DISTINCT subject FROM study_sessions WHERE username = :username ORDER BY subject ASC")
    List<String> getSubjectsForUser(String username);
    
    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE username = :username AND startTime >= :startDate AND startTime <= :endDate")
    Long getTotalStudyTimeInDateRange(String username, long startDate, long endDate);
    
    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE username = :username AND subject = :subject")
    Long getTotalStudyTimeForSubject(String username, String subject);
    
    @Query("SELECT COUNT(*) FROM study_sessions WHERE username = :username AND startTime >= :startDate AND startTime <= :endDate")
    int getSessionCountInDateRange(String username, long startDate, long endDate);
    
    @Query("SELECT AVG(rating) FROM study_sessions WHERE username = :username AND rating > 0")
    Double getAverageRating(String username);
    
    @Query("SELECT * FROM study_sessions WHERE username = :username ORDER BY startTime DESC LIMIT :limit")
    List<StudySession> getRecentSessions(String username, int limit);
    
    @Query("DELETE FROM study_sessions WHERE username = :username")
    void deleteAllSessionsForUser(String username);
    
    // Statistics queries
    @Query("SELECT subject, SUM(durationMinutes) as totalTime FROM study_sessions WHERE username = :username GROUP BY subject ORDER BY totalTime DESC")
    List<SubjectStats> getSubjectStats(String username);
    
    @Query("SELECT DATE(startTime/1000, 'unixepoch') as date, SUM(durationMinutes) as totalTime FROM study_sessions WHERE username = :username AND startTime >= :startDate GROUP BY DATE(startTime/1000, 'unixepoch') ORDER BY date DESC")
    List<DailyStats> getDailyStats(String username, long startDate);
    
    // Inner classes for statistics
    class SubjectStats {
        public String subject;
        public long totalTime;
    }
    
    class DailyStats {
        public String date;
        public long totalTime;
    }
} 