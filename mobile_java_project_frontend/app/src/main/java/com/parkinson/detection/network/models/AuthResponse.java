package com.parkinson.detection.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for authentication endpoints
 */
public class AuthResponse {
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("user_id")
    private long userId;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("full_name")
    private String fullName;
    
    // Default constructor
    public AuthResponse() {
    }
    
    // Getters and Setters
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
} 