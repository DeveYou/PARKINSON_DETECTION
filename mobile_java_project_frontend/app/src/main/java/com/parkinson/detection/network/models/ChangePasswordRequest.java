package com.parkinson.detection.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for changing user password
 */
public class ChangePasswordRequest {
    
    @SerializedName("current_password")
    private String currentPassword;
    
    @SerializedName("new_password")
    private String newPassword;
    
    // Default constructor
    public ChangePasswordRequest() {
    }
    
    // Constructor with parameters
    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
    
    // Getters and Setters
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 