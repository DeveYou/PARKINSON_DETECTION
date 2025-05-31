package com.parkinson.detection.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parkinson.detection.R;
import com.parkinson.detection.databinding.FragmentAccountInfoBinding;
import com.parkinson.detection.network.models.UserProfileResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment for user account information
 */
public class AccountInfoFragment extends Fragment {

    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final String TAG = "AccountInfoFragment";

    private FragmentAccountInfoBinding binding;
    private ProfileViewModel viewModel;
    private Uri selectedImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the shared ViewModel from activity
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        // Set up profile image click listener
        binding.profileImage.setOnClickListener(v -> showImageSourceDialog());

        // Set up save button click listener
        binding.saveButton.setOnClickListener(v -> saveProfile());
    }

    private void showImageSourceDialog() {
        String[] options = {getString(R.string.gallery), getString(R.string.camera)};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_image_source))
            .setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Gallery option selected
                    openImagePicker();
                } else {
                    // Camera option selected
                    openCamera();
                }
            })
            .show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(requireContext(), R.string.camera_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        // Observe user profile data
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUI);

        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.saveButton.setEnabled(!isLoading);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe update success
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe image upload success
        viewModel.getImageUploadSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), R.string.image_uploaded, Toast.LENGTH_SHORT).show();
                selectedImageUri = null;
            }
        });
    }

    private void updateUI(UserProfileResponse profile) {
        if (profile != null) {
            binding.fullNameInput.setText(profile.getFullName());
            binding.emailInput.setText(profile.getEmail());

            // Set age if available
            if (profile.getAge() > 0) {
                binding.ageInput.setText(String.valueOf(profile.getAge()));
            }

            // Set gender if available
            if (profile.getGender() != null) {
                binding.genderInput.setText(profile.getGender());
            }

            // Set medical history if available
            if (profile.getMedicalHistory() != null) {
                binding.medicalHistoryInput.setText(profile.getMedicalHistory());
            }

            // Load profile image if available
            if (profile.getProfilePictureUrl() != null && !profile.getProfilePictureUrl().isEmpty()) {
                Glide.with(this)
                    .load(profile.getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(binding.profileImage);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void saveProfile() {
        String fullName = binding.fullNameInput.getText().toString().trim();
        String ageStr = binding.ageInput.getText().toString().trim();
        String gender = binding.genderInput.getText().toString().trim();
        String medicalHistory = binding.medicalHistoryInput.getText().toString().trim();

        // Validate inputs
        if (fullName.isEmpty()) {
            binding.fullNameInput.setError(getString(R.string.required_field));
            return;
        }

        // Parse age
        int age = 0;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                binding.ageInput.setError("Invalid age");
                return;
            }
        }

        // Update profile
        viewModel.updateProfile(fullName, age, gender, medicalHistory);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                // Handle gallery image selection
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Preview the selected image
                    Glide.with(this)
                        .load(selectedImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.profileImage);

                    // Upload the image
                    uploadImage(selectedImageUri);
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
                // Handle camera photo capture
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        // Preview the captured image
                        Glide.with(this)
                            .load(imageBitmap)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.profileImage);

                        // Convert bitmap to URI and upload
                        Uri imageUri = saveBitmapToCache(imageBitmap);
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            uploadImage(selectedImageUri);
                        }
                    }
                }
            }
        }
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();

            File imageFile = new File(cachePath, "captured_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.image_upload_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            // Convert URI to File
            File imageFile = createImageFile(imageUri);
            if (imageFile != null) {
                viewModel.uploadProfileImage(imageUri, imageFile);
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.image_upload_error, Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return null;
        }

        File tempFile = File.createTempFile("profile_image", ".jpg", requireContext().getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        return tempFile;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 
