package com.example.unimate;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unimate.data.TodoItem;
import com.example.unimate.data.TodoRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class TodoActivity extends AppCompatActivity implements TodoAdapter.OnTodoItemClickListener {

    private RecyclerView recyclerTodos;
    private View layoutEmptyState;
    private TodoAdapter todoAdapter;
    private TodoRepository todoRepository;
    private String username;
    
    // Filter buttons
    private Button btnAllTodos, btnActiveTodos, btnCompletedTodos;
    private FilterType currentFilter = FilterType.ALL;
    
    private enum FilterType {
        ALL, ACTIVE, COMPLETED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        
        // Get username from SharedPreferences or Intent
        username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("current_user", "");
        
        if (username.isEmpty()) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadTodos();
    }
    
    private void initViews() {
        recyclerTodos = findViewById(R.id.recycler_todos);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        btnAllTodos = findViewById(R.id.btn_all_todos);
        btnActiveTodos = findViewById(R.id.btn_active_todos);
        btnCompletedTodos = findViewById(R.id.btn_completed_todos);
        
        todoRepository = new TodoRepository(this);
    }
    
    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter(this);
        recyclerTodos.setLayoutManager(new LinearLayoutManager(this));
        recyclerTodos.setAdapter(todoAdapter);
    }
    
    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Add todo button
        findViewById(R.id.fab_add_todo).setOnClickListener(v -> showAddTodoDialog());
        
        // Filter buttons
        btnAllTodos.setOnClickListener(v -> setFilter(FilterType.ALL));
        btnActiveTodos.setOnClickListener(v -> setFilter(FilterType.ACTIVE));
        btnCompletedTodos.setOnClickListener(v -> setFilter(FilterType.COMPLETED));
    }
    
    private void setFilter(FilterType filterType) {
        currentFilter = filterType;
        
        // Update button colors
        resetFilterButtonColors();
        switch (filterType) {
            case ALL:
                btnAllTodos.setTextColor(getColor(R.color.black));
                break;
            case ACTIVE:
                btnActiveTodos.setTextColor(getColor(R.color.black));
                break;
            case COMPLETED:
                btnCompletedTodos.setTextColor(getColor(R.color.black));
                break;
        }
        
        loadTodos();
    }
    
    private void resetFilterButtonColors() {
        int grayColor = getColor(android.R.color.darker_gray);
        btnAllTodos.setTextColor(grayColor);
        btnActiveTodos.setTextColor(grayColor);
        btnCompletedTodos.setTextColor(grayColor);
    }
    
    private void loadTodos() {
        List<TodoItem> todos;
        
        switch (currentFilter) {
            case ACTIVE:
                todos = todoRepository.getActiveTodosForUser(username);
                break;
            case COMPLETED:
                todos = todoRepository.getCompletedTodosForUser(username);
                break;
            default:
                todos = todoRepository.getAllTodosForUser(username);
                break;
        }
        
        todoAdapter.setTodoList(todos);
        
        // Show/hide empty state
        if (todos.isEmpty()) {
            recyclerTodos.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerTodos.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
    
    private void showAddTodoDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        TextInputEditText etTitle = dialogView.findViewById(R.id.et_title);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_description);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btn_add).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            if (title.isEmpty()) {
                etTitle.setError("Title is required");
                return;
            }
            
            TodoItem newTodo = new TodoItem(username, title, description);
            todoRepository.insertTodo(newTodo);
            
            // If we're showing all or active todos, add to the adapter
            if (currentFilter == FilterType.ALL || currentFilter == FilterType.ACTIVE) {
                todoAdapter.addTodoItem(newTodo);
                recyclerTodos.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
            
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    @Override
    public void onTodoStatusChanged(TodoItem todoItem, boolean isCompleted) {
        todoItem.setCompleted(isCompleted);
        todoRepository.updateTodo(todoItem);
        
        // If filter doesn't match the new status, remove from current view
        if ((currentFilter == FilterType.ACTIVE && isCompleted) ||
            (currentFilter == FilterType.COMPLETED && !isCompleted)) {
            todoAdapter.removeTodoItem(todoItem);
            
            // Check if list is now empty
            if (todoAdapter.getItemCount() == 0) {
                recyclerTodos.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        } else {
            todoAdapter.updateTodoItem(todoItem);
        }
        
        String message = isCompleted ? "Task completed!" : "Task marked as active";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTodoDelete(TodoItem todoItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    todoRepository.deleteTodo(todoItem);
                    todoAdapter.removeTodoItem(todoItem);
                    
                    // Check if list is now empty
                    if (todoAdapter.getItemCount() == 0) {
                        recyclerTodos.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                    
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onTodoClick(TodoItem todoItem) {
        // For now, just show a toast. Could be expanded to show details or edit
        Toast.makeText(this, "Task: " + todoItem.getTitle(), Toast.LENGTH_SHORT).show();
    }
} 