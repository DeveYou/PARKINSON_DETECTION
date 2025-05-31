package com.parkinson.detection.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.parkinson.detection.data.entities.User;

import java.util.List;

/**
 * Data Access Object for the User entity
 */
@Dao
public interface UserDao {
    
    /**
     * Insert a new user
     * @param user User to insert
     * @return Generated ID
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);
    
    /**
     * Update an existing user
     * @param user User to update
     */
    @Update
    void update(User user);
    
    /**
     * Delete a user
     * @param user User to delete
     */
    @Delete
    void delete(User user);
    
    /**
     * Get user by ID
     * @param id User ID
     * @return User with matching ID, or null if not found
     */
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(long id);
    
    /**
     * Get user by email
     * @param email User email
     * @return User with matching email, or null if not found
     */
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    /**
     * Check if a user with the given email exists
     * @param email User email
     * @return true if user exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean userExists(String email);
    
    /**
     * Get all users
     * @return List of all users
     */
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    
    /**
     * Get all users as LiveData
     * @return LiveData list of all users
     */
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsersLive();
}