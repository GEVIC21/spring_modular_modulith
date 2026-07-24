package com.monolith.modularmonolith.shared.exception;

/**
 * Exception pour les conflits de données (HTTP 409).
 * Ex: email déjà existant.
 */
public class ConflictException extends DomainException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}