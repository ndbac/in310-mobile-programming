package com.example.unimate.data;

import android.content.Context;

import java.util.List;

public class StudySessionRepository {
    
    private StudySessionDao studySessionDao;
    
    public StudySessionRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        studySessionDao = database.studySessionDao();
    }
    
    public void insertSession(StudySession session) {
        studySessionDao.insert(session);
    }
    
    public void updateSession(StudySession session) {
        studySessionDao.update(session);
    }
    
    public void deleteSession(StudySession session) {
        studySessionDao.delete(session);
    }
    
    public List<StudySession> getAllSessionsForUser(String username) {
        return studySessionDao.getAllSessionsForUser(username);
    }
    
    public List<StudySession> getSessionsForUserInDateRange(String username, long startDate, long endDate) {
        return studySessionDao.getSessionsForUserInDateRange(username, startDate, endDate);
    }
    
    public List<StudySession> getSessionsForSubject(String username, String subject) {
        return studySessionDao.getSessionsForSubject(username, subject);
    }
    
    public List<String> getSubjectsForUser(String username) {
        return studySessionDao.getSubjectsForUser(username);
    }
    
    public Long getTotalStudyTimeInDateRange(String username, long startDate, long endDate) {
        Long result = studySessionDao.getTotalStudyTimeInDateRange(username, startDate, endDate);
        return result != null ? result : 0L;
    }
    
    public Long getTotalStudyTimeForSubject(String username, String subject) {
        Long result = studySessionDao.getTotalStudyTimeForSubject(username, subject);
        return result != null ? result : 0L;
    }
    
    public int getSessionCountInDateRange(String username, long startDate, long endDate) {
        return studySessionDao.getSessionCountInDateRange(username, startDate, endDate);
    }
    
    public Double getAverageRating(String username) {
        Double result = studySessionDao.getAverageRating(username);
        return result != null ? result : 0.0;
    }
    
    public List<StudySession> getRecentSessions(String username, int limit) {
        return studySessionDao.getRecentSessions(username, limit);
    }
    
    public void deleteAllSessionsForUser(String username) {
        studySessionDao.deleteAllSessionsForUser(username);
    }
    
    public List<StudySessionDao.SubjectStats> getSubjectStats(String username) {
        return studySessionDao.getSubjectStats(username);
    }
    
    public List<StudySessionDao.DailyStats> getDailyStats(String username, long startDate) {
        return studySessionDao.getDailyStats(username, startDate);
    }
} 