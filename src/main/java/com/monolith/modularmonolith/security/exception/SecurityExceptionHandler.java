package com.monolith.modularmonolith.security.exception;

import com.monolith.modularmonolith.security.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire d'exceptions du module SECURITY.
 * Capture UNIQUEMENT les exceptions du package security et les exceptions
 * d'authentification/autorisation globales.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.monolith.modularmonolith.security")
public class SecurityExceptionHandler {

    // ========== AUTHENTIFICATION ==========

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Échec connexion sur {} : identifiants invalides", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED,
                        "Email ou mot de passe incorrect.", request.getRequestURI(), null));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex, HttpServletRequest request) {
        log.warn("Compte désactivé tentative connexion sur {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN,
                        "Votre compte a été désactivé. Contactez l'administration.", request.getRequestURI(), null));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(
            LockedException ex, HttpServletRequest request) {
        log.warn("Compte verrouillé tentative connexion sur {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN,
                        "Votre compte est temporairement verrouillé.", request.getRequestURI(), null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Erreur authentification sur {} : {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED,
                        "Authentification requise.", request.getRequestURI(), null));
    }

    // ========== AUTORISATION ==========

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Accès refusé sur {} pour l'utilisateur", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN,
                        "Vous n'avez pas les droits nécessaires pour accéder à cette ressource.", request.getRequestURI(), null));
    }

    // ========== VALIDATION ==========

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        });
        log.warn("Validation échouée sur {} : {}", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(ErrorResponse.of(HttpStatus.BAD_REQUEST,
                "La validation des données a échoué.", request.getRequestURI(), null, errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argument invalide sur {} : {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST,
                        ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON malformé sur {}", request.getRequestURI());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "Le corps de la requête est malformé ou incomplet.", request.getRequestURI(), null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Méthode {} non supportée sur {}", ex.getMethod(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED,
                        "Méthode HTTP " + ex.getMethod() + " non supportée.", request.getRequestURI(), null));
    }

    // ========== ERREURS CRITIQUES ==========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("--- ERREUR CRITIQUE SECURITY --- [ID: {}] sur {}", errorId, request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Une erreur technique imprévue est survenue. ID de suivi: " + errorId,
                        request.getRequestURI(), errorId));
    }
}