package mobile_java_project.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    
    private Long id;
    private Long userId;
    private LocalDateTime timestamp;
    private int prediction;
    private String predictionText; // "No Parkinson's" or "Suspected Parkinson's"
    
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
} 