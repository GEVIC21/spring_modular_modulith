package com.monolith.modularmonolith.identity.internal.dto.response;

import lombok.Builder;

/**
 * DTO de résultat de réinitialisation de mot de passe.
 */
@Builder
public record PasswordResetResult(
        String message,
        boolean emailSent,
        String temporaryPassword
) {
}