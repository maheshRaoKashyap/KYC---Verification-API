package com.kyc.kyc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kyc.kyc.entity.KycDetail;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class KycDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateKycRequest {
        @NotNull private Long userId;
        @NotBlank private String email;
        private String aadhaarNumber;
        private String panNumber;
        private String voterIdNumber;
        private String passportNumber;
        private String drivingLicenseNumber;
        @NotBlank private String fullName;
        @NotBlank private String dateOfBirth;
        private String gender;
        private String fatherName;
        private String motherName;
        private String nationality;
        private String permanentAddress;
        private String currentAddress;
        private String pinCode;
        private String district;
        @NotBlank private String state;
        @NotBlank private String country;
        private String bankAccountNumber;
        private String ifscCode;
        private String bankName;
        private String annualIncome;
        private String sourceOfFunds;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateKycRequest {
        private String aadhaarNumber;
        private String panNumber;
        private String voterIdNumber;
        private String passportNumber;
        private String fullName;
        private String dateOfBirth;
        private String gender;
        private String permanentAddress;
        private String currentAddress;
        private String pinCode;
        private String district;
        private String state;
        private String bankAccountNumber;
        private String ifscCode;
        private String bankName;
        private String annualIncome;
        private String sourceOfFunds;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateKycStatusRequest {
        @NotNull private KycDetail.KycStatus status;
        private String rejectionReason;
        private Long verifiedBy;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KycResponse {
        private Long id;
        private Long userId;
        private String email;
        private String fullName;
        private String dateOfBirth;
        private String gender;
        private String nationality;
        private String aadhaarNumber;
        private String panNumber;
        private String voterIdNumber;
        private String passportNumber;
        private String state;
        private String country;
        private String bankName;
        private String annualIncome;
        private Boolean aadhaarVerified;
        private Boolean panVerified;
        private Boolean addressVerified;
        private String status;
        private String rejectionReason;
        private LocalDateTime verifiedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static KycResponse from(KycDetail k) {
            return KycResponse.builder()
                .id(k.getId()).userId(k.getUserId()).email(k.getEmail())
                .fullName(k.getFullName()).dateOfBirth(k.getDateOfBirth())
                .gender(k.getGender()).nationality(k.getNationality())
                .aadhaarNumber(mask(k.getAadhaarNumber(), 4))
                .panNumber(k.getPanNumber())
                .voterIdNumber(k.getVoterIdNumber())
                .passportNumber(k.getPassportNumber())
                .state(k.getState()).country(k.getCountry())
                .bankName(k.getBankName()).annualIncome(k.getAnnualIncome())
                .aadhaarVerified(k.getAadhaarVerified())
                .panVerified(k.getPanVerified())
                .addressVerified(k.getAddressVerified())
                .status(k.getStatus().name())
                .rejectionReason(k.getRejectionReason())
                .verifiedAt(k.getVerifiedAt())
                .createdAt(k.getCreatedAt()).updatedAt(k.getUpdatedAt())
                .build();
        }

        private static String mask(String val, int show) {
            if (val == null || val.length() <= show) return val;
            String clean = val.replaceAll("\\s", "");
            return "*".repeat(clean.length() - show) + clean.substring(clean.length() - show);
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private String errorCode;
        @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();

        public static <T> ApiResponse<T> success(T data, String message) {
            return ApiResponse.<T>builder().success(true).message(message).data(data).build();
        }
        public static <T> ApiResponse<T> error(String message, String errorCode) {
            return ApiResponse.<T>builder().success(false).message(message).errorCode(errorCode).build();
        }
    }
}
