package com.parkinson.detection.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for updating user profile
 */
public class UserProfileRequest {

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("age")
    private int age;

    @SerializedName("gender")
    private String gender;

    @SerializedName("medicalHistory")
    private String medicalHistory;

    @SerializedName("profilePictureUrl")
    private String profilePictureUrl;

    // Default constructor
    public UserProfileRequest() {
    }

    // Constructor with parameters
    public UserProfileRequest(String fullName, int age, String gender, String medicalHistory, String profilePictureUrl) {
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters

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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

} 
