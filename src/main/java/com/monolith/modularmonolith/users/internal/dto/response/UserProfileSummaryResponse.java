package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UserProfileSummaryResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String profileType,
        String avatarUrl,
        boolean active,
        Set<String> roles,
        String gradeLevel,      // Pour élèves
        String department,      // Pour enseignants/admins
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {}