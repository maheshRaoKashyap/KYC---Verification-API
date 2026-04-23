package com.kyc.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 8)
        private String password;
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @Pattern(regexp = "^[6-9]\\d{9}$")
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
    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private String apiKey;
        private String appId;
        private UserInfo user;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiKeyValidationRequest {
        private String apiKey;
        private String appId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiKeyValidationResponse {
        private boolean valid;
        private Long userId;
        private String email;
        private String role;
    }
}
