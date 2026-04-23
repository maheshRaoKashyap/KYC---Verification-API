package com.kyc.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KycEvent {
    private String eventType;
    private Long kycId;
    private Long userId;
    private String email;
    private String fullName;
    private String oldStatus;
    private String newStatus;
    private String rejectionReason;
    private LocalDateTime eventTime;
}
