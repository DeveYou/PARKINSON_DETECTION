package com.parkinson.detection.api;

import android.content.SharedPreferences;

import com.parkinson.detection.BuildConfig;
import com.parkinson.detection.ParkinsonDetectionApp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton for managing Retrofit API client
 */
public class ApiClient {
    // Base URL for the API (replace with your actual API URL)
    private static final String BASE_URL = "http://192.168.1.117:8080/api/v1/";
    
    // Singleton instance
    private static ApiClient instance;
    
    // Retrofit and API service instances
    private Retrofit retrofit;
    private ApiService apiService;
    
    // Token for authentication
    private String authToken;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private ApiClient() {
        // Initialize HTTP client with logging and authorization
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        
        // Add logging interceptor for debug builds
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);
        }
        
        // Add authorization interceptor
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                
                // Skip authentication for login/register endpoints
                String url = original.url().toString();
                if (url.contains("/login") || url.contains("/register") || url.contains("/reset-password")) {
                    return chain.proceed(original);
                }
                
                // Add auth token to all other requests
                if (authToken != null && !authToken.isEmpty()) {
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
                
                return chain.proceed(original);
            }
        });
        
        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        
        // Create API service
        apiService = retrofit.create(ApiService.class);
        
        // Load saved auth token if available
        loadAuthToken();
    }
    
    /**
     * Get singleton instance
     * @return ApiClient instance
     */
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }
    
    /**
     * Get the API service
     * @return ApiService instance
     */
    public ApiService getApiService() {
        return apiService;
    }
    
    /**
     * Get the Retrofit instance
     * @return Retrofit instance
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }
    
    /**
     * Create a new API service interface
     * @param serviceClass The class of the API service interface
     * @return The API service instance
     */
    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
    
    /**
     * Set authentication token
     * @param token JWT token
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        saveAuthToken();
    }
    
    /**
     * Clear authentication token (for logout)
     */
    public void clearAuthToken() {
        this.authToken = null;
        
        // Clear from preferences
        SharedPreferences prefs = ParkinsonDetectionApp.getInstance().getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("auth_token");
        editor.apply();
    }
    
    /**
     * Check if user is authenticated
     * @return true if auth token exists, false otherwise
     */
    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }
    
    /**
     * Save auth token to SharedPreferences
     */
    private void saveAuthToken() {
        if (authToken != null) {
            SharedPreferences prefs = ParkinsonDetectionApp.getInstance().getPreferences();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("auth_token", authToken);
            editor.apply();
        }
    }
    
    /**
     * Load auth token from SharedPreferences
     */
    private void loadAuthToken() {
        SharedPreferences prefs = ParkinsonDetectionApp.getInstance().getPreferences();
        authToken = prefs.getString("auth_token", null);
    }
} 