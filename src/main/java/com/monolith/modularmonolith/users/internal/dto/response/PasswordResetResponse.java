package com.monolith.modularmonolith.users.internal.dto.response;

public record PasswordResetResponse(
        String message,
        boolean emailSent,
        String temporaryPassword  // Uniquement si email échoue, à afficher à l'admin
) {}