package com.parkinson.detection.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parkinson.detection.R;
import com.parkinson.detection.databinding.ActivityForgotPasswordBinding;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.network.models.ResetPasswordRequest;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    
    private ActivityForgotPasswordBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Setup toolbar with back button
        /*setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }*/
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        
        // Reset password button
        binding.btnResetPassword.setOnClickListener(v -> attemptResetPassword());
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void attemptResetPassword() {
        // Reset errors
        binding.tilEmail.setError(null);
        
        // Get values
        String email = binding.etEmail.getText().toString().trim();
        
        // Validate input
        boolean isValid = true;
        
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Show progress
        showProgress(true);
        
        // Attempt password reset
        ResetPasswordRequest resetRequest = new ResetPasswordRequest(email);
        ApiClient.getInstance().getApiService().resetPassword(resetRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showProgress(false);
                
                if (response.isSuccessful()) {
                    // Show success message
                    Toast.makeText(ForgotPasswordActivity.this, 
                            R.string.reset_password_instructions_sent, 
                            Toast.LENGTH_LONG).show();
                    
                    // Go back to login
                    finish();
                } else {
                    // Show error
                    Toast.makeText(ForgotPasswordActivity.this, 
                            R.string.error_reset_password_failed, 
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Reset password failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showProgress(false);
                Toast.makeText(ForgotPasswordActivity.this, 
                        R.string.error_network_connection, 
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Reset password error", t);
            }
        });
    }
    
    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnResetPassword.setEnabled(!show);
        binding.etEmail.setEnabled(!show);
    }
}
