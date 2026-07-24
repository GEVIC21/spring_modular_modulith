package com.monolith.modularmonolith.authentication.internal.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record AuthResponse(
        String token,
        String email,
        String username,
        Set<String> roles
) {}