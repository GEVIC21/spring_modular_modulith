package com.monolith.modularmonolith.authentication.internal.dto;

import java.util.Set;

public record AuthResponse(
        String token,
        String email,
        String username,
        Set<String> roles
) {}