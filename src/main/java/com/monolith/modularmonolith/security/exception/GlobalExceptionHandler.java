package com.monolith.modularmonolith.security.exception;

import com.monolith.modularmonolith.users.internal.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j // Lombok injecte automatiquement le logger professionnel 'log'
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Erreurs de validation d'entrée (DTO @Valid) -> 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Échec de la validation d'entrée sur l'URI: {} - Raisons: {}", request.getRequestURI(), errors);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "La validation des données a échoué.",
                request.getRequestURI(),
                null,
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. Erreurs d'arguments invalides (ex: votre e-mail déjà existant lance cette exception) -> 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Requête non valide reçue sur l'URI: {} - Message: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 3. Exception métier spécifique "Email déjà existant" -> 409 Conflict
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Tentative d'inscription avec un email déjà pris sur: {} - {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 4. Mauvais mot de passe ou identifiants erronés -> 401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Échec de tentative de connexion sur l'URI: {}", request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Identifiants invalides ou incorrects.", request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 5. Droits d'accès insuffisants (Rôles invalides) -> 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Accès refusé pour l'utilisateur sur l'URI: {}", request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "Vous n'avez pas les droits nécessaires pour accéder à cette ressource.", request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // 6. JSON malformé ou illisible envoyé par le client -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Le corps de la requête JSON est malformé sur: {}", request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, "Le corps de la requête HTTP (JSON) est manquant ou malformé.", request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 7. Mauvaise méthode HTTP appelée (ex: un GET au lieu d'un POST) -> 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Méthode HTTP non supportée ({}) sur l'URI: {}", ex.getMethod(), request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, "La méthode HTTP utilisée n'est pas supportée pour cet endpoint.", request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 8. Filet de sécurité ultime : Gestion des pannes inconnues du serveur -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString(); // Génère un jeton unique de suivi

        // Log TOUTE la stacktrace avec l'ID pour que le développeur puisse déboguer en arrière-plan
        log.error("--- ERREUR CRITIQUE INTERNE --- [ID Trace: {}] sur l'URI: {}", errorId, request.getRequestURI(), ex);

        // On masque la vraie erreur système au client pour éviter d'exposer des failles de sécurité
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur technique imprévue est survenue. Veuillez contacter le support en fournissant l'ID de suivi.",
                request.getRequestURI(),
                errorId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
