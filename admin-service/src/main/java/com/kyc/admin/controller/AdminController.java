package com.kyc.admin.controller;

import com.kyc.admin.dto.AdminDtos.*;
import com.kyc.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin operations for user and KYC monitoring")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Platform dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
            adminService.getDashboardStats(), "Dashboard stats fetched"));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Filter by status: ACTIVE, INACTIVE, SUSPENDED")
    public ResponseEntity<ApiResponse<List<UserSummary>>> getAllUsers(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
            adminService.getAllUsers(status), "Users fetched"));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get a specific user with KYC status")
    public ResponseEntity<ApiResponse<UserSummary>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            adminService.getUserById(id), "User fetched"));
    }

    @PutMapping("/users/{id}/suspend")
    @Operation(summary = "Suspend a user account")
    public ResponseEntity<ApiResponse<Void>> suspendUser(@PathVariable Long id) {
        boolean done = adminService.suspendUser(id);
        return done
            ? ResponseEntity.ok(ApiResponse.success(null, "User suspended"))
            : ResponseEntity.badRequest().body(ApiResponse.error("User not found", "NOT_FOUND"));
    }

    @PutMapping("/users/{id}/reactivate")
    @Operation(summary = "Reactivate a suspended user")
    public ResponseEntity<ApiResponse<Void>> reactivateUser(@PathVariable Long id) {
        boolean done = adminService.reactivateUser(id);
        return done
            ? ResponseEntity.ok(ApiResponse.success(null, "User reactivated"))
            : ResponseEntity.badRequest().body(ApiResponse.error("User not found", "NOT_FOUND"));
    }

    @GetMapping("/kyc")
    @Operation(summary = "Get all KYC records", description = "Filter by status: PENDING, VERIFIED, REJECTED")
    public ResponseEntity<ApiResponse<List<KycAdminDetail>>> getAllKycs(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
            adminService.getAllKycDetails(status), "KYC records fetched"));
    }
}
