package com.monolith.modularmonolith.identity.internal.domain.repository;

import com.monolith.modularmonolith.identity.internal.domain.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Port JPA pour l'entité TeacherProfile.
 */
@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByTeacherId(String teacherId);

    boolean existsByTeacherId(String teacherId);

    Optional<TeacherProfile> findByUserId(Long userId);

    Optional<TeacherProfile> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}