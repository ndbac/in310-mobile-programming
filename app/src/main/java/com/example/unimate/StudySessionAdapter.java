package com.example.unimate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.StudySession;

import java.util.ArrayList;
import java.util.List;

public class StudySessionAdapter extends RecyclerView.Adapter<StudySessionAdapter.SessionViewHolder> {

    private List<StudySession> sessionList = new ArrayList<>();
    private OnSessionItemClickListener listener;

    public interface OnSessionItemClickListener {
        void onSessionEdit(StudySession session);
        void onSessionDelete(StudySession session);
        void onSessionRatingChanged(StudySession session, float rating);
    }

    public StudySessionAdapter(OnSessionItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        StudySession session = sessionList.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public void setSessionList(List<StudySession> sessionList) {
        this.sessionList = sessionList;
        notifyDataSetChanged();
    }

    public void addSession(StudySession session) {
        sessionList.add(0, session);
        notifyItemInserted(0);
    }

    public void removeSession(StudySession session) {
        int position = sessionList.indexOf(session);
        if (position != -1) {
            sessionList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateSession(StudySession session) {
        int position = findSessionPosition(session.getId());
        if (position != -1) {
            sessionList.set(position, session);
            notifyItemChanged(position);
        }
    }

    private int findSessionPosition(int sessionId) {
        for (int i = 0; i < sessionList.size(); i++) {
            if (sessionList.get(i).getId() == sessionId) {
                return i;
            }
        }
        return -1;
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject, tvDescription, tvType, tvDuration, tvDate, tvTime;
        private LinearLayout layoutRating;
        private RatingBar ratingSession;
        private ImageButton btnEdit, btnDelete;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_session_subject);
            tvDescription = itemView.findViewById(R.id.tv_session_description);
            tvType = itemView.findViewById(R.id.tv_session_type);
            tvDuration = itemView.findViewById(R.id.tv_session_duration);
            tvDate = itemView.findViewById(R.id.tv_session_date);
            tvTime = itemView.findViewById(R.id.tv_session_time);
            layoutRating = itemView.findViewById(R.id.layout_rating);
            ratingSession = itemView.findViewById(R.id.rating_session);
            btnEdit = itemView.findViewById(R.id.btn_edit_session);
            btnDelete = itemView.findViewById(R.id.btn_delete_session);
        }

        public void bind(StudySession session) {
            tvSubject.setText(session.getSubject());
            
            // Show/hide description
            if (session.getDescription() != null && !session.getDescription().trim().isEmpty()) {
                tvDescription.setText(session.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Session type with proper capitalization
            String sessionType = session.getSessionType();
            if (sessionType != null) {
                sessionType = sessionType.substring(0, 1).toUpperCase() + sessionType.substring(1).toLowerCase();
            } else {
                sessionType = "Manual";
            }
            tvType.setText(sessionType);

            // Duration
            tvDuration.setText(session.getFormattedDuration());

            // Date and time
            tvDate.setText(session.getDateString());
            tvTime.setText(session.getTimeString());

            // Rating
            if (session.getRating() > 0) {
                layoutRating.setVisibility(View.VISIBLE);
                ratingSession.setRating(session.getRating());
                
                // Set rating change listener
                ratingSession.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                    if (fromUser && listener != null) {
                        listener.onSessionRatingChanged(session, rating);
                    }
                });
            } else {
                layoutRating.setVisibility(View.GONE);
            }

            // Click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onSessionEdit(session);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onSessionDelete(session);
            });
        }
    }
} 