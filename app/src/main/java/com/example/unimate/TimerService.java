package com.example.unimate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.unimate.data.Timer;
import com.example.unimate.data.TimerRepository;
import com.example.unimate.data.StudySession;
import com.example.unimate.data.StudySessionRepository;

import java.util.List;
import java.util.Locale;

public class TimerService extends Service {
    
    private static final String CHANNEL_ID = "timer_channel";
    private static final String COMPLETION_CHANNEL_ID = "timer_completion_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int TIMER_COMPLETE_NOTIFICATION_ID = 2;
    
    private TimerRepository timerRepository;
    private StudySessionRepository studySessionRepository;
    private Handler handler;
    private Runnable timerRunnable;
    private NotificationManager notificationManager;
    private Vibrator vibrator;
    
    @Override
    public void onCreate() {
        super.onCreate();
        timerRepository = new TimerRepository(this);
        studySessionRepository = new StudySessionRepository(this);
        handler = new Handler(Looper.getMainLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        
        createNotificationChannels();
        startTimerUpdates();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createOngoingNotification());
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
    
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Ongoing timer channel
            NotificationChannel ongoingChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Running",
                    NotificationManager.IMPORTANCE_LOW
            );
            ongoingChannel.setDescription("Shows when timers are running");
            ongoingChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(ongoingChannel);
            
            // Timer completion channel
            NotificationChannel completionChannel = new NotificationChannel(
                    COMPLETION_CHANNEL_ID,
                    "Timer Completed",
                    NotificationManager.IMPORTANCE_HIGH
            );
            completionChannel.setDescription("Notifications when timers complete");
            completionChannel.enableVibration(true);
            completionChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 500, 1000});
            completionChannel.enableLights(true);
            completionChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(completionChannel);
        }
    }
    
    private Notification createOngoingNotification() {
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Study Timer Running")
                .setContentText("Tap to view active timers")
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .build();
    }
    
    private void startTimerUpdates() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                checkActiveTimers();
                handler.postDelayed(this, 1000); // Check every second
            }
        };
        handler.post(timerRunnable);
    }
    
    private void checkActiveTimers() {
        List<Timer> activeTimers = timerRepository.getAllActiveTimers();
        
        boolean hasActiveTimers = false;
        for (Timer timer : activeTimers) {
            if (timer.isActive()) {
                // Check if timer has expired
                if (timer.isExpired()) {
                    // Mark timer as completed
                    timer.setCompleted(true);
                    timer.setActive(false);
                    timerRepository.updateTimer(timer);
                    
                    // Create study session for completed timer
                    createStudySessionFromTimer(timer);
                    
                    // Send completion notification with sound and vibration
                    sendTimerCompleteNotification(timer);
                    
                    // Trigger vibration
                    triggerVibration();
                } else {
                    hasActiveTimers = true;
                }
            }
        }
        
        // Stop service if no active timers
        if (!hasActiveTimers) {
            stopSelf();
        } else {
            // Update ongoing notification with current timer info
            updateOngoingNotification(activeTimers);
        }
    }
    
    private void createStudySessionFromTimer(Timer timer) {
        try {
            // Calculate actual study time (from start to completion)
            long actualDuration = timer.getDurationMinutes();
            long startTime = timer.getStartedAt();
            long endTime = System.currentTimeMillis();
            
            // Create study session
            StudySession session = new StudySession(
                timer.getUsername(),
                timer.getTitle(), // Use timer title as subject
                timer.getDescription(),
                startTime,
                endTime,
                actualDuration,
                "timer" // Session type
            );
            
            // Insert the study session
            studySessionRepository.insertSession(session);
        } catch (Exception e) {
            // Log error but don't crash the service
            e.printStackTrace();
        }
    }
    
    private void updateOngoingNotification(List<Timer> activeTimers) {
        if (activeTimers.isEmpty()) return;
        
        // Filter out expired timers
        activeTimers.removeIf(Timer::isExpired);
        if (activeTimers.isEmpty()) return;
        
        Timer firstTimer = activeTimers.get(0);
        long remainingTime = firstTimer.getRemainingTimeMillis();
        String timeDisplay = formatTime(remainingTime);
        
        String contentText = activeTimers.size() == 1 
                ? firstTimer.getTitle() + " - " + timeDisplay
                : activeTimers.size() + " timers running";
        
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Study Timer Running")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .build();
        
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    private void sendTimerCompleteNotification(Timer timer) {
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Get default notification sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        Notification notification = new NotificationCompat.Builder(this, COMPLETION_CHANNEL_ID)
                .setContentTitle("🎉 Timer Complete!")
                .setContentText(timer.getTitle() + " has finished - Great job!")
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000})
                .setLights(0xFF00FF00, 1000, 1000) // Green light
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(timer.getTitle() + " has finished!\n\nDuration: " + timer.getDurationMinutes() + " minutes\n\nGreat work on your study session!"))
                .build();
        
        // Use unique notification ID for each timer completion
        int notificationId = TIMER_COMPLETE_NOTIFICATION_ID + timer.getId();
        notificationManager.notify(notificationId, notification);
    }
    
    private void triggerVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create vibration pattern for completion
                long[] pattern = {0, 500, 200, 500, 200, 1000};
                VibrationEffect effect = VibrationEffect.createWaveform(pattern, -1);
                vibrator.vibrate(effect);
            } else {
                // Fallback for older devices
                long[] pattern = {0, 500, 200, 500, 200, 1000};
                vibrator.vibrate(pattern, -1);
            }
        }
    }
    
    private String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
} 