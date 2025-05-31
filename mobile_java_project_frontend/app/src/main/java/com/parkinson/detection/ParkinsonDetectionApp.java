package com.parkinson.detection;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Main Application class for the Parkinson Detection app.
 */
public class ParkinsonDetectionApp extends Application {
    private static final String TAG = "ParkinsonDetectionApp";

    // Singleton instance
    private static ParkinsonDetectionApp instance;


    // Shared preferences
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application starting...");

        // Set instance
        instance = this;

        // Initialize shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Apply theme based on preferences
        applyTheme();
    }

    /**
     * Get the application instance
     * @return The singleton app instance
     */
    public static ParkinsonDetectionApp getInstance() {
        return instance;
    }


    /**
     * Get shared preferences
     * @return Shared preferences instance
     */
    public SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Apply the theme based on user preferences
     */
    private void applyTheme() {
        String theme = preferences.getString("theme", "system");

        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
} 
