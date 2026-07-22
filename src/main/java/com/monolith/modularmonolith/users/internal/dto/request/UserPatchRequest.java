package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.Pattern;
import java.util.Set;

/**
 * DTO pour mise à jour partielle d'un utilisateur par un admin.
 */
public record UserPatchRequest(
        Boolean active,
        @Pattern(regexp = "^(STUDENT|TEACHER|ADMIN|SUPER_ADMIN|PARENT)$") String profileType,
        Set<@Pattern(regexp = "^(STUDENT|TEACHER|ADMIN|SUPER_ADMIN|PARENT)$") String> roles,
        Boolean emailVerified
) {}