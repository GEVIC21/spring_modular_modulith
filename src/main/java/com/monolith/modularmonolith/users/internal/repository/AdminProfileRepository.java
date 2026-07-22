package com.monolith.modularmonolith.users.internal.repository;

import com.monolith.modularmonolith.users.internal.model.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    Optional<AdminProfile> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);

    long countBy();
}