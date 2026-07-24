package com.monolith.modularmonolith.identity.api;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserSummary(
        Long id,
        String email,
        String username,
        String fullName,
        ProfileType profileType,
        Set<Role> roles,
        boolean active
) {}