package com.parkinson.detection.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parkinson.detection.R;
import com.parkinson.detection.databinding.ActivityRegisterBinding;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.network.models.AuthResponse;
import com.parkinson.detection.network.models.RegisterRequest;
import com.parkinson.detection.ui.MainActivity;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        setupGenderDropdown();

    }
    private void setupGenderDropdown() {
        String[] genderOptions = new String[] { "Homme", "Femme"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genderOptions
        );

        binding.autoCompleteGender.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Register button
        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        // Login text
        binding.tvLoginPrompt.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void attemptRegister() {
        // Reset errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilFullName.setError(null);
        binding.tilConfirmPassword.setError(null);
        binding.tilAge.setError(null);

        // Get values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String fullName = binding.etFullName.getText().toString().trim();
        String ageStr = binding.etAge.getText().toString().trim();
        String gender = binding.autoCompleteGender.getText().toString().trim();
        String medicalHistory = binding.etMedicalHistory.getText().toString().trim();

        // Debug logs
        Log.d(TAG, "Full Name: '" + fullName + "' (length: " + fullName.length() + ")");
        Log.d(TAG, "Email: '" + email + "'");
        Log.d(TAG, "Age: '" + ageStr + "'");

        // Validate input
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError(getString(R.string.error_field_required));
            isValid = false;
        }

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
        } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            binding.tilPassword.setError("Mot de passe invalide (au moins 8 caractères, une majuscule, une minuscule, un chiffre, un caractère spécial)");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            isValid = false;
        }

        int age = 0;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            binding.tilAge.setError("L'âge doit être un nombre");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show progress
        showProgress(true);

        // Attempt registration
        RegisterRequest registerRequest = new RegisterRequest(email, password, fullName, age, gender, medicalHistory);
        ApiClient.getInstance().getApiService().register(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    ApiClient.getInstance().setAuthToken(authResponse.getToken());
                    navigateToMainActivity();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        if (jsonObject.has("errors")) {
                            JSONObject errors = jsonObject.getJSONObject("errors");
                            Iterator<String> keys = errors.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String message = errors.getString(key);
                                Toast.makeText(RegisterActivity.this, key + ": " + message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.error_registration_failed, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, R.string.error_registration_failed, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error parsing error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegisterActivity.this, R.string.error_network_connection, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Registration error", t);
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!show);
        binding.etEmail.setEnabled(!show);
        binding.etPassword.setEnabled(!show);
        binding.etFullName.setEnabled(!show);
        binding.etConfirmPassword.setEnabled(!show);
        binding.etAge.setEnabled(!show);
        binding.autoCompleteGender.setEnabled(!show);
        binding.etMedicalHistory.setEnabled(!show);
        binding.tvLoginPrompt.setEnabled(!show);
    }
}