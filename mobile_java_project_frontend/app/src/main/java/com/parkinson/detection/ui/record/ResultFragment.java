package com.parkinson.detection.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.parkinson.detection.R;

/**
 * Fragment for displaying test results
 */
public class ResultFragment extends Fragment {
    
    private long sessionId;
    private int prediction;
    private String predictionText;
    
    private TextView resultStatusText;
    private TextView resultDescriptionText;
    private ImageView resultIcon;
    private Button doneButton;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            sessionId = getArguments().getLong("sessionId");
            prediction = getArguments().getInt("prediction");
            predictionText = getArguments().getString("predictionText");
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        resultStatusText = view.findViewById(R.id.result_status);
        resultDescriptionText = view.findViewById(R.id.result_description);
        resultIcon = view.findViewById(R.id.result_icon);
        doneButton = view.findViewById(R.id.btn_done);
        
        // Display result
        displayResult();
        
        // Set up done button
        doneButton.setOnClickListener(v -> {
            // Navigate back to home screen
            Navigation.findNavController(view).navigate(R.id.navigation_home);
        });
    }
    
    /**
     * Display the test result
     */
    private void displayResult() {
        // Set result text
        resultStatusText.setText(predictionText);
        
        // Set appropriate icon and description based on prediction
        if (prediction == 0) {
            // No Parkinson's
            resultIcon.setImageResource(android.R.drawable.ic_dialog_info);
            resultDescriptionText.setText(R.string.result_no_parkinsons);
        } else {
            // Suspected Parkinson's
            resultIcon.setImageResource(android.R.drawable.ic_dialog_alert);
            resultDescriptionText.setText(R.string.result_suspected_parkinsons);
        }
    }
} 