package com.parkinson.detection.ml;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

/**
 * TensorFlow Lite model wrapper for Parkinson's detection
 */
public class ParkinsonDetectionModel {
    private static final String TAG = "ParkinsonDetectionModel";
    private static final String MODEL_PATH = "parkinsons_model.tflite";
    
    private final Interpreter interpreter;
    private static ParkinsonDetectionModel instance;
    
    // Number of features expected by the model
    private static final int FEATURE_COUNT = 21;
    
    /**
     * Get singleton instance of the model
     */
    public static synchronized ParkinsonDetectionModel getInstance(Context context) {
        if (instance == null) {
            try {
                instance = new ParkinsonDetectionModel(context);
            } catch (IOException e) {
                Log.e(TAG, "Error initializing model", e);
                return null;
            }
        }
        return instance;
    }
    
    private ParkinsonDetectionModel(Context context) throws IOException {
        // Load the TFLite model
        MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH);
        interpreter = new Interpreter(modelBuffer);
        Log.d(TAG, "TensorFlow Lite model loaded successfully");
    }
    
    /**
     * Run inference on the provided features
     * 
     * @param features Array of features in the order expected by the model
     * @return Prediction (0 = No Parkinson's, 1 = Suspected Parkinson's)
     */
    public int predict(float[] features) {
        if (features.length != FEATURE_COUNT) {
            throw new IllegalArgumentException("Expected " + FEATURE_COUNT + " features, got " + features.length);
        }
        
        // Prepare input buffer
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(FEATURE_COUNT * 4); // 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder());
        
        // Fill input buffer with features
        for (float feature : features) {
            inputBuffer.putFloat(feature);
        }
        
        // Reset the position to the beginning of the buffer
        inputBuffer.rewind();
        
        // Prepare output buffer (single float for binary classification)
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(4); // 4 bytes for a single float
        outputBuffer.order(ByteOrder.nativeOrder());
        
        // Run inference
        interpreter.run(inputBuffer, outputBuffer);
        
        // Reset the position to the beginning of the buffer
        outputBuffer.rewind();
        
        // Get the prediction result
        float result = outputBuffer.getFloat();
        
        // Convert to binary class (0 or 1) using threshold of 0.5
        return result >= 0.5f ? 1 : 0;
    }
    
    /**
     * Release resources used by the model
     */
    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
        instance = null;
    }
} 