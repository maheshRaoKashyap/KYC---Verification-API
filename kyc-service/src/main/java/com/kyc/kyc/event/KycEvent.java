package com.kyc.kyc.event;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KycEvent {
    private String eventType;       // KYC_CREATED, KYC_UPDATED, KYC_STATUS_CHANGED
    private Long kycId;
    private Long userId;
    private String email;
    private String fullName;
    private String oldStatus;
    private String newStatus;
    private String rejectionReason;
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
