package com.monolith.modularmonolith.identity.api;

import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import lombok.Builder;

import java.util.Set;

/**
 * DTO technique pour Spring Security.
 * Contient le mot de passe hashé — ne doit jamais être exposé dans une API REST.
 */
@Builder
public record UserSecurityInfo(
        String email,
        String password,
        Set<Role> roles,
        boolean active
) {}