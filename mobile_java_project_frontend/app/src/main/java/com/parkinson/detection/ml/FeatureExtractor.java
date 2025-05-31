package com.parkinson.detection.ml;

import android.hardware.SensorEvent;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to extract features from accelerometer and gyroscope sensor data.
 */
public class FeatureExtractor {
    
    // Constants for feature normalization (from training data)
    public static final float[] FEATURE_MEANS = {
            2.16451445e-03F, 4.34967010e-03F, 6.17531195e-04F,  // accel_x_mean, accel_y_mean, accel_z_mean
            7.76659989e-01F, 8.90769845e-01F, 7.51154485e-01F,    // accel_x_std, accel_y_std, accel_z_std
            -4.49073831e-05F, 4.78739464e-04F, 1.99448259e-03F,    // gyro_x_mean, gyro_y_mean, gyro_z_mean
            5.53282868e-01F, 6.29934365e-01F, 4.74331566e-01F, // gyro_x_std, gyro_y_std, gyro_z_std
            7.80763320e+00F, 7.77254842e+00F, 7.70679030e+00F,    // accel_x_fft_peak, accel_y_fft_peak, accel_z_fft_peak
            7.73052468e+00F, 7.77779237e+00F, 7.74355218e+00F,    // gyro_x_fft_peak, gyro_y_fft_peak, gyro_z_fft_peak
            6.38417917e-01F, 5.87826023e-01F, 6.62068710e-01F     // cross_corr_x, cross_corr_y, cross_corr_z
    };
    
    public static final float[] FEATURE_STDS = {
            0.09279512F, 0.12847095F, 0.09247015F,    // accel_x_mean, accel_y_mean, accel_z_mean
            0.41559714F, 0.49850243F, 0.45241221F,    // accel_x_std, accel_y_std, accel_z_std
            0.04470217F, 0.04561358F, 0.04507193F,    // gyro_x_mean, gyro_y_mean, gyro_z_mean
            0.34681894F, 0.36963002F, 0.31590781F,    // gyro_x_std, gyro_y_std, gyro_z_std
            2.55606792F, 2.48898691F, 2.48891113F,    // accel_x_fft_peak, accel_y_fft_peak, accel_z_fft_peak
            2.50131653F, 2.51043773F, 2.47561772F,    // gyro_x_fft_peak, gyro_y_fft_peak, gyro_z_fft_peak
            0.2045824F, 0.25516114F, 0.19698866F     // cross_corr_x, cross_corr_y, cross_corr_z
    };
    
    private List<float[]> accelData = new ArrayList<>();
    private List<float[]> gyroData = new ArrayList<>();
    
    /**
     * Add accelerometer data to the buffer
     */
    public void addAccelerometerData(SensorEvent event) {
        float[] values = new float[3];
        values[0] = event.values[0]; // X
        values[1] = event.values[1]; // Y
        values[2] = event.values[2]; // Z
        accelData.add(values);
    }
    
    /**
     * Add gyroscope data to the buffer
     */
    public void addGyroscopeData(SensorEvent event) {
        float[] values = new float[3];
        values[0] = event.values[0]; // X
        values[1] = event.values[1]; // Y
        values[2] = event.values[2]; // Z
        gyroData.add(values);
    }
    
    /**
     * Clear all collected data
     */
    public void clear() {
        accelData.clear();
        gyroData.clear();
    }
    
    /**
     * Check if enough data has been collected for feature extraction
     */
    public boolean hasEnoughData() {
        // Ensure we have at least 1 second of data at 50Hz
        return accelData.size() >= 50 && gyroData.size() >= 50;
    }
    
