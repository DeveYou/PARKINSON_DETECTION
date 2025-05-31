package com.parkinson.detection.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parkinson.detection.R;
import com.parkinson.detection.databinding.ActivityLoginBinding;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.network.models.AuthResponse;
import com.parkinson.detection.network.models.LoginRequest;
import com.parkinson.detection.ui.MainActivity;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    
    private ActivityLoginBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Check if user is already logged in
        if (ApiClient.getInstance().isAuthenticated()) {
            navigateToMainActivity();
            return;
        }
        
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        
        // Forgot password text
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        // Register text
        binding.tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    private void attemptLogin() {
        // Reset errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        
        // Get values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Validate input
        boolean isValid = true;
        
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Show progress
        showProgress(true);
        
        // Attempt login
        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiClient.getInstance().getApiService().login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    // Save token
                    ApiClient.getInstance().setAuthToken(authResponse.getToken());
                    
                    // Navigate to main activity
                    navigateToMainActivity();
                } else {
                    // Show error
                    Toast.makeText(LoginActivity.this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, R.string.error_network_connection, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login error", t);
            }
        });
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);
        binding.etEmail.setEnabled(!show);
        binding.etPassword.setEnabled(!show);
        binding.tvForgotPassword.setEnabled(!show);
        binding.tvRegisterPrompt.setEnabled(!show);
    }
} 