package com.example.unimate.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, TodoItem.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "unimate_db";
    private static AppDatabase instance;
    
    public abstract UserDao userDao();
    public abstract TodoDao todoDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .allowMainThreadQueries() // Only for simplicity, not recommended for production
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
} 