package mobile_java_project.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import mobile_java_project.entity.User;
import mobile_java_project.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${server.servlet.context-path}")
    private String contextPath;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.mobile.deep-link:aigest://auth}")
    private String mobileDeepLink;

    @Override
    @Async
    public void sendVerificationEmail(User user) {
        try {
            // Web verification URL
            String webVerificationUrl = frontendUrl + "/verify-email?token=" + user.getVerificationToken();
            
            // Mobile deep link for verification
            String mobileVerificationUrl = mobileDeepLink + "/verify?token=" + user.getVerificationToken();
            
            String subject = "AI-Gest - Please Verify Your Email";
            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<h2>Hello " + user.getFirstName() + ",</h2>"
                    + "<p>Thank you for registering with AI-Gest. Please click on the link below to verify your email address:</p>"
                    + "<p><a href='" + webVerificationUrl + "'>Verify Your Email</a></p>"
                    + "<p>If you're using our mobile app, you can also <a href='" + mobileVerificationUrl + "'>verify on your device</a>.</p>"
                    + "<p>This link will expire in 24 hours.</p>"
                    + "<p>If you did not create an account, please ignore this email.</p>"
                    + "<p>Regards,<br/>The AI-Gest Team</p>"
                    + "</div>";
            
            sendEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user) {
        try {
            // Web reset URL
            String webResetUrl = frontendUrl + "/reset-password?token=" + user.getVerificationToken();
            
            // Mobile deep link for password reset
            String mobileResetUrl = mobileDeepLink + "/reset?token=" + user.getVerificationToken();
            
            String subject = "AI-Gest - Password Reset Request";
            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<h2>Hello " + user.getFirstName() + ",</h2>"
                    + "<p>You have requested to reset your password. Please click on the link below to create a new password:</p>"
                    + "<p><a href='" + webResetUrl + "'>Reset Your Password</a></p>"
                    + "<p>If you're using our mobile app, you can also <a href='" + mobileResetUrl + "'>reset on your device</a>.</p>"
                    + "<p>This link will expire in 2 hours.</p>"
                    + "<p>If you did not request a password reset, please ignore this email or contact support.</p>"
                    + "<p>Regards,<br/>The AI-Gest Team</p>"
                    + "</div>";
            
            sendEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAnalysisResultEmail(User user, Long sessionId, double probabilityScore) {
        try {
            // Web result URL
            String webResultUrl = frontendUrl + "/sessions/" + sessionId + "/analysis";
            
            // Mobile deep link for viewing results
            String mobileResultUrl = mobileDeepLink + "/sessions/" + sessionId + "/analysis";
            
            String subject = "AI-Gest - Analysis Results Available";
            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<h2>Hello " + user.getFirstName() + ",</h2>"
                    + "<p>The analysis for your recent movement session is now complete.</p>"
                    + "<p>Probability Score: <strong>" + String.format("%.2f", probabilityScore) + "</strong></p>"
                    + "<p>To view the detailed results, please click on the link below:</p>"
                    + "<p><a href='" + webResultUrl + "'>View Analysis Results</a></p>"
                    + "<p>If you're using our mobile app, you can also <a href='" + mobileResultUrl + "'>view on your device</a>.</p>"
                    + "<p>Remember, this is not a medical diagnosis. Please consult with a healthcare professional for interpretation.</p>"
                    + "<p>Regards,<br/>The AI-Gest Team</p>"
                    + "</div>";
            
            sendEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            log.error("Failed to send analysis results email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
    
    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        
        mailSender.send(message);
        log.info("Email sent to {} with subject: {}", to, subject);
    }
}