package com.example.unimate;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private List<Timer> timerList = new ArrayList<>();
    private OnTimerItemClickListener listener;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;

    public interface OnTimerItemClickListener {
        void onTimerStart(Timer timer);
        void onTimerPause(Timer timer);
        void onTimerStop(Timer timer);
        void onTimerDelete(Timer timer);
        void onTimerCompleted(Timer timer);
    }

    public TimerAdapter(OnTimerItemClickListener listener) {
        this.listener = listener;
        startUpdating();
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        Timer timer = timerList.get(position);
        holder.bind(timer);
    }

    @Override
    public int getItemCount() {
        return timerList.size();
    }

    public void setTimerList(List<Timer> timerList) {
        this.timerList = timerList;
        notifyDataSetChanged();
    }

    public void addTimer(Timer timer) {
        timerList.add(0, timer);
        notifyItemInserted(0);
    }

    public void removeTimer(Timer timer) {
        int position = timerList.indexOf(timer);
        if (position != -1) {
            timerList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateTimer(Timer timer) {
        int position = findTimerPosition(timer.getId());
        if (position != -1) {
            timerList.set(position, timer);
            notifyItemChanged(position);
        }
    }

    private int findTimerPosition(int timerId) {
        for (int i = 0; i < timerList.size(); i++) {
            if (timerList.get(i).getId() == timerId) {
                return i;
            }
        }
        return -1;
    }

    private void startUpdating() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                checkForCompletedTimers();
                notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private void checkForCompletedTimers() {
        for (Timer timer : timerList) {
            if (timer.isActive() && timer.isExpired() && !timer.isCompleted()) {
                timer.setCompleted(true);
                timer.setActive(false);
                if (listener != null) {
                    listener.onTimerCompleted(timer);
                }
            }
        }
    }

    public void stopUpdating() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    class TimerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTimerTitle, tvTimerDescription, tvTimerStatus, tvTimerDisplay, tvTimerInfo;
        private ImageButton btnPlayPause, btnStop, btnDelete;
        private ProgressBar progressTimer;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimerTitle = itemView.findViewById(R.id.tv_timer_title);
            tvTimerDescription = itemView.findViewById(R.id.tv_timer_description);
            tvTimerStatus = itemView.findViewById(R.id.tv_timer_status);
            tvTimerDisplay = itemView.findViewById(R.id.tv_timer_display);
            tvTimerInfo = itemView.findViewById(R.id.tv_timer_info);
            btnPlayPause = itemView.findViewById(R.id.btn_play_pause);
            btnStop = itemView.findViewById(R.id.btn_stop);
            btnDelete = itemView.findViewById(R.id.btn_delete_timer);
            progressTimer = itemView.findViewById(R.id.progress_timer);
        }

        public void bind(Timer timer) {
            tvTimerTitle.setText(timer.getTitle());
            
            if (timer.getDescription() != null && !timer.getDescription().trim().isEmpty()) {
                tvTimerDescription.setText(timer.getDescription());
                tvTimerDescription.setVisibility(View.VISIBLE);
            } else {
                tvTimerDescription.setVisibility(View.GONE);
            }

            long remainingTime = timer.getRemainingTimeMillis();
            String timeDisplay = formatTime(remainingTime);
            tvTimerDisplay.setText(timeDisplay);

            long totalTime = timer.getDurationMillis();
            if (totalTime > 0) {
                int progress = (int) ((totalTime - remainingTime) * 100 / totalTime);
                progressTimer.setProgress(Math.max(0, Math.min(100, progress)));
            }

            if (timer.isCompleted()) {
                tvTimerStatus.setText("Completed ✓");
                tvTimerDisplay.setText("00:00");
                progressTimer.setProgress(100);
                btnPlayPause.setImageResource(R.drawable.ic_play);
                btnPlayPause.setEnabled(false);
                btnStop.setEnabled(false);
                btnPlayPause.setAlpha(0.5f);
                btnStop.setAlpha(0.5f);
            } else if (timer.isActive()) {
                if (timer.isExpired()) {
                    tvTimerStatus.setText("Time's Up! 🎉");
                    tvTimerDisplay.setText("00:00");
                    progressTimer.setProgress(100);
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    btnPlayPause.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnPlayPause.setAlpha(0.5f);
                    btnStop.setAlpha(0.5f);
                } else {
                    tvTimerStatus.setText("Running ⏱️");
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    btnPlayPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnPlayPause.setAlpha(1.0f);
                    btnStop.setAlpha(1.0f);
                }
            } else {
                tvTimerStatus.setText("Ready");
                btnPlayPause.setImageResource(R.drawable.ic_play);
                btnPlayPause.setEnabled(true);
                btnStop.setEnabled(false);
                btnPlayPause.setAlpha(1.0f);
                btnStop.setAlpha(0.5f);
            }

            tvTimerInfo.setText("Duration: " + timer.getDurationMinutes() + " minutes");

            btnPlayPause.setOnClickListener(v -> {
                if (timer.isCompleted()) {
                    return;
                }
                
                if (timer.isActive()) {
                    if (listener != null) listener.onTimerPause(timer);
                } else {
                    if (listener != null) listener.onTimerStart(timer);
                }
            });

            btnStop.setOnClickListener(v -> {
                if (timer.isCompleted()) {
                    return;
                }
                
                if (listener != null) listener.onTimerStop(timer);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onTimerDelete(timer);
            });
        }

        private String formatTime(long milliseconds) {
            long totalSeconds = Math.max(0, milliseconds / 1000);
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }
} 