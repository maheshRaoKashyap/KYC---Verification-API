package com.kyc.auth.service;

import com.kyc.auth.dto.AuthDtos.*;
import com.kyc.auth.entity.*;
import com.kyc.auth.exception.KycException;
import com.kyc.auth.repository.*;
import com.kyc.auth.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApiCredentialRepository apiCredentialRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ApiKeyGenerator apiKeyGenerator;
    private final AuthenticationManager authenticationManager;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate uniqueness
        if (userRepository.existsByEmail(request.getEmail()))
            throw KycException.conflict("Email already registered");
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone()))
            throw KycException.conflict("Phone number already registered");
        if (request.getAadhaarNumber() != null && userRepository.existsByAadhaarNumber(request.getAadhaarNumber()))
            throw KycException.conflict("Aadhaar number already registered");
        if (request.getPanNumber() != null && userRepository.existsByPanNumber(request.getPanNumber()))
            throw KycException.conflict("PAN number already registered");

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> KycException.notFound("Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .citizenship(request.getCitizenship())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .profession(request.getProfession())
                .aadhaarNumber(request.getAadhaarNumber())
                .panNumber(request.getPanNumber())
                .voterIdNumber(request.getVoterIdNumber())
                .roles(Set.of(userRole))
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        // Generate API credentials
        String apiKey = generateUniqueApiKey();
        String appId = generateUniqueAppId();

        ApiCredential credential = ApiCredential.builder()
                .user(savedUser)
                .apiKey(apiKey)
                .appId(appId)
                .active(true)
                .build();
        apiCredentialRepository.save(credential);

        // Generate JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", savedUser.getId());
        claims.put("roles", List.of(userRole.getName().name()));
        String token = jwtUtil.generateToken(userDetails, claims);

        auditLog(savedUser.getId(), "REGISTER", "AUTH", AuditLog.AuditStatus.SUCCESS, "User registered");

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .apiKey(apiKey)
                .appId(appId)
                .user(UserInfo.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .firstName(savedUser.getFirstName())
                        .lastName(savedUser.getLastName())
                        .role("ROLE_USER")
                        .build())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            auditLogAnon("LOGIN_FAILED", "AUTH", AuditLog.AuditStatus.FAILURE, request.getEmail());
            throw e;
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> KycException.notFound("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles().stream().map(r -> r.getName().name()).toList());
        String token = jwtUtil.generateToken(userDetails, claims);

        ApiCredential credential = apiCredentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> KycException.notFound("API credentials not found"));

        auditLog(user.getId(), "LOGIN", "AUTH", AuditLog.AuditStatus.SUCCESS, "User logged in");

        String primaryRole = user.getRoles().stream().findFirst()
                .map(r -> r.getName().name()).orElse("ROLE_USER");

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .apiKey(credential.getApiKey())
                .appId(credential.getAppId())
                .user(UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(primaryRole)
                        .build())
                .build();
    }

    @Transactional
    public ApiKeyValidationResponse validateApiKey(ApiKeyValidationRequest request) {
        Optional<ApiCredential> credOpt = apiCredentialRepository
                .findByApiKeyAndAppId(request.getApiKey(), request.getAppId());

        if (credOpt.isEmpty() || !credOpt.get().isActive()) {
            return ApiKeyValidationResponse.builder().valid(false).build();
        }

        ApiCredential cred = credOpt.get();
        cred.setLastUsedAt(LocalDateTime.now());
        apiCredentialRepository.save(cred);

        User user = cred.getUser();
        String role = user.getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("ROLE_USER");

        return ApiKeyValidationResponse.builder()
                .valid(true)
                .userId(user.getId())
                .email(user.getEmail())
                .role(role)
                .build();
    }

    private String generateUniqueApiKey() {
        String key;
        do { key = apiKeyGenerator.generateApiKey(); }
        while (apiCredentialRepository.existsByApiKey(key));
        return key;
    }

    private String generateUniqueAppId() {
        String id;
        do { id = apiKeyGenerator.generateAppId(); }
        while (apiCredentialRepository.existsByAppId(id));
        return id;
    }

    private void auditLog(Long userId, String action, String resource,
                          AuditLog.AuditStatus status, String details) {
        auditLogRepository.save(AuditLog.builder()
                .userId(userId).action(action).resource(resource)
                .status(status).details(details).build());
    }

    private void auditLogAnon(String action, String resource,
                               AuditLog.AuditStatus status, String details) {
        auditLogRepository.save(AuditLog.builder()
                .action(action).resource(resource)
                .status(status).details(details).build());
    }
}
