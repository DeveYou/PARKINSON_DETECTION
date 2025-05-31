package com.parkinson.detection.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.parkinson.detection.api.SessionApiService;

/**
 * Factory for creating RecordViewModel with dependencies
 */
public class RecordViewModelFactory implements ViewModelProvider.Factory {
    
    private final Application application;
    private final SessionApiService apiService;
    
    public RecordViewModelFactory(Application application, SessionApiService apiService) {
        this.application = application;
        this.apiService = apiService;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(application, apiService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
} 