    /**
     * Extract all features from the collected data
     * 
     * @return Normalized features ready for model input
     */
    public float[] extractFeatures() {
        if (!hasEnoughData()) {
            throw new IllegalStateException("Not enough data collected for feature extraction");
        }
        
        // Extract raw features
        float[] features = new float[21];
        
        // Get separate axis data
        float[] accelX = new float[accelData.size()];
        float[] accelY = new float[accelData.size()];
        float[] accelZ = new float[accelData.size()];
        
        for (int i = 0; i < accelData.size(); i++) {
            accelX[i] = accelData.get(i)[0];
            accelY[i] = accelData.get(i)[1];
            accelZ[i] = accelData.get(i)[2];
        }
        
        float[] gyroX = new float[gyroData.size()];
        float[] gyroY = new float[gyroData.size()];
        float[] gyroZ = new float[gyroData.size()];
        
        for (int i = 0; i < gyroData.size(); i++) {
            gyroX[i] = gyroData.get(i)[0];
            gyroY[i] = gyroData.get(i)[1];
            gyroZ[i] = gyroData.get(i)[2];
        }
        
        // Calculate means
        features[0] = calculateMean(accelX);
        features[1] = calculateMean(accelY);
        features[2] = calculateMean(accelZ);
        
        // Calculate standard deviations
        features[3] = calculateStd(accelX, features[0]);
        features[4] = calculateStd(accelY, features[1]);
        features[5] = calculateStd(accelZ, features[2]);
        
        // Calculate gyroscope means
        features[6] = calculateMean(gyroX);
        features[7] = calculateMean(gyroY);
        features[8] = calculateMean(gyroZ);
        
        // Calculate gyroscope standard deviations
        features[9] = calculateStd(gyroX, features[6]);
        features[10] = calculateStd(gyroY, features[7]);
        features[11] = calculateStd(gyroZ, features[8]);
        
        // Calculate FFT peaks
        features[12] = calculateFftPeak(accelX);
        features[13] = calculateFftPeak(accelY);
        features[14] = calculateFftPeak(accelZ);
        
        features[15] = calculateFftPeak(gyroX);
        features[16] = calculateFftPeak(gyroY);
        features[17] = calculateFftPeak(gyroZ);
        
        // Calculate cross-correlations
        features[18] = calculateCrossCorrelation(accelX, gyroX);
        features[19] = calculateCrossCorrelation(accelY, gyroY);
        features[20] = calculateCrossCorrelation(accelZ, gyroZ);
        
        // Normalize features
        normalizeFeatures(features);
        
        return features;
    }
    
    /**
     * Normalize features using the training data means and stds
     */
    private void normalizeFeatures(float[] features) {
        for (int i = 0; i < features.length; i++) {
            if (FEATURE_STDS[i] != 0) {
                features[i] = (features[i] - FEATURE_MEANS[i]) / FEATURE_STDS[i];
            } else {
                features[i] = 0;
            }
        }
    }
    
    /**
     * Calculate mean of an array
     */
    private float calculateMean(float[] data) {
        float sum = 0;
        for (float value : data) {
            sum += value;
        }
        return sum / data.length;
    }
    
    /**
     * Calculate standard deviation of an array
     */
    private float calculateStd(float[] data, float mean) {
        float sumSquaredDiff = 0;
        for (float value : data) {
            float diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        return (float) Math.sqrt(sumSquaredDiff / data.length);
    }
    
    /**
     * Calculate the dominant frequency (FFT peak) in the signal
     */
    private float calculateFftPeak(float[] data) {
        // Ensure data length is a power of 2 for FFT
        int nextPowerOf2 = 1;
        while (nextPowerOf2 < data.length) {
            nextPowerOf2 *= 2;
        }
        
        // Pad with zeros
        double[] paddedData = new double[nextPowerOf2];
        for (int i = 0; i < data.length; i++) {
            paddedData[i] = data[i];
        }
        
        // Apply FFT
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] fftResult = transformer.transform(paddedData, TransformType.FORWARD);
        
        // Find the magnitude of the peak (excluding DC component)
        double maxMagnitude = 0;
        for (int i = 1; i < fftResult.length / 2; i++) {
            double magnitude = fftResult[i].abs();
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
            }
        }
        
        return (float) maxMagnitude;
    }
    
    /**
     * Calculate cross-correlation between two signals
     */
    private float calculateCrossCorrelation(float[] signal1, float[] signal2) {
        // Ensure signals are of the same length
        int minLength = Math.min(signal1.length, signal2.length);
        
        double[] s1 = new double[minLength];
        double[] s2 = new double[minLength];
        
        for (int i = 0; i < minLength; i++) {
            s1[i] = signal1[i];
            s2[i] = signal2[i];
        }
        
        // Calculate Pearson correlation coefficient
        PearsonsCorrelation correlation = new PearsonsCorrelation();
        double corr = correlation.correlation(s1, s2);
        
        // Handle NaN or infinite values
        if (Double.isNaN(corr) || Double.isInfinite(corr)) {
            return 0.0f;
        }
        
        return (float) corr;
    }
} 