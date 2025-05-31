package com.parkinson.detection.ui.profile;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.api.ApiService;
import com.parkinson.detection.network.models.ChangePasswordRequest;
import com.parkinson.detection.network.models.UserProfileRequest;
import com.parkinson.detection.network.models.UserProfileResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for profile management
 */
public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel";

    private final ApiService apiService;

    // LiveData for user profile
    private final MutableLiveData<UserProfileResponse> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordChangeSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> imageUploadSuccess = new MutableLiveData<>();

    public ProfileViewModel() {
        this.apiService = ApiClient.getInstance().getApiService();
    }

    /**
     * Load user profile from API
     */
    public void loadUserProfile() {
        isLoading.setValue(true);

        apiService.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load profile: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Error loading profile", t);
            }
        });
    }

    /**
     * Update user profile
     * @param fullName User's full name
     * @param age User's age
     * @param gender User's gender
     * @param medicalHistory User's medical history
     */
    public void updateProfile(String fullName, int age, String gender, String medicalHistory) {
        isLoading.setValue(true);

        // Get current profile
        UserProfileResponse currentProfile = userProfile.getValue();
        if (currentProfile == null) {
            errorMessage.setValue("No profile data available");
            isLoading.setValue(false);
            return;
        }

        // Create request
        UserProfileRequest request = new UserProfileRequest();
        request.setFullName(fullName);
        request.setAge(age);
        request.setGender(gender);
        request.setMedicalHistory(medicalHistory);

        // Preserve profile picture URL
        if (currentProfile.getProfilePictureUrl() != null) {
            request.setProfilePictureUrl(currentProfile.getProfilePictureUrl());
        }

        // Update profile
        apiService.updateUserProfile(request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body());
                    updateSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to update profile: " + response.message());
                    updateSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                updateSuccess.setValue(false);
                Log.e(TAG, "Error updating profile", t);
            }
        });
    }

    /**
     * Upload profile image
     * @param imageUri URI of the selected image
     * @param imageFile File object of the selected image
     */
    public void uploadProfileImage(Uri imageUri, File imageFile) {
        isLoading.setValue(true);

        // Create multipart request
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        // Upload image
        apiService.uploadProfileImage(imagePart).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body());
                    imageUploadSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to upload image: " + response.message());
                    imageUploadSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                imageUploadSuccess.setValue(false);
                Log.e(TAG, "Error uploading image", t);
            }
        });
    }

    /**
     * Change user password
     * @param currentPassword Current password
     * @param newPassword New password
     * @param confirmPassword Confirmation of new password
     */
    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        isLoading.setValue(true);

        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setValue("New passwords don't match");
            isLoading.setValue(false);
            passwordChangeSuccess.setValue(false);
            return;
        }

        // Create request
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        // Change password
        apiService.changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    passwordChangeSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to change password: " + response.message());
                    passwordChangeSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                passwordChangeSuccess.setValue(false);
                Log.e(TAG, "Error changing password", t);
            }
        });
    }

    // Getters for LiveData

    public LiveData<UserProfileResponse> getUserProfile() {
        return userProfile;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<Boolean> getPasswordChangeSuccess() {
        return passwordChangeSuccess;
    }

    public LiveData<Boolean> getImageUploadSuccess() {
        return imageUploadSuccess;
    }
} 
