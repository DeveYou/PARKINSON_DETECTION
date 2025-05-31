package mobile_java_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private int prediction; // 0 = No Parkinson's, 1 = Suspected Parkinson's
    
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
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 