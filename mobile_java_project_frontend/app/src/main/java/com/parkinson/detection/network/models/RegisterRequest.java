package com.parkinson.detection.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for register endpoint
 */
public class RegisterRequest {
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("age")
    private int age;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("medical_history")
    private String medicalHistory;
    
    // Default constructor
    public RegisterRequest() {
    }
    
    // Constructor with parameters
    public RegisterRequest(String email, String password, String fullName, int age, String gender, String medicalHistory) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
    }

    public RegisterRequest(String email, String password, String fullName) {
    }

    // Getters and Setters
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
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
} 