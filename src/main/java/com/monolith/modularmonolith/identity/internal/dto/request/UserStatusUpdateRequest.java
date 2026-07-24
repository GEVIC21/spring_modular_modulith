package com.monolith.modularmonolith.identity.internal.dto.request;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

/**
 * DTO de mise à jour partielle d'un utilisateur par un administrateur.
 * Tous les champs sont optionnels.
 */
@Builder
public record UserStatusUpdateRequest(
        Boolean active,
        ProfileType profileType,
        Set<Role> roles,
        @Email @Size(max = 255) String email,
        Boolean emailVerified
) {
}