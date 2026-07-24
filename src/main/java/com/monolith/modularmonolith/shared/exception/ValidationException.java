package com.monolith.modularmonolith.shared.exception;

/**
 * Exception pour les erreurs de validation (HTTP 422).
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(ErrorCode.VALID_001, message);
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}