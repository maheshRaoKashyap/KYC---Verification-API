package com.kyc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyc.auth.controller.AuthController;
import com.kyc.auth.dto.AuthDtos.*;
import com.kyc.auth.service.AuthService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testRegisterReturns201() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("Password@123")
                .firstName("Priya")
                .lastName("Sharma")
                .build();

        AuthResponse mockResponse = AuthResponse.builder()
                .accessToken("jwt.token.here")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .apiKey("kyc_key")
                .appId("app_id")
                .user(UserInfo.builder()
                        .id(1L)
                        .email("newuser@example.com")
                        .build())
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").value("jwt.token.here"));
    }

    @Test
    void testLoginReturns200() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("Password@123")
                .build();

        AuthResponse mockResponse = AuthResponse.builder()
                .accessToken("login.jwt.token")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("login.jwt.token"));
    }
}