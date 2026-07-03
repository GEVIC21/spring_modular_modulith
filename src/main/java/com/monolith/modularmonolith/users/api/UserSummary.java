package com.monolith.modularmonolith.users.api;

import java.util.Set;

public record UserSummary(
        Long id,
        String username,
        String email,
        Set<String> roles
) {}