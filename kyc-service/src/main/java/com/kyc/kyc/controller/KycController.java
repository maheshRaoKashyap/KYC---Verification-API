package com.kyc.kyc.controller;

import com.kyc.kyc.dto.KycDtos.*;
import com.kyc.kyc.entity.KycDetail;
import com.kyc.kyc.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC Management", description = "KYC submission, retrieval and status management")
@SecurityRequirement(name = "bearerAuth")
public class KycController {

    private final KycService kycService;

    @PostMapping
    @Operation(summary = "Submit KYC", description = "Create a new KYC record for a user")
    public ResponseEntity<ApiResponse<KycResponse>> createKyc(@Valid @RequestBody CreateKycRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(kycService.createKyc(request), "KYC submitted successfully"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get KYC by user ID")
    public ResponseEntity<ApiResponse<KycResponse>> getKyc(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getKycByUserId(userId), "KYC fetched"));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update KYC details")
    public ResponseEntity<ApiResponse<KycResponse>> updateKyc(
            @PathVariable Long userId,
            @RequestBody UpdateKycRequest request) {
        return ResponseEntity.ok(ApiResponse.success(kycService.updateKyc(userId, request), "KYC updated"));
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_OFFICER')")
    @Operation(summary = "Update KYC status (Admin/Officer only)")
    public ResponseEntity<ApiResponse<KycResponse>> updateKycStatus(
            @PathVariable Long userId,
            @RequestBody UpdateKycStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(kycService.updateKycStatus(userId, request), "KYC status updated"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all KYC records (Admin only)")
    public ResponseEntity<ApiResponse<List<KycResponse>>> getAllKycs(
            @RequestParam(required = false) KycDetail.KycStatus status) {
        List<KycResponse> result = status != null
            ? kycService.getKycsByStatus(status)
            : kycService.getAllKycs();
        return ResponseEntity.ok(ApiResponse.success(result, "KYC records fetched"));
    }
}
