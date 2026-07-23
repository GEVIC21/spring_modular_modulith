package com.monolith.modularmonolith.users.api.exception;

/**
 * Exception métier : accès non autorisé à une ressource.
 *
 * ✅ Dans api.exception car c'est une exception transverse.
 *    N'importe quel module peut la lancer (vérification de propriété).
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}