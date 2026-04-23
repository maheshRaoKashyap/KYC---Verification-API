package com.kyc.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String action;
    private String resource;
    private String ipAddress;
    private String userAgent;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Column(length = 1000)
    private String details;

    @CreationTimestamp
    private LocalDateTime timestamp;

    public enum AuditStatus {
        SUCCESS, FAILURE
    }
}
