package com.kyc.auth.controller;

import com.kyc.auth.dto.ApiResponse;
import com.kyc.auth.dto.AuthDtos.*;
import com.kyc.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and API key management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates user, generates JWT and API credentials")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive JWT + API credentials")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/validate-api-key")
    @Operation(summary = "Validate API Key", description = "Internal endpoint to validate X-API-KEY + X-APP-ID")
    public ResponseEntity<ApiResponse<ApiKeyValidationResponse>> validateApiKey(
            @RequestBody ApiKeyValidationRequest request) {
        ApiKeyValidationResponse response = authService.validateApiKey(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Validation complete"));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth Service is UP", "OK"));
    }
}
