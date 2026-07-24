package com.monolith.modularmonolith.identity.internal.dto.response;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO allégé pour les listes d'utilisateurs.
 */
@Builder
public record UserSummaryResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String avatarUrl,
        ProfileType profileType,
        Set<Role> roles,
        boolean active,
        LocalDateTime createdAt
) {
}