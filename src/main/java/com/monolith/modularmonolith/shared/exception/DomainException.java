package com.monolith.modularmonolith.shared.exception;

import lombok.Getter;

/**
 * Classe abstraite de base pour toutes les exceptions métier.
 * Porte un ErrorCode et un message personnalisable.
 */
@Getter
public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    protected DomainException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    protected DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}