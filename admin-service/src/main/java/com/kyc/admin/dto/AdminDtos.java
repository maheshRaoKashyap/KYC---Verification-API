package com.kyc.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserSummary {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String status;
        private String kycStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashboardStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long inactiveUsers;
        private Long totalKyc;
        private Long pendingKyc;
        private Long verifiedKyc;
        private Long rejectedKyc;
        private LocalDateTime generatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KycAdminDetail {
        private Long kycId;
        private Long userId;
        private String email;
        private String fullName;
        private String status;
        private Boolean aadhaarVerified;
        private Boolean panVerified;
        private Boolean addressVerified;
        private String rejectionReason;
        private LocalDateTime createdAt;
        private LocalDateTime verifiedAt;
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

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}
