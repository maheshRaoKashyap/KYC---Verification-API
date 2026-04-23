package com.kyc.kyc.repository;

import com.kyc.kyc.entity.KycDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface KycDetailRepository extends JpaRepository<KycDetail, Long> {
    Optional<KycDetail> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<KycDetail> findByStatus(KycDetail.KycStatus status);

    @Query("SELECT k FROM KycDetail k WHERE k.status = 'PENDING' ORDER BY k.createdAt ASC")
    List<KycDetail> findPendingKycs();

    @Query("SELECT COUNT(k) FROM KycDetail k WHERE k.status = :status")
    Long countByStatus(KycDetail.KycStatus status);
}
