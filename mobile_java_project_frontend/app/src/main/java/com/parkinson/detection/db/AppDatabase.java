package com.parkinson.detection.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

import com.parkinson.detection.model.Session;

/**
 * Room database for the application
 */
@Database(entities = {Session.class}, version = 2, exportSchema = false)
@TypeConverters({DateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "aigest_db";
    private static AppDatabase instance;
    
    // Migration from version 1 to 2 to add durationMs column
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the durationMs column to the sessions table
            database.execSQL("ALTER TABLE sessions ADD COLUMN durationMs INTEGER");
        }
    };
    
    public abstract SessionDao sessionDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2) // Add the migration
                    .fallbackToDestructiveMigration() // As a fallback if migration fails
                    .build();
        }
        return instance;
    }
} 