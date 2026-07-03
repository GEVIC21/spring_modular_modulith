package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record StudentProfileResponse(
        Long userId,
        String username,
        String email,
        String avatarUrl,
        boolean active,
        Set<String> roles,
        Set<String> permissions,

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