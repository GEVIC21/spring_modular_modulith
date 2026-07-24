package com.monolith.modularmonolith.shared.exception;

/**
 * Exception pour les accès refusés (HTTP 403).
 */
public class ForbiddenException extends DomainException {

    public ForbiddenException() {
        super(ErrorCode.AUTH_004);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.AUTH_004, message);
    }
}