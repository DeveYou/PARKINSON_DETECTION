package mobile_java_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mobile_java_project.dto.auth.AuthRequest;
import mobile_java_project.dto.auth.AuthResponse;
import mobile_java_project.dto.auth.RegisterRequest;
import mobile_java_project.entity.Role;
import mobile_java_project.entity.User;
import mobile_java_project.exception.ResourceNotFoundException;
import mobile_java_project.repository.UserRepository;
import mobile_java_project.security.JwtTokenProvider;
import mobile_java_project.service.AuthService;
import mobile_java_project.service.EmailService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + registerRequest.getEmail());
        }

        // Process fullName from frontend to get firstName and lastName
        registerRequest.processFullName();
        
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .age(registerRequest.getAge())
                .gender(registerRequest.getGender())
                .medicalHistory(registerRequest.getMedicalHistory())
                .profilePictureUrl(registerRequest.getProfilePictureUrl())
                .role(Role.USER)
                .enabled(true)
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .tokenExpiration(LocalDateTime.now().plusDays(1))
                .build();
        
        User savedUser = userRepository.save(user);
        
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        
        return buildAuthResponse(user, newAccessToken, refreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        // We could implement a token blacklist here if needed
        // For now, client-side logout is sufficient
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));
        
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setTokenExpiration(LocalDateTime.now().plusHours(2));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user);
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));
        
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);
        
        return true;
    }
    
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)         // Changed from accessToken to token to match frontend
                .refreshToken(refreshToken) // Keep for backend use
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .build();
    }
} 