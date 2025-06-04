package com.example.unimate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<SubjectData> subjectList = new ArrayList<>();
    private OnSubjectClickListener listener;

    public interface OnSubjectClickListener {
        void onSubjectClick(String subject);
    }

    public static class SubjectData {
        public String subject;
        public long totalTimeMinutes;
        public int sessionCount;

        public SubjectData(String subject, long totalTimeMinutes, int sessionCount) {
            this.subject = subject;
            this.totalTimeMinutes = totalTimeMinutes;
            this.sessionCount = sessionCount;
        }

        public String getFormattedTime() {
            if (totalTimeMinutes < 60) {
                return totalTimeMinutes + " min";
            } else {
                long hours = totalTimeMinutes / 60;
                long minutes = totalTimeMinutes % 60;
                if (minutes == 0) {
                    return hours + "h";
                } else {
                    return hours + "h " + minutes + "m";
                }
            }
        }

        public String getSessionText() {
            return sessionCount == 1 ? "1 session" : sessionCount + " sessions";
        }
    }

    public SubjectAdapter(OnSubjectClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectData subject = subjectList.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void setSubjectList(List<SubjectData> subjectList) {
        this.subjectList = subjectList;
        notifyDataSetChanged();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubjectName, tvSubjectSessions, tvSubjectTotalTime;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvSubjectSessions = itemView.findViewById(R.id.tv_subject_sessions);
            tvSubjectTotalTime = itemView.findViewById(R.id.tv_subject_total_time);
        }

        public void bind(SubjectData subject) {
            tvSubjectName.setText(subject.subject);
            tvSubjectSessions.setText(subject.getSessionText());
            tvSubjectTotalTime.setText(subject.getFormattedTime());

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectClick(subject.subject);
                }
            });
        }
    }
} 