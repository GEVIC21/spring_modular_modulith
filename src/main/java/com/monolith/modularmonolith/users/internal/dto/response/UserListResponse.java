package com.monolith.modularmonolith.users.internal.dto.response;

import java.util.Set;

public record UserListResponse(
        Long id,
        String username,
        String email,
        boolean active,
        Set<String> roles,
        String profileType
) {}