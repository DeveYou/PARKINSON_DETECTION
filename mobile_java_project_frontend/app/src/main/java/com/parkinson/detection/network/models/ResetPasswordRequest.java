package com.parkinson.detection.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for reset password endpoint
 */
public class ResetPasswordRequest {
    
    @SerializedName("email")
    private String email;
    
    // Default constructor
    public ResetPasswordRequest() {
    }
    
    // Constructor with parameters
    public ResetPasswordRequest(String email) {
        this.email = email;
    }
    
    // Getters and Setters
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 