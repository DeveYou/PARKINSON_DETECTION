package mobile_java_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile_java_project.dto.analysis.UserProfileResponse;
import mobile_java_project.dto.analysis.UserProfileUpdateRequest;
import mobile_java_project.entity.User;
import mobile_java_project.repository.UserRepository;
import mobile_java_project.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), request));
    }

    @PostMapping("/picture")
    public ResponseEntity<UserProfileResponse> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("image") MultipartFile file) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(userService.uploadProfilePicture(user.getId(), file));
    }

    @GetMapping("/pictures/{filename}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}