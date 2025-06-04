package com.example.unimate.data;

import android.content.Context;

import java.util.List;

public class TodoRepository {
    
    private TodoDao todoDao;
    
    public TodoRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        todoDao = database.todoDao();
    }
    
    public void insertTodo(TodoItem todoItem) {
        todoDao.insert(todoItem);
    }
    
    public void updateTodo(TodoItem todoItem) {
        todoDao.update(todoItem);
    }
    
    public void deleteTodo(TodoItem todoItem) {
        todoDao.delete(todoItem);
    }
    
    public List<TodoItem> getAllTodosForUser(String username) {
        return todoDao.getAllTodosForUser(username);
    }
    
    public List<TodoItem> getActiveTodosForUser(String username) {
        return todoDao.getActiveTodosForUser(username);
    }
    
    public List<TodoItem> getCompletedTodosForUser(String username) {
        return todoDao.getCompletedTodosForUser(username);
    }
    
    public TodoItem getTodoById(int id) {
        return todoDao.getTodoById(id);
    }
    
    public void updateTodoStatus(int id, boolean isCompleted) {
        todoDao.updateTodoStatus(id, isCompleted);
    }
    
    public void deleteAllTodosForUser(String username) {
        todoDao.deleteAllTodosForUser(username);
    }
} 