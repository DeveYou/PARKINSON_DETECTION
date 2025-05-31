package mobile_java_project.service;

import mobile_java_project.entity.User;

public interface EmailService {
    
    void sendVerificationEmail(User user);
    
    void sendPasswordResetEmail(User user);
    
    void sendAnalysisResultEmail(User user, Long sessionId, double probabilityScore);
} 