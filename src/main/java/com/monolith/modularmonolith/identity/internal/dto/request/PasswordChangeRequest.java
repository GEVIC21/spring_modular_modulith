package com.monolith.modularmonolith.identity.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO de changement de mot de passe.
 */
@Builder
public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, max = 100) String newPassword,
        @NotBlank String confirmPassword
) {
}