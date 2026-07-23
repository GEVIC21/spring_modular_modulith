package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO allégé pour l'affichage en liste (admin, recherche).
 */
public record UserProfileSummaryResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String displayName,
        String profileType,
        String gradeLevel,
        String department,
        boolean active,
        String avatarUrl,
        Set<String> roles,
        LocalDateTime createdAt
) {}