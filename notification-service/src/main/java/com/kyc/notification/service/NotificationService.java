package com.kyc.notification.service;

import com.kyc.notification.dto.KycEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@kycplatform.com}")
    private String fromEmail;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    public void processKycEvent(KycEvent event) {
        log.info("Processing KYC event: type={}, userId={}, status={}",
            event.getEventType(), event.getUserId(), event.getNewStatus());

        switch (event.getEventType()) {
            case "KYC_CREATED"       -> sendKycCreatedNotification(event);
            case "KYC_UPDATED"       -> sendKycUpdatedNotification(event);
            case "KYC_STATUS_CHANGED"-> sendKycStatusChangedNotification(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void sendKycCreatedNotification(KycEvent event) {
        String subject = "KYC Submission Received - KYC Platform";
        String body = String.format("""
            Dear %s,
            
            Your KYC submission has been received successfully.
            
            Reference ID: KYC-%d
            Status: PENDING REVIEW
            
            Our team will review your documents within 2-3 business days.
            You will receive an email once verification is complete.
            
            Thank you for choosing KYC Platform.
            
            Regards,
            KYC Platform Team
            """, event.getFullName(), event.getKycId());

        sendEmail(event.getEmail(), subject, body);
    }

    private void sendKycUpdatedNotification(KycEvent event) {
        String subject = "KYC Details Updated - KYC Platform";
        String body = String.format("""
            Dear %s,
            
            Your KYC details have been updated successfully.
            
            Reference ID: KYC-%d
            Current Status: %s
            
            Your application will be reviewed shortly.
            
            Regards,
            KYC Platform Team
            """, event.getFullName(), event.getKycId(), event.getNewStatus());

        sendEmail(event.getEmail(), subject, body);
    }

    private void sendKycStatusChangedNotification(KycEvent event) {
        String subject = "KYC Status Update - " + event.getNewStatus();
        String body;

        if ("VERIFIED".equals(event.getNewStatus())) {
            body = String.format("""
                Dear %s,
                
                Congratulations! Your KYC verification is complete.
                
                Reference ID: KYC-%d
                Status: ✅ VERIFIED
                
                You now have full access to all platform features.
                
                Regards,
                KYC Platform Team
                """, event.getFullName(), event.getKycId());
        } else if ("REJECTED".equals(event.getNewStatus())) {
            body = String.format("""
                Dear %s,
                
                Unfortunately, your KYC verification could not be completed.
                
                Reference ID: KYC-%d
                Status: ❌ REJECTED
                Reason: %s
                
                Please update your KYC details and resubmit.
                Login to the platform to make corrections.
                
                Regards,
                KYC Platform Team
                """, event.getFullName(), event.getKycId(),
                    event.getRejectionReason() != null ? event.getRejectionReason() : "Documents incomplete");
        } else {
            body = String.format("Dear %s, your KYC status has been updated to: %s",
                event.getFullName(), event.getNewStatus());
        }

        sendEmail(event.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email notification (disabled): to={}, subject={}", to, subject);
            log.debug("Email body:\n{}", body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
