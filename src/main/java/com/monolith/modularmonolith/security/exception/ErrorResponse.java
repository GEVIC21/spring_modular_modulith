package com.monolith.modularmonolith.security.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // N'affiche pas les champs nulls (comme errors) dans le JSON final
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String errorId, // ID unique pour corréler la réponse client avec les logs du serveur
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    // Constructeur d'usine rapide pour les erreurs standards
    public static ErrorResponse of(org.springframework.http.HttpStatus status, String message, String path, String errorId) {
        return new ErrorResponse(status.value(), status.getReasonPhrase(), message, path, errorId, LocalDateTime.now(), null);
    }
}
