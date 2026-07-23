package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.response.PasswordResetResult;

/**
 * Port inbound : Réinitialisation du mot de passe d'un utilisateur.
 * Génère un mot de passe temporaire et publie un événement.
 */
public interface ResetPasswordUseCase {

    PasswordResetResult execute(Long userId);
}