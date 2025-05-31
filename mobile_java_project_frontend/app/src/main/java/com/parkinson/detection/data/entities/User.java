package com.parkinson.detection.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;


/**
 * Entity representing a user in the database
 */
@Entity(
    tableName = "users",
    indices = {@Index(value = "email", unique = true)}
)
public class User {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @NonNull
    private String email;
    
    @NonNull
    private String fullName;
    
    private int age;
    
    private String gender;
    
    private String medicalHistory;
    
    private String profilePictureUrl;
    
    // Default constructor
    public User() {
    }

    // Constructor with parameters
    @Ignore
    public User(@NonNull String email, @NonNull String fullName, int age, String gender, String medicalHistory) {
        this.email = email;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    @NonNull
    public String getEmail() {
        return email;
    }
    
    public void setEmail(@NonNull String email) {
        this.email = email;
    }
    
    @NonNull
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
} 