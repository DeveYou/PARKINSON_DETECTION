package com.parkinson.detection.ui.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.parkinson.detection.R;

import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.model.Session;
import com.parkinson.detection.api.ApiClient;

/**
 * Fragment for recording motion data
 */
public class RecordFragment extends Fragment {
    
    private RecordViewModel viewModel;
    private Button recordButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private TextView recordingTimeText;
    
    // Sensor data TextViews
    private TextView accelerometerX;
    private TextView accelerometerY;
    private TextView accelerometerZ;
    private TextView gyroscopeX;
    private TextView gyroscopeY;
    private TextView gyroscopeZ;
    
    // Progress bars for sensor data
    private ProgressBar accelerometerXProgress;
    private ProgressBar accelerometerYProgress;
    private ProgressBar accelerometerZProgress;
    private ProgressBar gyroscopeXProgress;
    private ProgressBar gyroscopeYProgress;
    private ProgressBar gyroscopeZProgress;
    
    // For tracking recording duration
    private long startTime = 0;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (startTime > 0) {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                recordingTimeText.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
                
                // Update the ViewModel with current duration
                viewModel.updateRecordingDuration(millis);
            }
        }
    };
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recordButton = view.findViewById(R.id.record_button);
        progressBar = view.findViewById(R.id.progress_bar);
        statusText = view.findViewById(R.id.status_text);
        recordingTimeText = view.findViewById(R.id.tv_recording_time);
        
        // Initialize sensor data views
        accelerometerX = view.findViewById(R.id.tv_accelerometer_x);
        accelerometerY = view.findViewById(R.id.tv_accelerometer_y);
        accelerometerZ = view.findViewById(R.id.tv_accelerometer_z);
        gyroscopeX = view.findViewById(R.id.tv_gyroscope_x);
        gyroscopeY = view.findViewById(R.id.tv_gyroscope_y);
        gyroscopeZ = view.findViewById(R.id.tv_gyroscope_z);
        
        // Initialize progress bars
        accelerometerXProgress = view.findViewById(R.id.pb_accelerometer_x);
        accelerometerYProgress = view.findViewById(R.id.pb_accelerometer_y);
        accelerometerZProgress = view.findViewById(R.id.pb_accelerometer_z);
        gyroscopeXProgress = view.findViewById(R.id.pb_gyroscope_x);
        gyroscopeYProgress = view.findViewById(R.id.pb_gyroscope_y);
        gyroscopeZProgress = view.findViewById(R.id.pb_gyroscope_z);
        
        // Create ViewModel
        SessionApiService apiService = ApiClient.getInstance().createService(SessionApiService.class);
        RecordViewModelFactory factory = new RecordViewModelFactory(
                requireActivity().getApplication(), apiService);
        viewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);
        
        // Set up record button
        recordButton.setOnClickListener(v -> {
            if (viewModel.getRecordingStatus().getValue() != null && 
                    viewModel.getRecordingStatus().getValue()) {
                // Stop recording
                viewModel.stopRecording();
                recordButton.setText(R.string.start_recording);
                stopTimer();
            } else {
                // Start recording
                viewModel.startRecording();
                recordButton.setText(R.string.stop_recording);
                startTimer();
            }
        });
        
        // Observe recording status
        viewModel.getRecordingStatus().observe(getViewLifecycleOwner(), isRecording -> {
            recordButton.setText(isRecording ? R.string.stop_recording : R.string.start_recording);
            statusText.setText(isRecording ? R.string.recording_in_progress : R.string.ready_to_record);
            progressBar.setVisibility(isRecording ? View.VISIBLE : View.INVISIBLE);
            
            if (isRecording) {
                startTimer();
            } else {
                stopTimer();
            }
        });
        
        // Observe data points collected
        viewModel.getDataPointsCollected().observe(getViewLifecycleOwner(), dataPoints -> {
            if (dataPoints > 0) {
                statusText.setText(getString(R.string.data_points_collected, dataPoints));
            }
        });
        
        // Observe sensor data updates
        viewModel.getSensorData().observe(getViewLifecycleOwner(), sensorData -> {
            if (sensorData != null) {
                // Update accelerometer values
                updateSensorDisplay(accelerometerX, accelerometerXProgress, "X", sensorData.accelX, 20);
                updateSensorDisplay(accelerometerY, accelerometerYProgress, "Y", sensorData.accelY, 20);
                updateSensorDisplay(accelerometerZ, accelerometerZProgress, "Z", sensorData.accelZ, 20);
                
                // Update gyroscope values
                updateSensorDisplay(gyroscopeX, gyroscopeXProgress, "X", sensorData.gyroX, 10);
                updateSensorDisplay(gyroscopeY, gyroscopeYProgress, "Y", sensorData.gyroY, 10);
                updateSensorDisplay(gyroscopeZ, gyroscopeZProgress, "Z", sensorData.gyroZ, 10);
            }
        });
        
        // Observe session result
        viewModel.getSessionResult().observe(getViewLifecycleOwner(), this::navigateToResult);
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * Start the duration timer
     */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }
    
    /**
     * Stop the duration timer
     */
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }
    
    /**
     * Update sensor display with value and progress bar
     */
    private void updateSensorDisplay(TextView textView, ProgressBar progressBar, String axis, 
                                    float value, float scale) {
        String formattedValue = String.format("%s: %.2f", axis, value);
        textView.setText(formattedValue);
        
        // Scale the value for progress bar (values range from negative to positive)
        int progress = (int) (50 + (value / scale) * 50);
        progress = Math.max(0, Math.min(100, progress));
        progressBar.setProgress(progress);
    }
    
    /**
     * Navigate to result fragment when session is completed
     */
    private void navigateToResult(Session session) {
        if (session != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("sessionId", session.getId());
            bundle.putInt("prediction", session.getPrediction());
            bundle.putString("predictionText", session.getPredictionText());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_recordFragment_to_resultFragment, bundle);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (viewModel.getRecordingStatus().getValue() != null && 
                viewModel.getRecordingStatus().getValue()) {
            viewModel.stopRecording();
        }
    }
} 