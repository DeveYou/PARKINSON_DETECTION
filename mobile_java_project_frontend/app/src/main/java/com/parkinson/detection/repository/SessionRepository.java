package com.parkinson.detection.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.db.AppDatabase;
import com.parkinson.detection.db.SessionDao;
import com.parkinson.detection.model.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for handling Session data with offline-first approach
 */
public class SessionRepository {
    private static final String TAG = "SessionRepository";
    
    private final SessionDao sessionDao;
    private final SessionApiService apiService;
    private final Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();
    
    public SessionRepository(Context context, SessionApiService apiService) {
        this.context = context;
        this.apiService = apiService;
        AppDatabase database = AppDatabase.getInstance(context);
        this.sessionDao = database.sessionDao();
    }
    
    /**
     * Save a session locally, then try to sync with server if online
     */
    public LiveData<Session> saveSession(Session session) {
        MutableLiveData<Session> result = new MutableLiveData<>();
        
        // Save locally first (offline-first approach)
        executor.execute(() -> {
            long localId = sessionDao.insert(session);
            session.setId(localId);
            result.postValue(session);
            
            // Try to sync if online
            if (isNetworkAvailable()) {
                syncSession(session);
            }
        });
        
        return result;
    }
    
    /**
     * Get all sessions from local database
     */
    public LiveData<List<Session>> getAllSessions() {
        // Try to sync any unsynced sessions
        syncUnsyncedSessions();
        
        // Return local data
        return sessionDao.getAllSessions();
    }
    
    /**
     * Get sessions filtered by prediction
     */
    public LiveData<List<Session>> getSessionsByPrediction(int prediction) {
        // Try to sync any unsynced sessions
        syncUnsyncedSessions();
        
        // Return local data
        return sessionDao.getSessionsByPrediction(prediction);
    }
    
    /**
     * Sync all unsynced sessions with the server
     */
    public void syncUnsyncedSessions() {
        if (!isNetworkAvailable()) {
            return;
        }
        
        AsyncTask.execute(() -> {
            List<Session> unsyncedSessions = sessionDao.getUnsyncedSessions();
            Log.d(TAG, "Found " + unsyncedSessions.size() + " unsynced sessions");
            
            for (Session session : unsyncedSessions) {
                syncSession(session);
            }
        });
    }
    
    /**
     * Sync a specific session with the server
     */
    private void syncSession(Session session) {
        apiService.saveSession(session).enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Session serverSession = response.body();
                    executor.execute(() -> {
                        // Update local session with server ID and mark as synced
                        session.setId(serverSession.getId());
                        session.setSynced(true);
                        sessionDao.update(session);
                        Log.d(TAG, "Session synced successfully with ID: " + serverSession.getId());
                    });
                } else {
                    Log.e(TAG, "Failed to sync session: " + 
                            (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                }
            }
            
            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                Log.e(TAG, "Network error syncing session", t);
            }
        });
    }
    
    /**
     * Fetch sessions from server and update local database
     */
    public void refreshSessionsFromServer() {
        if (!isNetworkAvailable()) {
            return;
        }
        
        apiService.getSessionHistory().enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        List<Session> serverSessions = response.body();
                        for (Session session : serverSessions) {
                            session.setSynced(true);
                            sessionDao.insert(session);
                        }
                        Log.d(TAG, "Refreshed " + serverSessions.size() + " sessions from server");
                    });
                }
            }
            
            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.e(TAG, "Failed to refresh sessions from server", t);
            }
        });
    }
    
    /**
     * Get a specific session by ID
     * 
     * @param id The session ID
     * @return The session or null if not found
     */
    public Session getSessionById(long id) {
        // Try to get from local database first
        Session localSession = sessionDao.getSessionById(id);
        
        // If we have a network connection, try to get the latest from server
        if (isNetworkAvailable()) {
            try {
                Response<Session> response = apiService.getSessionById(id).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Session serverSession = response.body();
                    serverSession.setSynced(true);
                    sessionDao.insert(serverSession);
                    return serverSession;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching session from server", e);
            }
        }
        
        return localSession;
    }
    
    /**
     * Check if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null && 
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                 capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }
} 