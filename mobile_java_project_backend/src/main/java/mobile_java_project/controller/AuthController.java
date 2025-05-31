package mobile_java_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mobile_java_project.dto.auth.AuthRequest;
import mobile_java_project.dto.auth.AuthResponse;
import mobile_java_project.dto.auth.RegisterRequest;
import mobile_java_project.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", 
               description = "Authenticates a user and returns JWT tokens for API access")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", 
               description = "Creates a new user account with the provided details")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", 
               description = "Uses a refresh token to get a new access token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", 
               description = "Invalidates the current refresh token")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify email address", 
               description = "Verifies a user's email address using the token sent via email")
    public ResponseEntity<Map<String, Boolean>> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("verified", verified));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", 
               description = "Sends a password reset email to the specified address")
    public ResponseEntity<Void> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.sendPasswordResetEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping({"/reset-password", "/reset"})
    @Operation(summary = "Reset password", 
               description = "Resets a user's password using the token sent via email")
    public ResponseEntity<Map<String, Boolean>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password") != null ? 
                request.get("password") : request.get("newPassword");
        
        if (token == null && request.get("resetToken") != null) {
            token = request.get("resetToken");
        }
        
        if (token == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("reset", false, "error", true));
        }
        
        boolean reset = authService.resetPassword(token, password);
        return ResponseEntity.ok(Map.of("reset", reset));
    }
} 