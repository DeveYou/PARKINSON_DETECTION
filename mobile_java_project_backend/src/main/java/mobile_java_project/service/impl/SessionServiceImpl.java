package mobile_java_project.service.impl;

import lombok.RequiredArgsConstructor;
import mobile_java_project.dto.session.SessionCreateRequest;
import mobile_java_project.dto.session.SessionResponse;
import mobile_java_project.entity.Session;
import mobile_java_project.entity.User;
import mobile_java_project.exception.ResourceNotFoundException;
import mobile_java_project.repository.SessionRepository;
import mobile_java_project.repository.UserRepository;
import mobile_java_project.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SessionResponse saveSession(Long userId, SessionCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Session session = Session.builder()
                .user(user)
                .timestamp(request.getTimestamp())
                .prediction(request.getPrediction())
                .accelXMean(request.getAccelXMean())
                .accelYMean(request.getAccelYMean())
                .accelZMean(request.getAccelZMean())
                .accelXStd(request.getAccelXStd())
                .accelYStd(request.getAccelYStd())
                .accelZStd(request.getAccelZStd())
                .accelXFftPeak(request.getAccelXFftPeak())
                .accelYFftPeak(request.getAccelYFftPeak())
                .accelZFftPeak(request.getAccelZFftPeak())
                .gyroXMean(request.getGyroXMean())
                .gyroYMean(request.getGyroYMean())
                .gyroZMean(request.getGyroZMean())
                .gyroXStd(request.getGyroXStd())
                .gyroYStd(request.getGyroYStd())
                .gyroZStd(request.getGyroZStd())
                .gyroXFftPeak(request.getGyroXFftPeak())
                .gyroYFftPeak(request.getGyroYFftPeak())
                .gyroZFftPeak(request.getGyroZFftPeak())
                .crossCorrX(request.getCrossCorrX())
                .crossCorrY(request.getCrossCorrY())
                .crossCorrZ(request.getCrossCorrZ())
                .build();

        Session savedSession = sessionRepository.save(session);
        return mapToSessionResponse(savedSession);
    }

    @Override
    public SessionResponse getSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        return mapToSessionResponse(session);
    }

    @Override
    public List<SessionResponse> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return sessionRepository.findByUserOrderByTimestampDesc(user)
                .stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SessionResponse> getUserSessionsPaginated(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return sessionRepository.findByUser(user, pageable)
                .map(this::mapToSessionResponse);
    }

    @Override
    public Page<SessionResponse> getUserSessionsByPrediction(Long userId, int prediction, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return sessionRepository.findByUserAndPrediction(user, prediction, pageable)
                .map(this::mapToSessionResponse);
    }
    
    private SessionResponse mapToSessionResponse(Session session) {
        String predictionText = session.getPrediction() == 0 ? "No Parkinson's" : "Suspected Parkinson's";
        
        return SessionResponse.builder()
                .id(session.getId())
                .userId(session.getUser().getId())
                .timestamp(session.getTimestamp())
                .prediction(session.getPrediction())
                .predictionText(predictionText)
                .accelXMean(session.getAccelXMean())
                .accelYMean(session.getAccelYMean())
                .accelZMean(session.getAccelZMean())
                .accelXStd(session.getAccelXStd())
                .accelYStd(session.getAccelYStd())
                .accelZStd(session.getAccelZStd())
                .accelXFftPeak(session.getAccelXFftPeak())
                .accelYFftPeak(session.getAccelYFftPeak())
                .accelZFftPeak(session.getAccelZFftPeak())
                .gyroXMean(session.getGyroXMean())
                .gyroYMean(session.getGyroYMean())
                .gyroZMean(session.getGyroZMean())
                .gyroXStd(session.getGyroXStd())
                .gyroYStd(session.getGyroYStd())
                .gyroZStd(session.getGyroZStd())
                .gyroXFftPeak(session.getGyroXFftPeak())
                .gyroYFftPeak(session.getGyroYFftPeak())
                .gyroZFftPeak(session.getGyroZFftPeak())
                .crossCorrX(session.getCrossCorrX())
                .crossCorrY(session.getCrossCorrY())
                .crossCorrZ(session.getCrossCorrZ())
                .createdAt(session.getCreatedAt())
                .build();
    }
} 