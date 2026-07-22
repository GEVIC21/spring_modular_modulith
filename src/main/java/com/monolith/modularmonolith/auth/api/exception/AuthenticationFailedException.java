package com.monolith.modularmonolith.auth.api.exception;

/**
 * Exception métier unchecked pour l'authentification.
 * Le module appelant peut la catcher ou laisser passer pour une 401.
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}