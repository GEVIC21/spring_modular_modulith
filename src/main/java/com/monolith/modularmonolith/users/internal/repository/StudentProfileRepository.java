package com.monolith.modularmonolith.users.internal.repository;

import com.monolith.modularmonolith.users.internal.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    long countBy();
}