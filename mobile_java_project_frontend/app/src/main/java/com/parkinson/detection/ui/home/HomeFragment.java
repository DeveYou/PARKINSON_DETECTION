package com.parkinson.detection.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.parkinson.detection.R;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.databinding.FragmentHomeBinding;
import com.parkinson.detection.model.Session;
import com.parkinson.detection.repository.SessionRepository;
import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.ParkinsonDetectionApp;
import com.parkinson.detection.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Home screen fragment showing app overview and quick stats
 */
public class HomeFragment extends Fragment {
    
    private FragmentHomeBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SessionRepository sessionRepository;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize repository
        SessionApiService apiService = ApiClient.getInstance().createService(SessionApiService.class);
        sessionRepository = new SessionRepository(requireContext(), apiService);
        
        // Set up click listeners
        binding.btnStartRecording.setOnClickListener(v -> {
            // Navigate to Record tab
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_record);

            executor.execute(() -> {
                try {
                    // Give time for navigation
                    Thread.sleep(300);
                    requireActivity().runOnUiThread(() -> {
                        if (getActivity() instanceof MainActivity) {
                            MainActivity activity = (MainActivity) getActivity();
                            Fragment recordFragment = activity.getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host_fragment)
                                    .getChildFragmentManager()
                                    .getPrimaryNavigationFragment();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        
        // Load and display session statistics
        loadSessionStats();
    }
    
    private void loadSessionStats() {
        // Observe sessions data to get count and latest recording date
        sessionRepository.getAllSessions().observe(getViewLifecycleOwner(), sessions -> {
            // Update total recordings count
            binding.tvRecordingsCount.setText(String.valueOf(sessions.size()));
            
            // Update last recording date
            if (sessions != null && !sessions.isEmpty()) {
                // Sessions are already ordered by timestamp desc, so first one is the latest
                Session latestSession = sessions.get(0);
                if (latestSession.getTimestamp() != null) {
                    // Convert LocalDateTime to Date before formatting
                    Date date = Date.from(latestSession.getTimestamp().atZone(ZoneId.systemDefault()).toInstant());
                    binding.tvLastRecordingDate.setText(dateFormat.format(date));
                } else {
                    binding.tvLastRecordingDate.setText("Never");
                }
            } else {
                binding.tvLastRecordingDate.setText("Never");
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment is resumed
        loadSessionStats();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 