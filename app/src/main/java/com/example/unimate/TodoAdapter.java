package com.example.unimate;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todoList = new ArrayList<>();
    private OnTodoItemClickListener listener;

    public interface OnTodoItemClickListener {
        void onTodoStatusChanged(TodoItem todoItem, boolean isCompleted);
        void onTodoDelete(TodoItem todoItem);
        void onTodoClick(TodoItem todoItem);
    }

    public TodoAdapter(OnTodoItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todoItem = todoList.get(position);
        holder.bind(todoItem);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTodoList(List<TodoItem> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void addTodoItem(TodoItem todoItem) {
        todoList.add(0, todoItem); // Add to the beginning
        notifyItemInserted(0);
    }

    public void removeTodoItem(TodoItem todoItem) {
        int position = todoList.indexOf(todoItem);
        if (position != -1) {
            todoList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateTodoItem(TodoItem todoItem) {
        int position = -1;
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).getId() == todoItem.getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            todoList.set(position, todoItem);
            notifyItemChanged(position);
        }
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDescription, tvCreatedDate;
        private CheckBox checkboxCompleted;
        private ImageButton btnDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            checkboxCompleted = itemView.findViewById(R.id.checkbox_completed);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(TodoItem todoItem) {
            tvTitle.setText(todoItem.getTitle());
            
            // Show/hide description
            if (todoItem.getDescription() != null && !todoItem.getDescription().trim().isEmpty()) {
                tvDescription.setText(todoItem.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Format and display created date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String createdDate = dateFormat.format(new Date(todoItem.getCreatedAt()));
            tvCreatedDate.setText("Created: " + createdDate);

            // Set checkbox state
            checkboxCompleted.setChecked(todoItem.isCompleted());

            // Apply strikethrough effect for completed tasks
            if (todoItem.isCompleted()) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setAlpha(0.6f);
                tvDescription.setAlpha(0.6f);
                tvCreatedDate.setAlpha(0.6f);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setAlpha(1.0f);
                tvDescription.setAlpha(1.0f);
                tvCreatedDate.setAlpha(1.0f);
            }

            // Set click listeners
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTodoStatusChanged(todoItem, isChecked);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoDelete(todoItem);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoClick(todoItem);
                }
            });
        }
    }
} 