package com.parkinson.detection.ui.record;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.ml.FeatureExtractor;
import com.parkinson.detection.ml.ParkinsonDetectionModel;
import com.parkinson.detection.model.Session;
import com.parkinson.detection.repository.SessionRepository;

import static android.content.Context.SENSOR_SERVICE;

/**
 * ViewModel for the recording screen
 */
public class RecordViewModel extends AndroidViewModel implements SensorEventListener {
    private static final String TAG = "RecordViewModel";
    
    // Sensor data class for UI updates
    public static class SensorData {
        public float accelX;
        public float accelY;
        public float accelZ;
        public float gyroX;
        public float gyroY;
        public float gyroZ;
        
        public SensorData(float accelX, float accelY, float accelZ, 
                         float gyroX, float gyroY, float gyroZ) {
            this.accelX = accelX;
            this.accelY = accelY;
            this.accelZ = accelZ;
            this.gyroX = gyroX;
            this.gyroY = gyroY;
            this.gyroZ = gyroZ;
        }
    }
    
    // Sensor related
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor gyroscope;
    private boolean isRecording = false;
    
    // For tracking recording duration
    private long recordingStartTime = 0;
    
    // Latest sensor values
    private float lastAccelX = 0;
    private float lastAccelY = 0;
    private float lastAccelZ = 0;
    private float lastGyroX = 0;
    private float lastGyroY = 0;
    private float lastGyroZ = 0;
    
    // Data processing
    private final FeatureExtractor featureExtractor = new FeatureExtractor();
    private ParkinsonDetectionModel model;
    
    // Repository for saving data
    private final SessionRepository sessionRepository;
    
    // LiveData for UI updates
    private final MutableLiveData<Boolean> recordingStatus = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> dataPointsCollected = new MutableLiveData<>(0);
    private final MutableLiveData<Session> sessionResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<SensorData> sensorData = new MutableLiveData<>();
    
    // Counter for UI updates (to avoid updating UI too frequently)
    private int dataPointCounter = 0;
    
    public RecordViewModel(@NonNull Application application, SessionApiService apiService) {
        super(application);
        
        // Initialize sensors
        sensorManager = (SensorManager) application.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        // Initialize model
        try {
            model = ParkinsonDetectionModel.getInstance(application.getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing model", e);
            errorMessage.setValue("Error initializing model: " + e.getMessage());
        }
        
        // Initialize repository
        sessionRepository = new SessionRepository(application.getApplicationContext(), apiService);
    }
    
    /**
     * Start recording sensor data
     */
    public void startRecording() {
        if (isRecording) {
            return;
        }
        
        // Clear previous data
        featureExtractor.clear();
        dataPointCounter = 0;
        dataPointsCollected.setValue(0);
        
        // Set recording start time
        recordingStartTime = System.currentTimeMillis();
        
        // Register sensor listeners
        boolean accelSuccess = sensorManager.registerListener(
                this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        boolean gyroSuccess = sensorManager.registerListener(
                this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        
        if (!accelSuccess || !gyroSuccess) {
            errorMessage.setValue("Failed to start sensors. Please try again.");
            return;
        }
        
        isRecording = true;
        recordingStatus.setValue(true);
        Log.d(TAG, "Recording started");
    }
    
    /**
     * Stop recording and process data
     */
    public void stopRecording() {
        if (!isRecording) {
            return;
        }
        
        // Calculate recording duration
        long recordingDuration = System.currentTimeMillis() - recordingStartTime;
        
        // Unregister sensor listeners
        sensorManager.unregisterListener(this);
        isRecording = false;
        recordingStatus.setValue(false);
        Log.d(TAG, "Recording stopped");
        
        // Process data and run inference with duration
        processData(recordingDuration);
    }
    
    /**
     * Process collected sensor data, extract features, and run model inference
     */
    private void processData(long durationMs) {
        // Check if we have enough data
        if (!featureExtractor.hasEnoughData()) {
            errorMessage.setValue("Not enough data collected. Please record for longer.");
            return;
        }
        
        try {
            // Check if model is null
            if (model == null) {
                errorMessage.setValue("Model not initialized. Please restart the app.");
                return;
            }
            
            // Extract features
            float[] features = featureExtractor.extractFeatures();
            
            // Run model inference
            int prediction = model.predict(features);
            
            // Create session object
            Session session = new Session();
            session.setFeatures(features);
            session.setPrediction(prediction);
            session.setSynced(false);
            session.setDurationMs(durationMs);
            
            // Save session
            sessionRepository.saveSession(session).observeForever(savedSession -> {
                if (savedSession != null) {
                    Log.d(TAG, "Session saved with prediction: " + prediction);
                    sessionResult.postValue(savedSession);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing data", e);
            errorMessage.setValue("Error processing data: " + e.getMessage());
        }
    }
    
    /**
     * Handle sensor data
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isRecording) {
            return;
        }
        
        // Add data to feature extractor
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            featureExtractor.addAccelerometerData(event);
            
            // Store values for UI updates
            lastAccelX = event.values[0];
            lastAccelY = event.values[1];
            lastAccelZ = event.values[2];
            
            // Update UI counter occasionally (not every data point to avoid UI lag)
            dataPointCounter++;
            if (dataPointCounter % 10 == 0) {
                dataPointsCollected.postValue(dataPointCounter);
                // Update sensor data in UI
                updateSensorDataUI();
            }
            
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            featureExtractor.addGyroscopeData(event);
            
            // Store values for UI updates
            lastGyroX = event.values[0];
            lastGyroY = event.values[1];
            lastGyroZ = event.values[2];
        }
    }
    
    /**
     * Update sensor data in the UI
     */
    private void updateSensorDataUI() {
        SensorData data = new SensorData(
                lastAccelX, lastAccelY, lastAccelZ,
                lastGyroX, lastGyroY, lastGyroZ);
        sensorData.postValue(data);
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
    
    /**
     * Clean up resources when ViewModel is cleared
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (isRecording) {
            sensorManager.unregisterListener(this);
        }
        if (model != null) {
            model.close();
        }
    }
    
    // Getters for LiveData
    
    public LiveData<Boolean> getRecordingStatus() {
        return recordingStatus;
    }
    
    public LiveData<Integer> getDataPointsCollected() {
        return dataPointsCollected;
    }
    
    public LiveData<Session> getSessionResult() {
        return sessionResult;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<SensorData> getSensorData() {
        return sensorData;
    }

    /**
     * Update the current recording duration (called from RecordFragment)
     * 
     * @param durationMs Current duration in milliseconds
     */
    public void updateRecordingDuration(long durationMs) {
        // This method is called from the UI to update the duration
        // No need to store in a variable as we calculate it when recording stops
    }
} 