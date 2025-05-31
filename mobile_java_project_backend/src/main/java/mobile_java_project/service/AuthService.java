package mobile_java_project.service;

import mobile_java_project.dto.auth.AuthRequest;
import mobile_java_project.dto.auth.AuthResponse;
import mobile_java_project.dto.auth.RegisterRequest;

public interface AuthService {
    
    AuthResponse login(AuthRequest authRequest);
    
    AuthResponse register(RegisterRequest registerRequest);
    
    AuthResponse refreshToken(String refreshToken);
    
    void logout(String refreshToken);
    
    boolean verifyEmail(String token);
    
    void sendPasswordResetEmail(String email);
    
    boolean resetPassword(String token, String newPassword);
} 