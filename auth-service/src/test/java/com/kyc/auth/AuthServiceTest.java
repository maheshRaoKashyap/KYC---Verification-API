package com.kyc.auth;

import com.kyc.auth.dto.AuthDtos.*;
import com.kyc.auth.entity.*;
import com.kyc.auth.exception.KycException;
import com.kyc.auth.repository.*;
import com.kyc.auth.service.AuthService;
import com.kyc.auth.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock ApiCredentialRepository apiCredentialRepository;
    @Mock AuditLogRepository auditLogRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock ApiKeyGenerator apiKeyGenerator;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;

    @InjectMocks AuthService authService;

    private RegisterRequest validRequest;
    private Role userRole;

    @BeforeEach
    void setUp() {
        validRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("SecurePass@123")
                .firstName("Rahul")
                .lastName("Sharma")
                .phone("9876543210")
                .panNumber("ABCDE1234F")
                .build();

        userRole = Role.builder()
                .id(1L)
                .name(Role.RoleName.ROLE_USER)
                .build();
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.existsByPanNumber(anyString())).thenReturn(false);

        when(roleRepository.findByName(Role.RoleName.ROLE_USER))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User saved = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Rahul")
                .lastName("Sharma")
                .roles(Set.of(userRole))
                .build();

        when(userRepository.save(any())).thenReturn(saved);

        when(apiKeyGenerator.generateApiKey()).thenReturn("kyc_key");
        when(apiKeyGenerator.generateAppId()).thenReturn("app_id");

        when(apiCredentialRepository.existsByApiKey(anyString())).thenReturn(false);
        when(apiCredentialRepository.existsByAppId(anyString())).thenReturn(false);

        when(apiCredentialRepository.save(any()))
                .thenReturn(ApiCredential.builder()
                        .apiKey("kyc_key")
                        .appId("app_id")
                        .build());

        UserDetails ud = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(ud);

        when(jwtUtil.generateToken(any(), any())).thenReturn("jwt_token");
        when(jwtUtil.getExpiration()).thenReturn(86400L);

        when(auditLogRepository.save(any())).thenReturn(new AuditLog());

        AuthResponse resp = authService.register(validRequest);

        assertThat(resp.getAccessToken()).isEqualTo("jwt_token");
        assertThat(resp.getApiKey()).isEqualTo("kyc_key");
    }

    @Test
    void register_duplicateEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validRequest))
                .isInstanceOf(KycException.class);
    }

    @Test
    void login_success() {
        LoginRequest req = LoginRequest.builder()
                .email("test@example.com")
                .password("pass")
                .build();

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(auth);

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Rahul")
                .lastName("Sharma")
                .roles(Set.of(userRole))
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails ud = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(ud);

        when(jwtUtil.generateToken(any(), any())).thenReturn("jwt_token");

        when(apiCredentialRepository.findByUserId(1L))
                .thenReturn(Optional.of(
                        ApiCredential.builder()
                                .apiKey("kyc_key")
                                .appId("app_id")
                                .build()
                ));

        when(auditLogRepository.save(any())).thenReturn(new AuditLog());

        AuthResponse resp = authService.login(req);

        assertThat(resp.getAccessToken()).isEqualTo("jwt_token");
    }

    @Test
    void login_badCredentials() {
        LoginRequest req = LoginRequest.builder()
                .email("x@x.com")
                .password("wrong")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad"));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void validateApiKey_valid() {
        ApiKeyValidationRequest req = new ApiKeyValidationRequest("k", "a");

        User user = User.builder()
                .id(1L)
                .email("e@e.com")
                .roles(Set.of(userRole))
                .build();

        ApiCredential cred = ApiCredential.builder()
                .apiKey("k")
                .appId("a")
                .active(true)
                .user(user)
                .build();

        when(apiCredentialRepository.findByApiKeyAndAppId("k", "a"))
                .thenReturn(Optional.of(cred));

        ApiKeyValidationResponse resp = authService.validateApiKey(req);

        assertThat(resp.isValid()).isTrue();
    }

    @Test
    void validateApiKey_invalid() {
        when(apiCredentialRepository.findByApiKeyAndAppId(any(), any()))
                .thenReturn(Optional.empty());

        ApiKeyValidationResponse resp =
                authService.validateApiKey(new ApiKeyValidationRequest("x", "y"));

        assertThat(resp.isValid()).isFalse();
    }
}