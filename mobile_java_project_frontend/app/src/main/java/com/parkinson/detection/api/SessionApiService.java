package com.parkinson.detection.api;

import java.util.List;

import com.parkinson.detection.model.Session;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API interface for session-related endpoints
 */
public interface SessionApiService {
    
    /**
     * Save a new session
     * 
     * @param session The session data to save
     * @return The saved session with server-assigned ID
     */
    @POST("api/sessions/save")
    Call<Session> saveSession(@Body Session session);
    
    /**
     * Get all sessions for the authenticated user
     * 
     * @return List of sessions
     */
    @GET("api/sessions/history")
    Call<List<Session>> getSessionHistory();
    
    /**
     * Get a specific session by ID
     * 
     * @param id The session ID
     * @return The session
     */
    @GET("api/sessions/{id}")
    Call<Session> getSessionById(@Path("id") Long id);
    
    /**
     * Get paginated and filtered sessions
     * 
     * @param page Page number (0-based)
     * @param size Page size
     * @param prediction Optional prediction filter (0 or 1)
     * @param sort Field to sort by
     * @param direction Sort direction ("asc" or "desc")
     * @return Paginated list of sessions
     */
    @GET("api/sessions")
    Call<PagedResponse<Session>> getSessions(
            @Query("page") int page,
            @Query("size") int size,
            @Query("prediction") Integer prediction,
            @Query("sort") String sort,
            @Query("direction") String direction
    );
} 