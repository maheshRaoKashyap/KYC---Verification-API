package com.kyc.kyc.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String email;

    // Document details
    private String aadhaarNumber;
    private String panNumber;
    private String voterIdNumber;
    private String passportNumber;
    private String drivingLicenseNumber;

    // Personal details
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String fatherName;
    private String motherName;
    private String nationality;

    // Address
    private String permanentAddress;
    private String currentAddress;
    private String pinCode;
    private String district;
    private String state;
    private String country;

    // Financial
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String annualIncome;
    private String sourceOfFunds;

    // Document verification flags
    @Builder.Default
    private Boolean aadhaarVerified = false;

    @Builder.Default
    private Boolean panVerified = false;

    @Builder.Default
    private Boolean addressVerified = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KycStatus status = KycStatus.PENDING;

    private String rejectionReason;
    private Long verifiedBy;
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum KycStatus {
        PENDING, UNDER_REVIEW, VERIFIED, REJECTED, EXPIRED
    }
}