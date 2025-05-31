package mobile_java_project.service.impl;

import lombok.RequiredArgsConstructor;
import mobile_java_project.dto.analysis.UserProfileResponse;
import mobile_java_project.dto.analysis.UserProfileUpdateRequest;
import mobile_java_project.entity.User;
import mobile_java_project.exception.ResourceNotFoundException;
import mobile_java_project.repository.UserRepository;
import mobile_java_project.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .age(user.getAge())
                .gender(user.getGender())
                .medicalHistory(user.getMedicalHistory())
                .profilePictureUrl(user.getProfilePictureUrl())
                .totalRecordings(user.getSessions() != null ? user.getSessions().size() : 0)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Split full name into first and last name
        String[] nameParts = request.getFullName().trim().split("\\s+", 2);
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");

        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setMedicalHistory(request.getMedicalHistory());

        User updatedUser = userRepository.save(user);
        return getUserProfile(updatedUser.getId());
    }

    @Override
    @Transactional
    public UserProfileResponse uploadProfilePicture(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Update user profile picture URL
            user.setProfilePictureUrl("/api/profile/pictures/" + filename);
            User updatedUser = userRepository.save(user);

            return getUserProfile(updatedUser.getId());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }
}
