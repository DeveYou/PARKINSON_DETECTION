package com.parkinson.detection.api;

import com.parkinson.detection.network.models.AuthResponse;
import com.parkinson.detection.network.models.ChangePasswordRequest;
import com.parkinson.detection.network.models.LoginRequest;
import com.parkinson.detection.network.models.RegisterRequest;
import com.parkinson.detection.network.models.ResetPasswordRequest;
import com.parkinson.detection.network.models.UserProfileRequest;
import com.parkinson.detection.network.models.UserProfileResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Retrofit interface for API endpoints
 */
public interface ApiService {

    // Authentication endpoints

    /**
     * Login with email and password
     * @param request Login request body
     * @return Auth response with token
     */
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    /**
     * Register a new user
     * @param request Register request body
     * @return Auth response with token
     */
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    /**
     * Request password reset
     * @param request Reset request with email
     * @return Success response
     */
    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    /**
     * Change user password
     * @param request Change password request with current and new password
     * @return Success response
     */
    @PUT("api/v1/auth/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    // User profile endpoints

    /**
     * Get user profile
     * @return User profile response
     */
    @GET("api/profile")
    Call<UserProfileResponse> getUserProfile();

    /**
     * Update user profile
     * @param request Profile update request
     * @return Updated user profile
     */
    @PUT("api/profile")
    Call<UserProfileResponse> updateUserProfile(@Body UserProfileRequest request);

    /**
     * Upload profile image
     * @param image Image file part
     * @return Updated user profile with new image URL
     */
    @Multipart
    @POST("api/profile/picture")
    Call<UserProfileResponse> uploadProfileImage(@Part MultipartBody.Part image);

    /**
     * Get profile picture by filename
     * @param filename Profile picture filename
     * @return Response body containing the image
     */
    @GET("api/profile/pictures/{filename}")
    Call<okhttp3.ResponseBody> getProfilePicture(@Path("filename") String filename);

} 
