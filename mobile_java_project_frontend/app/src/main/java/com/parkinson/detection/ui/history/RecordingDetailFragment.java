package com.parkinson.detection.ui.history;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.parkinson.detection.R;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.model.Session;
import com.parkinson.detection.repository.SessionRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordingDetailFragment extends Fragment {

    private long recordingId;
    private TextView textDate;
    private TextView textResult;
    private TextView textAcceleration;
    private TextView textGyroscope;
    private LineChart accelerometerChart;
    private LineChart gyroscopeChart;
    private SessionRepository sessionRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - HH:mm", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            recordingId = getArguments().getLong("recordingId", -1);
        }
        
        // Initialize repository
        SessionApiService apiService = ApiClient.getInstance().createService(SessionApiService.class);
        sessionRepository = new SessionRepository(requireContext(), apiService);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recording_detail, container, false);
        
        // Find views
        textDate = root.findViewById(R.id.text_date);
        textResult = root.findViewById(R.id.text_result);
        textAcceleration = root.findViewById(R.id.text_acceleration);
        textGyroscope = root.findViewById(R.id.text_gyroscope);
        accelerometerChart = root.findViewById(R.id.accelerometer_chart);
        gyroscopeChart = root.findViewById(R.id.gyroscope_chart);
        
        // Initialize charts
        setupChart(accelerometerChart, "Accelerometer Data");
        setupChart(gyroscopeChart, "Gyroscope Data");
        
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Load session data
        loadSessionDetails();
    }
    
    private void loadSessionDetails() {
        // This would ideally be in a ViewModel, but for simplicity we'll do it here
        Thread thread = new Thread(() -> {
            Session session = sessionRepository.getSessionById(recordingId);
            if (session != null) {
                requireActivity().runOnUiThread(() -> displaySessionDetails(session));
            }
        });
        thread.start();
    }
    
    private void displaySessionDetails(Session session) {
        textDate.setText(session.getTimestamp().format(formatter));
        
        String resultText = session.getPrediction() == 0 ? 
                getString(R.string.no_parkinsons) : 
                getString(R.string.suspected_parkinsons);
        textResult.setText(resultText);
        
        // Format acceleration data
        StringBuilder accelBuilder = new StringBuilder();
        accelBuilder.append("X Mean: ").append(formatDouble(session.getAccelXMean())).append("\n");
        accelBuilder.append("Y Mean: ").append(formatDouble(session.getAccelYMean())).append("\n");
        accelBuilder.append("Z Mean: ").append(formatDouble(session.getAccelZMean())).append("\n");
        accelBuilder.append("X Std: ").append(formatDouble(session.getAccelXStd())).append("\n");
        accelBuilder.append("Y Std: ").append(formatDouble(session.getAccelYStd())).append("\n");
        accelBuilder.append("Z Std: ").append(formatDouble(session.getAccelZStd())).append("\n");
        textAcceleration.setText(accelBuilder.toString());
        
        // Format gyroscope data
        StringBuilder gyroBuilder = new StringBuilder();
        gyroBuilder.append("X Mean: ").append(formatDouble(session.getGyroXMean())).append("\n");
        gyroBuilder.append("Y Mean: ").append(formatDouble(session.getGyroYMean())).append("\n");
        gyroBuilder.append("Z Mean: ").append(formatDouble(session.getGyroZMean())).append("\n");
        gyroBuilder.append("X Std: ").append(formatDouble(session.getGyroXStd())).append("\n");
        gyroBuilder.append("Y Std: ").append(formatDouble(session.getGyroYStd())).append("\n");
        gyroBuilder.append("Z Std: ").append(formatDouble(session.getGyroZStd())).append("\n");
        textGyroscope.setText(gyroBuilder.toString());
        
        // Populate charts with data
        populateAccelerometerChart(session);
        populateGyroscopeChart(session);
    }
    
    private void setupChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        
        // Set empty data initially to avoid "no chart data available" message
        chart.setData(new LineData());
        chart.invalidate();
        
        // Style x-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        // Style y-axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
    }
    
    private void populateAccelerometerChart(Session session) {
        // Create sample data points (since we don't have actual time series data)
        // In a real app, you would use actual data points from session
        List<Entry> entriesX = new ArrayList<>();
        List<Entry> entriesY = new ArrayList<>();
        List<Entry> entriesZ = new ArrayList<>();
        
        // Generate some sample data based on mean and std values
        Double xMean = session.getAccelXMean();
        Double yMean = session.getAccelYMean();
        Double zMean = session.getAccelZMean();
        
        if (xMean != null && yMean != null && zMean != null) {
            for (int i = 0; i < 10; i++) {
                // Create some variation around the mean values for demonstration
                entriesX.add(new Entry(i, xMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
                entriesY.add(new Entry(i, yMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
                entriesZ.add(new Entry(i, zMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
            }
            
            LineDataSet setX = new LineDataSet(entriesX, "X-axis");
            setX.setColor(getResources().getColor(R.color.accelerometer_x));
            setX.setLineWidth(2f);
            setX.setDrawCircles(false);
            setX.setDrawValues(false);
            setX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineDataSet setY = new LineDataSet(entriesY, "Y-axis");
            setY.setColor(getResources().getColor(R.color.accelerometer_y));
            setY.setLineWidth(2f);
            setY.setDrawCircles(false);
            setY.setDrawValues(false);
            setY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineDataSet setZ = new LineDataSet(entriesZ, "Z-axis");
            setZ.setColor(getResources().getColor(R.color.accelerometer_z));
            setZ.setLineWidth(2f);
            setZ.setDrawCircles(false);
            setZ.setDrawValues(false);
            setZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineData data = new LineData(setX, setY, setZ);
            accelerometerChart.setData(data);
            accelerometerChart.invalidate();
        }
    }
    
    private void populateGyroscopeChart(Session session) {
        // Create sample data points (since we don't have actual time series data)
        List<Entry> entriesX = new ArrayList<>();
        List<Entry> entriesY = new ArrayList<>();
        List<Entry> entriesZ = new ArrayList<>();
        
        // Generate some sample data based on mean and std values
        Double xMean = session.getGyroXMean();
        Double yMean = session.getGyroYMean();
        Double zMean = session.getGyroZMean();
        
        if (xMean != null && yMean != null && zMean != null) {
            for (int i = 0; i < 10; i++) {
                // Create some variation around the mean values for demonstration
                entriesX.add(new Entry(i, xMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
                entriesY.add(new Entry(i, yMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
                entriesZ.add(new Entry(i, zMean.floatValue() + (float) (Math.random() * 0.5 - 0.25)));
            }
            
            LineDataSet setX = new LineDataSet(entriesX, "X-axis");
            setX.setColor(getResources().getColor(R.color.gyroscope_x));
            setX.setLineWidth(2f);
            setX.setDrawCircles(false);
            setX.setDrawValues(false);
            setX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineDataSet setY = new LineDataSet(entriesY, "Y-axis");
            setY.setColor(getResources().getColor(R.color.gyroscope_y));
            setY.setLineWidth(2f);
            setY.setDrawCircles(false);
            setY.setDrawValues(false);
            setY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineDataSet setZ = new LineDataSet(entriesZ, "Z-axis");
            setZ.setColor(getResources().getColor(R.color.gyroscope_z));
            setZ.setLineWidth(2f);
            setZ.setDrawCircles(false);
            setZ.setDrawValues(false);
            setZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            
            LineData data = new LineData(setX, setY, setZ);
            gyroscopeChart.setData(data);
            gyroscopeChart.invalidate();
        }
    }
    
    private String formatDouble(Double value) {
        if (value == null) {
            return "N/A";
        }
        return String.format(Locale.getDefault(), "%.2f", value);
    }
} 