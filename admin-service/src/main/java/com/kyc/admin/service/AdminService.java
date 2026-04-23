package com.kyc.admin.service;

import com.kyc.admin.dto.AdminDtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    // Admin service reads directly from shared DB views for efficiency.
    // In fully isolated microservices, this would use Feign clients.
    private final JdbcTemplate jdbcTemplate;

    public List<UserSummary> getAllUsers(String statusFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT u.id, u.email, u.first_name, u.last_name, u.phone,
                   u.status, u.created_at, u.updated_at,
                   k.status as kyc_status
            FROM users u
            LEFT JOIN kyc_details k ON u.id = k.user_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append(" AND u.status = ?");
            params.add(statusFilter.toUpperCase());
        }
        sql.append(" ORDER BY u.created_at DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, row) ->
            UserSummary.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .phone(rs.getString("phone"))
                .status(rs.getString("status"))
                .kycStatus(rs.getString("kyc_status"))
                .createdAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .updatedAt(rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build()
        );
    }

    public UserSummary getUserById(Long id) {
        String sql = """
            SELECT u.id, u.email, u.first_name, u.last_name, u.phone,
                   u.status, u.created_at, u.updated_at,
                   k.status as kyc_status
            FROM users u
            LEFT JOIN kyc_details k ON u.id = k.user_id
            WHERE u.id = ?
            """;
        List<UserSummary> results = jdbcTemplate.query(sql, new Object[]{id}, (rs, row) ->
            UserSummary.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .phone(rs.getString("phone"))
                .status(rs.getString("status"))
                .kycStatus(rs.getString("kyc_status"))
                .createdAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build()
        );
        return results.stream().findFirst()
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public List<KycAdminDetail> getAllKycDetails(String statusFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, user_id, email, full_name, status,
                   aadhaar_verified, pan_verified, address_verified,
                   rejection_reason, created_at, verified_at
            FROM kyc_details WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append(" AND status = ?");
            params.add(statusFilter.toUpperCase());
        }
        sql.append(" ORDER BY created_at DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, row) ->
            KycAdminDetail.builder()
                .kycId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .fullName(rs.getString("full_name"))
                .status(rs.getString("status"))
                .aadhaarVerified(rs.getBoolean("aadhaar_verified"))
                .panVerified(rs.getBoolean("pan_verified"))
                .addressVerified(rs.getBoolean("address_verified"))
                .rejectionReason(rs.getString("rejection_reason"))
                .createdAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .verifiedAt(rs.getTimestamp("verified_at") != null
                    ? rs.getTimestamp("verified_at").toLocalDateTime() : null)
                .build()
        );
    }

    public DashboardStats getDashboardStats() {
        Long totalUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users", Long.class);
        Long activeUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'", Long.class);
        Long inactiveUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE status = 'INACTIVE'", Long.class);
        Long totalKyc = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM kyc_details", Long.class);
        Long pendingKyc = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM kyc_details WHERE status = 'PENDING'", Long.class);
        Long verifiedKyc = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM kyc_details WHERE status = 'VERIFIED'", Long.class);
        Long rejectedKyc = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM kyc_details WHERE status = 'REJECTED'", Long.class);

        return DashboardStats.builder()
            .totalUsers(totalUsers).activeUsers(activeUsers).inactiveUsers(inactiveUsers)
            .totalKyc(totalKyc).pendingKyc(pendingKyc)
            .verifiedKyc(verifiedKyc).rejectedKyc(rejectedKyc)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    public boolean suspendUser(Long userId) {
        int rows = jdbcTemplate.update(
            "UPDATE users SET status = 'SUSPENDED' WHERE id = ?", userId);
        return rows > 0;
    }

    public boolean reactivateUser(Long userId) {
        int rows = jdbcTemplate.update(
            "UPDATE users SET status = 'ACTIVE' WHERE id = ?", userId);
        return rows > 0;
    }
}
