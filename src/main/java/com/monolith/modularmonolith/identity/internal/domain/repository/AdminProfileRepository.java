package com.monolith.modularmonolith.identity.internal.domain.repository;

import com.monolith.modularmonolith.identity.internal.domain.model.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Port JPA pour l'entité AdminProfile.
 */
@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    Optional<AdminProfile> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);

    Optional<AdminProfile> findByUserId(Long userId);
}