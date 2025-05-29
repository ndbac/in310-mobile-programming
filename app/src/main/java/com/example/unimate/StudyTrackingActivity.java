package com.example.unimate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.StudySession;
import com.example.unimate.data.StudySessionRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudyTrackingActivity extends AppCompatActivity implements 
        StudySessionAdapter.OnSessionItemClickListener, SubjectAdapter.OnSubjectClickListener {

    private String username;
    private StudySessionRepository sessionRepository;
    
    // UI Components
    private Button btnTabSessions, btnTabStatistics, btnTabSubjects;
    private View layoutSessionsTab, layoutStatisticsTab, layoutSubjectsTab;
    
    // Sessions Tab
    private RecyclerView recyclerSessions;
    private View layoutSessionsEmpty;
    private StudySessionAdapter sessionAdapter;
    private Spinner spinnerTimeFilter, spinnerSubjectFilter;
    
    // Statistics Tab
    private TextView tvTotalTime, tvSessionCount, tvAverageRating;
    private RatingBar ratingAverage;
    private Button btnPeriodWeek, btnPeriodMonth, btnPeriodAll;
    
    // Subjects Tab
    private RecyclerView recyclerSubjects;
    private View layoutSubjectsEmpty;
    private SubjectAdapter subjectAdapter;
    
    // Current filters and state
    private TabType currentTab = TabType.SESSIONS;
    private StatsPeriod currentPeriod = StatsPeriod.WEEK;
    private String currentSubjectFilter = "All Subjects";
    private String currentTimeFilter = "All Time";
    
    private enum TabType {
        SESSIONS, STATISTICS, SUBJECTS
    }
    
    private enum StatsPeriod {
        WEEK, MONTH, ALL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_tracking);
        
        // Get username from SharedPreferences
        username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("current_user", "");
        
        if (username.isEmpty()) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        sessionRepository = new StudySessionRepository(this);
        
        initViews();
        setupClickListeners();
        setupRecyclerViews();
        setupFilters();
        loadData();
    }
    
    private void initViews() {
        // Tab buttons
        btnTabSessions = findViewById(R.id.btn_tab_sessions);
        btnTabStatistics = findViewById(R.id.btn_tab_statistics);
        btnTabSubjects = findViewById(R.id.btn_tab_subjects);
        
        // Tab content layouts
        layoutSessionsTab = findViewById(R.id.layout_sessions_tab);
        layoutStatisticsTab = findViewById(R.id.layout_statistics_tab);
        layoutSubjectsTab = findViewById(R.id.layout_subjects_tab);
        
        // Sessions tab components
        recyclerSessions = findViewById(R.id.recycler_sessions);
        layoutSessionsEmpty = findViewById(R.id.layout_sessions_empty);
        spinnerTimeFilter = findViewById(R.id.spinner_time_filter);
        spinnerSubjectFilter = findViewById(R.id.spinner_subject_filter);
        
        // Statistics tab components
        tvTotalTime = findViewById(R.id.tv_total_time);
        tvSessionCount = findViewById(R.id.tv_session_count);
        tvAverageRating = findViewById(R.id.tv_average_rating);
        ratingAverage = findViewById(R.id.rating_average);
        btnPeriodWeek = findViewById(R.id.btn_period_week);
        btnPeriodMonth = findViewById(R.id.btn_period_month);
        btnPeriodAll = findViewById(R.id.btn_period_all);
        
        // Subjects tab components
        recyclerSubjects = findViewById(R.id.recycler_subjects);
        layoutSubjectsEmpty = findViewById(R.id.layout_subjects_empty);
    }
    
    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Add session button
        findViewById(R.id.btn_add_session).setOnClickListener(v -> showAddSessionDialog(null));
        
        // Tab buttons
        btnTabSessions.setOnClickListener(v -> switchTab(TabType.SESSIONS));
        btnTabStatistics.setOnClickListener(v -> switchTab(TabType.STATISTICS));
        btnTabSubjects.setOnClickListener(v -> switchTab(TabType.SUBJECTS));
        
        // Statistics period buttons
        btnPeriodWeek.setOnClickListener(v -> setStatsPeriod(StatsPeriod.WEEK));
        btnPeriodMonth.setOnClickListener(v -> setStatsPeriod(StatsPeriod.MONTH));
        btnPeriodAll.setOnClickListener(v -> setStatsPeriod(StatsPeriod.ALL));
    }
    
    private void setupRecyclerViews() {
        // Sessions RecyclerView
        sessionAdapter = new StudySessionAdapter(this);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSessions.setAdapter(sessionAdapter);
        
        // Subjects RecyclerView
        subjectAdapter = new SubjectAdapter(this);
        recyclerSubjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerSubjects.setAdapter(subjectAdapter);
    }
    
    private void setupFilters() {
        // Time filter spinner
        String[] timeFilters = {"All Time", "Today", "This Week", "This Month"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeFilters);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeFilter.setAdapter(timeAdapter);
        
        spinnerTimeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTimeFilter = timeFilters[position];
                loadSessions();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Subject filter will be populated when subjects are loaded
        updateSubjectFilter();
    }
    
    private void updateSubjectFilter() {
        List<String> subjects = sessionRepository.getSubjectsForUser(username);
        List<String> subjectFilters = new ArrayList<>();
        subjectFilters.add("All Subjects");
        subjectFilters.addAll(subjects);
        
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectFilters);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjectFilter.setAdapter(subjectAdapter);
        
        spinnerSubjectFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSubjectFilter = subjectFilters.get(position);
                loadSessions();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void switchTab(TabType tab) {
        currentTab = tab;
        
        // Reset tab button colors
        resetTabButtonColors();
        
        // Hide all tabs
        layoutSessionsTab.setVisibility(View.GONE);
        layoutStatisticsTab.setVisibility(View.GONE);
        layoutSubjectsTab.setVisibility(View.GONE);
        
        // Show selected tab and update button color
        switch (tab) {
            case SESSIONS:
                layoutSessionsTab.setVisibility(View.VISIBLE);
                btnTabSessions.setTextColor(getColor(R.color.black));
                loadSessions();
                break;
            case STATISTICS:
                layoutStatisticsTab.setVisibility(View.VISIBLE);
                btnTabStatistics.setTextColor(getColor(R.color.black));
                loadStatistics();
                break;
            case SUBJECTS:
                layoutSubjectsTab.setVisibility(View.VISIBLE);
                btnTabSubjects.setTextColor(getColor(R.color.black));
                loadSubjects();
                break;
        }
    }
    
    private void resetTabButtonColors() {
        int defaultColor = getColor(android.R.color.darker_gray);
        btnTabSessions.setTextColor(defaultColor);
        btnTabStatistics.setTextColor(defaultColor);
        btnTabSubjects.setTextColor(defaultColor);
    }
    
    private void setStatsPeriod(StatsPeriod period) {
        currentPeriod = period;
        
        // Reset period button colors
        int defaultColor = getColor(android.R.color.darker_gray);
        int selectedColor = getColor(R.color.black);
        
        btnPeriodWeek.setTextColor(defaultColor);
        btnPeriodMonth.setTextColor(defaultColor);
        btnPeriodAll.setTextColor(defaultColor);
        
        // Set selected button color
        switch (period) {
            case WEEK:
                btnPeriodWeek.setTextColor(selectedColor);
                break;
            case MONTH:
                btnPeriodMonth.setTextColor(selectedColor);
                break;
            case ALL:
                btnPeriodAll.setTextColor(selectedColor);
                break;
        }
        
        loadStatistics();
    }
    
    private void loadData() {
        switchTab(TabType.SESSIONS); // Start with sessions tab
    }
    
    private void loadSessions() {
        List<StudySession> sessions;
        
        // Apply filters
        if ("All Subjects".equals(currentSubjectFilter)) {
            sessions = getSessionsForTimeFilter();
        } else {
            sessions = sessionRepository.getSessionsForSubject(username, currentSubjectFilter);
            // Apply time filter to subject-filtered sessions
            sessions = filterSessionsByTime(sessions);
        }
        
        sessionAdapter.setSessionList(sessions);
        
        // Show/hide empty state
        if (sessions.isEmpty()) {
            recyclerSessions.setVisibility(View.GONE);
            layoutSessionsEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerSessions.setVisibility(View.VISIBLE);
            layoutSessionsEmpty.setVisibility(View.GONE);
        }
    }
    
    private List<StudySession> getSessionsForTimeFilter() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        long startTime;
        
        switch (currentTimeFilter) {
            case "Today":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                return sessionRepository.getSessionsForUserInDateRange(username, startTime, endTime);
                
            case "This Week":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                return sessionRepository.getSessionsForUserInDateRange(username, startTime, endTime);
                
            case "This Month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                return sessionRepository.getSessionsForUserInDateRange(username, startTime, endTime);
                
            default: // "All Time"
                return sessionRepository.getAllSessionsForUser(username);
        }
    }
    
    private List<StudySession> filterSessionsByTime(List<StudySession> sessions) {
        if ("All Time".equals(currentTimeFilter)) {
            return sessions;
        }
        
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        long startTime;
        
        switch (currentTimeFilter) {
            case "Today":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                break;
                
            case "This Week":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                break;
                
            case "This Month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startTime = cal.getTimeInMillis();
                break;
                
            default:
                return sessions;
        }
        
        List<StudySession> filteredSessions = new ArrayList<>();
        for (StudySession session : sessions) {
            if (session.getStartTime() >= startTime && session.getStartTime() <= endTime) {
                filteredSessions.add(session);
            }
        }
        
        return filteredSessions;
    }
    
    private void loadStatistics() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        long startTime;
        
        switch (currentPeriod) {
            case WEEK:
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                startTime = cal.getTimeInMillis();
                break;
            case MONTH:
                cal.add(Calendar.MONTH, -1);
                startTime = cal.getTimeInMillis();
                break;
            default: // ALL
                startTime = 0;
                break;
        }
        
        // Load statistics
        Long totalTime = sessionRepository.getTotalStudyTimeInDateRange(username, startTime, endTime);
        int sessionCount = sessionRepository.getSessionCountInDateRange(username, startTime, endTime);
        Double averageRating = sessionRepository.getAverageRating(username);
        
        // Update UI
        updateStatisticsUI(totalTime, sessionCount, averageRating);
    }
    
    private void updateStatisticsUI(Long totalTimeMinutes, int sessionCount, Double averageRating) {
        // Format total time
        String formattedTime;
        if (totalTimeMinutes < 60) {
            formattedTime = totalTimeMinutes + "m";
        } else {
            long hours = totalTimeMinutes / 60;
            long minutes = totalTimeMinutes % 60;
            if (minutes == 0) {
                formattedTime = hours + "h";
            } else {
                formattedTime = hours + "h " + minutes + "m";
            }
        }
        
        tvTotalTime.setText(formattedTime);
        tvSessionCount.setText(String.valueOf(sessionCount));
        
        // Update rating
        if (averageRating > 0) {
            ratingAverage.setRating(averageRating.floatValue());
            tvAverageRating.setText(String.format("%.1f", averageRating));
        } else {
            ratingAverage.setRating(0);
            tvAverageRating.setText("0.0");
        }
    }
    
    private void loadSubjects() {
        List<String> subjects = sessionRepository.getSubjectsForUser(username);
        List<SubjectAdapter.SubjectData> subjectDataList = new ArrayList<>();
        
        for (String subject : subjects) {
            Long totalTime = sessionRepository.getTotalStudyTimeForSubject(username, subject);
            List<StudySession> sessions = sessionRepository.getSessionsForSubject(username, subject);
            int sessionCount = sessions.size();
            
            subjectDataList.add(new SubjectAdapter.SubjectData(subject, totalTime, sessionCount));
        }
        
        subjectAdapter.setSubjectList(subjectDataList);
        
        // Show/hide empty state
        if (subjectDataList.isEmpty()) {
            recyclerSubjects.setVisibility(View.GONE);
            layoutSubjectsEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerSubjects.setVisibility(View.VISIBLE);
            layoutSubjectsEmpty.setVisibility(View.GONE);
        }
    }
    
    private void showAddSessionDialog(StudySession existingSession) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_study_session, null);
        
        // Get dialog components
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextInputEditText etSubject = dialogView.findViewById(R.id.et_session_subject);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_session_description);
        SeekBar seekBarDuration = dialogView.findViewById(R.id.seekbar_session_duration);
        TextView tvDurationDisplay = dialogView.findViewById(R.id.tv_duration_display);
        Button btnSelectDate = dialogView.findViewById(R.id.btn_select_date);
        Button btnSelectTime = dialogView.findViewById(R.id.btn_select_time);
        RadioGroup radioGroupType = dialogView.findViewById(R.id.radio_group_session_type);
        RatingBar ratingQuality = dialogView.findViewById(R.id.rating_session_quality);
        
        // Set dialog title
        tvDialogTitle.setText(existingSession == null ? "Add Study Session" : "Edit Study Session");
        
        // Initialize date and time
        Calendar selectedDateTime = Calendar.getInstance();
        if (existingSession != null) {
            selectedDateTime.setTimeInMillis(existingSession.getStartTime());
        }
        
        // Update date and time buttons
        updateDateTimeButtons(btnSelectDate, btnSelectTime, selectedDateTime);
        
        // Set up duration seekbar
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = Math.max(5, progress); // Minimum 5 minutes
                tvDurationDisplay.setText(minutes + " min");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Date picker
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateTimeButtons(btnSelectDate, btnSelectTime, selectedDateTime);
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        
        // Time picker
        btnSelectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        updateDateTimeButtons(btnSelectDate, btnSelectTime, selectedDateTime);
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });
        
        // Pre-fill data if editing
        if (existingSession != null) {
            etSubject.setText(existingSession.getSubject());
            etDescription.setText(existingSession.getDescription());
            seekBarDuration.setProgress((int) existingSession.getDurationMinutes());
            ratingQuality.setRating(existingSession.getRating());
            
            // Set session type
            String sessionType = existingSession.getSessionType();
            if ("timer".equals(sessionType)) {
                radioGroupType.check(R.id.radio_timer);
            } else if ("pomodoro".equals(sessionType)) {
                radioGroupType.check(R.id.radio_pomodoro);
            } else {
                radioGroupType.check(R.id.radio_manual);
            }
        }
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        // Cancel button
        dialogView.findViewById(R.id.btn_cancel_session).setOnClickListener(v -> dialog.dismiss());
        
        // Save button
        dialogView.findViewById(R.id.btn_save_session).setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int duration = Math.max(5, seekBarDuration.getProgress());
            float rating = ratingQuality.getRating();
            
            if (subject.isEmpty()) {
                etSubject.setError("Subject is required");
                return;
            }
            
            // Get session type
            String sessionType = "manual";
            int checkedId = radioGroupType.getCheckedRadioButtonId();
            if (checkedId == R.id.radio_timer) {
                sessionType = "timer";
            } else if (checkedId == R.id.radio_pomodoro) {
                sessionType = "pomodoro";
            }
            
            // Calculate end time
            long startTime = selectedDateTime.getTimeInMillis();
            long endTime = startTime + (duration * 60 * 1000);
            
            if (existingSession == null) {
                // Create new session
                StudySession newSession = new StudySession(username, subject, description, 
                        startTime, endTime, duration, sessionType);
                newSession.setRating((int) rating);
                sessionRepository.insertSession(newSession);
                
                Toast.makeText(this, "Study session added successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Update existing session
                existingSession.setSubject(subject);
                existingSession.setDescription(description);
                existingSession.setStartTime(startTime);
                existingSession.setEndTime(endTime);
                existingSession.setDurationMinutes(duration);
                existingSession.setSessionType(sessionType);
                existingSession.setRating((int) rating);
                sessionRepository.updateSession(existingSession);
                
                Toast.makeText(this, "Study session updated successfully", Toast.LENGTH_SHORT).show();
            }
            
            // Refresh data
            updateSubjectFilter();
            loadData();
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void updateDateTimeButtons(Button btnDate, Button btnTime, Calendar calendar) {
        btnDate.setText(android.text.format.DateFormat.format("MMM dd, yyyy", calendar));
        btnTime.setText(android.text.format.DateFormat.format("HH:mm", calendar));
    }
    
    // StudySessionAdapter.OnSessionItemClickListener implementation
    @Override
    public void onSessionEdit(StudySession session) {
        showAddSessionDialog(session);
    }
    
    @Override
    public void onSessionDelete(StudySession session) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this study session?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    sessionRepository.deleteSession(session);
                    updateSubjectFilter();
                    loadData();
                    Toast.makeText(this, "Session deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onSessionRatingChanged(StudySession session, float rating) {
        session.setRating((int) rating);
        sessionRepository.updateSession(session);
        Toast.makeText(this, "Rating updated", Toast.LENGTH_SHORT).show();
    }
    
    // SubjectAdapter.OnSubjectClickListener implementation
    @Override
    public void onSubjectClick(String subject) {
        // Switch to sessions tab and filter by subject
        currentSubjectFilter = subject;
        switchTab(TabType.SESSIONS);
        
        // Update subject filter spinner
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerSubjectFilter.getAdapter();
        int position = adapter.getPosition(subject);
        if (position >= 0) {
            spinnerSubjectFilter.setSelection(position);
        }
    }
} 