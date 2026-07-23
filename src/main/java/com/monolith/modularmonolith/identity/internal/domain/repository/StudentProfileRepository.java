package com.monolith.modularmonolith.identity.internal.domain.repository;

import com.monolith.modularmonolith.identity.internal.domain.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Port JPA pour l'entité StudentProfile.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    Optional<StudentProfile> findByUserId(Long userId);
}