package mobile_java_project.service;

import mobile_java_project.dto.session.SessionCreateRequest;
import mobile_java_project.dto.session.SessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {
    
    /**
     * Save a new session for the authenticated user
     * 
     * @param userId The ID of the user creating the session
     * @param sessionCreateRequest The session data to save
     * @return The created session response
     */
    SessionResponse saveSession(Long userId, SessionCreateRequest sessionCreateRequest);
    
    /**
     * Get a session by ID
     * 
     * @param sessionId The ID of the session to retrieve
     * @return The session response
     */
    SessionResponse getSession(Long sessionId);
    
    /**
     * Get all sessions for a user, ordered by timestamp descending
     * 
     * @param userId The ID of the user
     * @return List of session responses
     */
    List<SessionResponse> getUserSessions(Long userId);
    
    /**
     * Get paginated sessions for a user
     * 
     * @param userId The ID of the user
     * @param pageable Pagination information
     * @return Page of session responses
     */
    Page<SessionResponse> getUserSessionsPaginated(Long userId, Pageable pageable);
    
    /**
     * Get paginated sessions for a user filtered by prediction
     * 
     * @param userId The ID of the user
     * @param prediction The prediction value to filter by (0 or 1)
     * @param pageable Pagination information
     * @return Page of session responses
     */
    Page<SessionResponse> getUserSessionsByPrediction(Long userId, int prediction, Pageable pageable);
} 