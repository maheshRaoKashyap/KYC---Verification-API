package com.kyc.auth.repository;

import com.kyc.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    boolean existsByPanNumber(String panNumber);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    java.util.List<User> findAllActiveUsers();
}
