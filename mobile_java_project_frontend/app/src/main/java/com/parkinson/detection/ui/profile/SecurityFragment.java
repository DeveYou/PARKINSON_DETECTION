package com.parkinson.detection.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.parkinson.detection.R;
import com.parkinson.detection.databinding.FragmentSecurityBinding;

/**
 * Fragment for security settings (password change)
 */
public class SecurityFragment extends Fragment {

    private FragmentSecurityBinding binding;
    private ProfileViewModel viewModel;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the shared ViewModel from parent fragment
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSecurityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        observeViewModel();
    }
    
    private void setupViews() {
        // Set up change password button click listener
        binding.changePasswordButton.setOnClickListener(v -> validateAndChangePassword());
    }
    
    private void observeViewModel() {
        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.changePasswordButton.setEnabled(!isLoading);
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe password change success
        viewModel.getPasswordChangeSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), R.string.password_changed, Toast.LENGTH_SHORT).show();
                clearPasswordFields();
            }
        });
    }
    
    private void validateAndChangePassword() {
        String currentPassword = binding.currentPasswordInput.getText().toString();
        String newPassword = binding.newPasswordInput.getText().toString();
        String confirmPassword = binding.confirmPasswordInput.getText().toString();
        
        // Validate inputs
        if (currentPassword.isEmpty()) {
            binding.currentPasswordInput.setError(getString(R.string.required_field));
            return;
        }
        
        if (newPassword.isEmpty()) {
            binding.newPasswordInput.setError(getString(R.string.required_field));
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInput.setError(getString(R.string.required_field));
            return;
        }
        
        // Validate password strength
        if (newPassword.length() < 8) {
            binding.newPasswordInput.setError(getString(R.string.password_too_short));
            return;
        }
        
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            binding.confirmPasswordInput.setError(getString(R.string.passwords_dont_match));
            return;
        }
        
        // Change password
        viewModel.changePassword(currentPassword, newPassword, confirmPassword);
    }
    
    private void clearPasswordFields() {
        binding.currentPasswordInput.setText("");
        binding.newPasswordInput.setText("");
        binding.confirmPasswordInput.setText("");
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 