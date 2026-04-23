package com.kyc.auth.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateApiKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return "kyc_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String generateAppId() {
        byte[] randomBytes = new byte[12];
        secureRandom.nextBytes(randomBytes);
        return "app_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
