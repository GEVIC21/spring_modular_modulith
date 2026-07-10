package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO unifié pour le profil de l'utilisateur connecté (/api/v1/users/me).
 * Contient les champs communs à tous les rôles + des blocs optionnels
 * spécifiques selon le profil (student / teacher).
 */
public record MeResponse(
        Long id,
        String username,
        String email,
        boolean active,
        Set<String> roles,
        String profileType,
        String avatarUrl,
        StudentInfo student,
        TeacherInfo teacher
) {

    public record StudentInfo(
            String studentId,
            String registrationNumber,
            String gradeLevel,
            String className,
            String section,
            String academicYear,
            LocalDate birthDate,
            String parentName,
            String parentPhone,
            String parentEmail,
            String emergencyContact,
            String address,
            LocalDate enrollmentDate,
            boolean scholarship
    ) {}

    public record TeacherInfo(
            String teacherId,
            String department,
            String specialization,
            Set<String> subjects,
            LocalDate hireDate,
            String qualification,
            String phone,
            String officeLocation,
            String bio,
            boolean tenured
    ) {}
}
