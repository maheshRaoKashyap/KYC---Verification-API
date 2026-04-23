package com.kyc.auth.repository;

import com.kyc.auth.entity.ApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiCredentialRepository extends JpaRepository<ApiCredential, Long> {
    Optional<ApiCredential> findByApiKeyAndAppId(String apiKey, String appId);
    Optional<ApiCredential> findByUserId(Long userId);
    boolean existsByApiKey(String apiKey);
    boolean existsByAppId(String appId);
}
