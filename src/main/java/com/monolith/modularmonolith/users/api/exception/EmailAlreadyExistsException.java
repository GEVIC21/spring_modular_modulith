package com.monolith.modularmonolith.users.api.exception;

/**
 * Exception métier : email déjà utilisé.
 *
 * ✅ Dans api.exception car le module auth (inscription)
 *    doit la connaître pour renvoyer une 409 Conflict.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email déjà utilisé: " + email);
    }
}