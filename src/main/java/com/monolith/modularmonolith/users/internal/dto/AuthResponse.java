package com.monolith.modularmonolith.users.internal.dto;

public record AuthResponse(
        String token,
        String email,
        String username // <-- Doit avoir 3 paramètres
) {}
