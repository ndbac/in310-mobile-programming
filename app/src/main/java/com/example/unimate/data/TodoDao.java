package com.example.unimate.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    
    @Insert
    void insert(TodoItem todoItem);
    
    @Update
    void update(TodoItem todoItem);
    
    @Delete
    void delete(TodoItem todoItem);
    
    @Query("SELECT * FROM todo_items WHERE username = :username ORDER BY createdAt DESC")
    List<TodoItem> getAllTodosForUser(String username);
    
    @Query("SELECT * FROM todo_items WHERE username = :username AND isCompleted = 0 ORDER BY createdAt DESC")
    List<TodoItem> getActiveTodosForUser(String username);
    
    @Query("SELECT * FROM todo_items WHERE username = :username AND isCompleted = 1 ORDER BY createdAt DESC")
    List<TodoItem> getCompletedTodosForUser(String username);
    
    @Query("SELECT * FROM todo_items WHERE id = :id")
    TodoItem getTodoById(int id);
    
    @Query("DELETE FROM todo_items WHERE username = :username")
    void deleteAllTodosForUser(String username);
    
    @Query("UPDATE todo_items SET isCompleted = :isCompleted WHERE id = :id")
    void updateTodoStatus(int id, boolean isCompleted);
} 