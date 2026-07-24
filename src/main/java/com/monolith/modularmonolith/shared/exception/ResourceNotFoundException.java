package com.monolith.modularmonolith.shared.exception;

/**
 * Exception pour les ressources introuvables (HTTP 404).
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(ErrorCode.GEN_002, resourceName + " introuvable avec l'identifiant: " + identifier);
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}