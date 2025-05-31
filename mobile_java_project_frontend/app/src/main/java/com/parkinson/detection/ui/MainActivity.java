package com.parkinson.detection.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.parkinson.detection.ParkinsonDetectionApp;
import com.parkinson.detection.R;
import com.parkinson.detection.databinding.ActivityMainBinding;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.ui.auth.LoginActivity;

/**
 * Main activity for the app, hosts fragments and handles navigation
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    private ActivityMainBinding binding;
    private NavController navController;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        
        // Set up navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_record, R.id.navigation_history, R.id.navigation_profile)
                .build();


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Set up ActionBar and BottomNavigationView with the NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        } else {
            Log.e(TAG, "NavHostFragment with ID R.id.nav_host_fragment not found in layout.");
        }

    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }


    
    /**
     * Get current user ID from preferences
     * @return User ID
     */
    private long getCurrentUserId() {
        // In a real app, we would get this from the user session or preferences
        // For now, we'll return a placeholder value
        return ParkinsonDetectionApp.getInstance().getPreferences().getLong("user_id", 1);
    }

    
    /**
     * Logout user and navigate to login screen
     */
    private void logout() {
        // Clear auth token
        ApiClient.getInstance().clearAuthToken();
        
        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 