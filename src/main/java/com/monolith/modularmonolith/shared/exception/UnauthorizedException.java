package com.monolith.modularmonolith.shared.exception;

/**
 * Exception pour les identifiants invalides (HTTP 401).
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException() {
        super(ErrorCode.AUTH_001);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.AUTH_001, message);
    }
}