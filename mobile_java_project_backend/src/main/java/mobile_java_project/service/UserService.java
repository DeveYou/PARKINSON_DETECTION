package mobile_java_project.service;

import mobile_java_project.dto.analysis.UserProfileResponse;
import mobile_java_project.dto.analysis.UserProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * Retrieves a user's profile information
     *
     * @param userId the ID of the user to retrieve
     * @return the user's profile information
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * Updates a user's profile information
     *
     * @param userId the ID of the user to update
     * @param request the updated profile information
     * @return the updated user profile
     */
    UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request);

    UserProfileResponse uploadProfilePicture(Long userId, MultipartFile file);
}