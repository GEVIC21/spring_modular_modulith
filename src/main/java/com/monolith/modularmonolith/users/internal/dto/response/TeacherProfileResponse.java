package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record TeacherProfileResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        boolean active,
        Set<String> roles,
        Set<String> permissions,
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
