package com.kyc.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kyc.user.entity.UserProfile;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class UserDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserProfileResponse {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String dateOfBirth;
        private String gender;
        private String citizenship;
        private String address;
        private String city;
        private String state;
        private String profession;
        private String aadhaarNumber;
        private String panNumber;
        private String voterIdNumber;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static UserProfileResponse from(UserProfile u) {
            return UserProfileResponse.builder()
                .id(u.getId()).email(u.getEmail())
                .firstName(u.getFirstName()).lastName(u.getLastName())
                .phone(u.getPhone()).dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender()).citizenship(u.getCitizenship())
                .address(u.getAddress()).city(u.getCity()).state(u.getState())
                .profession(u.getProfession())
                .aadhaarNumber(maskAadhaar(u.getAadhaarNumber()))
                .panNumber(u.getPanNumber())
                .voterIdNumber(u.getVoterIdNumber())
                .status(u.getStatus() != null ? u.getStatus().name() : null)
                .createdAt(u.getCreatedAt()).updatedAt(u.getUpdatedAt())
                .build();
        }

        private static String maskAadhaar(String aadhaar) {
            if (aadhaar == null || aadhaar.length() < 4) return aadhaar;
            return "XXXX-XXXX-" + aadhaar.replaceAll("\\s","").substring(aadhaar.replaceAll("\\s","").length() - 4);
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
        private String phone;
        private String dateOfBirth;
        private String gender;
        private String citizenship;
        private String address;
        private String city;
        private String state;
        private String profession;
        @Pattern(regexp = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$|^[2-9]{1}[0-9]{11}$")
        private String aadhaarNumber;
        @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$")
        private String panNumber;
        private String voterIdNumber;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private String errorCode;
        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();

        public static <T> ApiResponse<T> success(T data, String message) {
            return ApiResponse.<T>builder().success(true).message(message).data(data).build();
        }
        public static <T> ApiResponse<T> error(String message, String errorCode) {
            return ApiResponse.<T>builder().success(false).message(message).errorCode(errorCode).build();
        }
    }
}
