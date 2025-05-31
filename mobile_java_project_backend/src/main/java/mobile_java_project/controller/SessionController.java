package mobile_java_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobile_java_project.dto.session.SessionCreateRequest;
import mobile_java_project.dto.session.SessionResponse;
import mobile_java_project.entity.User;
import mobile_java_project.repository.UserRepository;
import mobile_java_project.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management endpoints")
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    @PostMapping("/save")
    @Operation(summary = "Save a new session", 
               description = "Saves a new session with sensor data and prediction")
    public ResponseEntity<SessionResponse> saveSession(
            Authentication authentication,
            @Valid @RequestBody SessionCreateRequest request) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return ResponseEntity.ok(sessionService.saveSession(user.getId(), request));
    }

    @GetMapping("/history")
    @Operation(summary = "Get session history", 
               description = "Returns all sessions for the authenticated user")
    public ResponseEntity<List<SessionResponse>> getSessionHistory(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return ResponseEntity.ok(sessionService.getUserSessions(user.getId()));
    }

    @GetMapping
    @Operation(summary = "Get paginated session history", 
               description = "Returns paginated sessions for the authenticated user with sorting and filtering options")
    public ResponseEntity<Page<SessionResponse>> getSessionsPaginated(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer prediction,
            @RequestParam(defaultValue = "timestamp") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        if (prediction != null) {
            return ResponseEntity.ok(
                    sessionService.getUserSessionsByPrediction(user.getId(), prediction, pageable));
        } else {
            return ResponseEntity.ok(
                    sessionService.getUserSessionsPaginated(user.getId(), pageable));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID", 
               description = "Returns a specific session by its ID")
    public ResponseEntity<SessionResponse> getSession(
            @PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSession(id));
    }
} 