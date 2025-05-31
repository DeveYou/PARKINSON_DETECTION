package com.parkinson.detection.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

import com.parkinson.detection.db.DateTimeConverter;

/**
 * Model class representing a session with motion data and prediction results
 */
@Entity(tableName = "sessions")
@TypeConverters(DateTimeConverter.class)
public class Session {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    
    private Long userId;
    private LocalDateTime timestamp;
    private int prediction; // 0 = No Parkinson's, 1 = Suspected Parkinson's
    private String predictionText;
    private Long durationMs; // Duration in milliseconds
    
    // Accelerometer features
    private Double accelXMean;
    private Double accelYMean;
    private Double accelZMean;
    
    private Double accelXStd;
    private Double accelYStd;
    private Double accelZStd;
    
    private Double accelXFftPeak;
    private Double accelYFftPeak;
    private Double accelZFftPeak;
    
    // Gyroscope features
    private Double gyroXMean;
    private Double gyroYMean;
    private Double gyroZMean;
    
    private Double gyroXStd;
    private Double gyroYStd;
    private Double gyroZStd;
    
    private Double gyroXFftPeak;
    private Double gyroYFftPeak;
    private Double gyroZFftPeak;
    
    // Cross-correlation features
    private Double crossCorrX;
    private Double crossCorrY;
    private Double crossCorrZ;
    
    private LocalDateTime createdAt;
    
    // For local caching and sync status
    private boolean isSynced;

    public Session() {
        this.timestamp = LocalDateTime.now();
    }

    @Ignore
    public Session(Long id, Long userId, LocalDateTime timestamp, int prediction) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.prediction = prediction;
        this.predictionText = prediction == 0 ? "No Parkinson's" : "Suspected Parkinson's";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrediction() {
        return prediction;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
        this.predictionText = prediction == 0 ? "No Parkinson's" : "Suspected Parkinson's";
    }

    public String getPredictionText() {
        return predictionText;
    }

    public void setPredictionText(String predictionText) {
        this.predictionText = predictionText;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Double getAccelXMean() {
        return accelXMean;
    }

    public void setAccelXMean(Double accelXMean) {
        this.accelXMean = accelXMean;
    }

    public Double getAccelYMean() {
        return accelYMean;
    }

    public void setAccelYMean(Double accelYMean) {
        this.accelYMean = accelYMean;
    }

    public Double getAccelZMean() {
        return accelZMean;
    }

    public void setAccelZMean(Double accelZMean) {
        this.accelZMean = accelZMean;
    }

    public Double getAccelXStd() {
        return accelXStd;
    }

    public void setAccelXStd(Double accelXStd) {
        this.accelXStd = accelXStd;
    }

    public Double getAccelYStd() {
        return accelYStd;
    }

    public void setAccelYStd(Double accelYStd) {
        this.accelYStd = accelYStd;
    }

    public Double getAccelZStd() {
        return accelZStd;
    }

    public void setAccelZStd(Double accelZStd) {
        this.accelZStd = accelZStd;
    }

    public Double getAccelXFftPeak() {
        return accelXFftPeak;
    }

    public void setAccelXFftPeak(Double accelXFftPeak) {
        this.accelXFftPeak = accelXFftPeak;
    }

    public Double getAccelYFftPeak() {
        return accelYFftPeak;
    }

    public void setAccelYFftPeak(Double accelYFftPeak) {
        this.accelYFftPeak = accelYFftPeak;
    }

    public Double getAccelZFftPeak() {
        return accelZFftPeak;
    }

    public void setAccelZFftPeak(Double accelZFftPeak) {
        this.accelZFftPeak = accelZFftPeak;
    }

    public Double getGyroXMean() {
        return gyroXMean;
    }

    public void setGyroXMean(Double gyroXMean) {
        this.gyroXMean = gyroXMean;
    }

    public Double getGyroYMean() {
        return gyroYMean;
    }

    public void setGyroYMean(Double gyroYMean) {
        this.gyroYMean = gyroYMean;
    }

    public Double getGyroZMean() {
        return gyroZMean;
    }

    public void setGyroZMean(Double gyroZMean) {
        this.gyroZMean = gyroZMean;
    }

    public Double getGyroXStd() {
        return gyroXStd;
    }

    public void setGyroXStd(Double gyroXStd) {
        this.gyroXStd = gyroXStd;
    }

    public Double getGyroYStd() {
        return gyroYStd;
    }

    public void setGyroYStd(Double gyroYStd) {
        this.gyroYStd = gyroYStd;
    }

    public Double getGyroZStd() {
        return gyroZStd;
    }

    public void setGyroZStd(Double gyroZStd) {
        this.gyroZStd = gyroZStd;
    }

    public Double getGyroXFftPeak() {
        return gyroXFftPeak;
    }

    public void setGyroXFftPeak(Double gyroXFftPeak) {
        this.gyroXFftPeak = gyroXFftPeak;
    }

    public Double getGyroYFftPeak() {
        return gyroYFftPeak;
    }

    public void setGyroYFftPeak(Double gyroYFftPeak) {
        this.gyroYFftPeak = gyroYFftPeak;
    }

    public Double getGyroZFftPeak() {
        return gyroZFftPeak;
    }

    public void setGyroZFftPeak(Double gyroZFftPeak) {
        this.gyroZFftPeak = gyroZFftPeak;
    }

    public Double getCrossCorrX() {
        return crossCorrX;
    }

    public void setCrossCorrX(Double crossCorrX) {
        this.crossCorrX = crossCorrX;
    }

    public Double getCrossCorrY() {
        return crossCorrY;
    }

    public void setCrossCorrY(Double crossCorrY) {
        this.crossCorrY = crossCorrY;
    }

    public Double getCrossCorrZ() {
        return crossCorrZ;
    }

    public void setCrossCorrZ(Double crossCorrZ) {
        this.crossCorrZ = crossCorrZ;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    /**
     * Set all the feature values at once from the extracted features array
     * 
     * @param features Array of extracted features
     */
    public void setFeatures(float[] features) {
        this.accelXMean = (double) features[0];
        this.accelYMean = (double) features[1];
        this.accelZMean = (double) features[2];
        
        this.accelXStd = (double) features[3];
        this.accelYStd = (double) features[4];
        this.accelZStd = (double) features[5];
        
        this.gyroXMean = (double) features[6];
        this.gyroYMean = (double) features[7];
        this.gyroZMean = (double) features[8];
        
        this.gyroXStd = (double) features[9];
        this.gyroYStd = (double) features[10];
        this.gyroZStd = (double) features[11];
        
        this.accelXFftPeak = (double) features[12];
        this.accelYFftPeak = (double) features[13];
        this.accelZFftPeak = (double) features[14];
        
        this.gyroXFftPeak = (double) features[15];
        this.gyroYFftPeak = (double) features[16];
        this.gyroZFftPeak = (double) features[17];
        
        this.crossCorrX = (double) features[18];
        this.crossCorrY = (double) features[19];
        this.crossCorrZ = (double) features[20];
    }
} 