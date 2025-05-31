package com.parkinson.detection.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.model.Session;
import com.parkinson.detection.repository.SessionRepository;

/**
 * ViewModel for the history screen
 */
public class HistoryViewModel extends AndroidViewModel {
    private static final String TAG = "HistoryViewModel";
    
    private final SessionRepository sessionRepository;
    
    // Filter state
    private final MutableLiveData<Integer> currentFilter = new MutableLiveData<>(null);
    
    // Current sessions list, transformed based on filter
    private final LiveData<List<Session>> sessions;
    
    public HistoryViewModel(@NonNull Application application, SessionApiService apiService) {
        super(application);
        
        sessionRepository = new SessionRepository(application.getApplicationContext(), apiService);
        
        // Transform sessions based on filter
        sessions = Transformations.switchMap(currentFilter, filter -> {
            if (filter == null) {
                return sessionRepository.getAllSessions();
            } else {
                return sessionRepository.getSessionsByPrediction(filter);
            }
        });
        
        // Refresh data from server initially
        refreshSessions();
    }
    
    /**
     * Refresh sessions from server
     */
    public void refreshSessions() {
        sessionRepository.refreshSessionsFromServer();
    }
    
    /**
     * Set prediction filter
     * @param prediction 0 for No Parkinson's, 1 for Suspected Parkinson's, null for all
     */
    public void setFilter(Integer prediction) {
        currentFilter.setValue(prediction);
    }
    
    /**
     * Clear filter
     */
    public void clearFilter() {
        currentFilter.setValue(null);
    }
    
    /**
     * Get sessions LiveData
     */
    public LiveData<List<Session>> getSessions() {
        return sessions;
    }
    
    /**
     * Get current filter
     */
    public LiveData<Integer> getCurrentFilter() {
        return currentFilter;
    }
} 