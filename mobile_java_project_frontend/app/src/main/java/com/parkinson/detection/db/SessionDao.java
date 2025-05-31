package com.parkinson.detection.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.parkinson.detection.model.Session;

/**
 * Data Access Object for Session entities
 */
@Dao
public interface SessionDao {
    
    /**
     * Insert a new session
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Session session);
    
    /**
     * Update an existing session
     */
    @Update
    void update(Session session);
    
    /**
     * Delete a session
     */
    @Delete
    void delete(Session session);
    
    /**
     * Get all sessions ordered by timestamp descending
     */
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    LiveData<List<Session>> getAllSessions();
    
    /**
     * Get sessions that have not been synced
     */
    @Query("SELECT * FROM sessions WHERE isSynced = 0")
    List<Session> getUnsyncedSessions();
    
    /**
     * Get session by ID
     */
    @Query("SELECT * FROM sessions WHERE id = :id")
    Session getSessionById(long id);
    
    /**
     * Get sessions filtered by prediction
     */
    @Query("SELECT * FROM sessions WHERE prediction = :prediction ORDER BY timestamp DESC")
    LiveData<List<Session>> getSessionsByPrediction(int prediction);
} 