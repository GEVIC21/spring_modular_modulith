package com.monolith.modularmonolith.users.api.exception;

/**
 * Exception métier : utilisateur non trouvé.
 *
 * ✅ Dans api.exception car d'autres modules (auth, order, etc.)
 *    doivent pouvoir la catcher et réagir.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("Utilisateur non trouvé: " + userId);
    }
}