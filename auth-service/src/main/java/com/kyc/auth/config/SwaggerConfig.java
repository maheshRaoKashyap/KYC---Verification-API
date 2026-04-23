package com.kyc.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "KYC Platform - Auth Service API",
        version = "1.0.0",
        description = "Authentication and Authorization Service for the KYC Verification Platform",
        contact = @Contact(name = "KYC Platform Team", email = "support@kycplatform.com")
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Auth Service Direct"),
        @Server(url = "http://localhost:8080", description = "API Gateway")
    },
    security = {
        @SecurityRequirement(name = "bearerAuth"),
        @SecurityRequirement(name = "apiKey")
    }
)
@SecuritySchemes({
    @SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
    ),
    @SecurityScheme(
        name = "apiKey",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-KEY"
    )
})
public class SwaggerConfig {}
