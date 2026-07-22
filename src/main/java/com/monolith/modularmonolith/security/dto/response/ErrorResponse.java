package com.monolith.modularmonolith.security.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO standardisé pour les réponses d'erreur du module Security.
 * Utilisé par le SecurityExceptionHandler.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private String traceId;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

    /**
     * Factory method pour créer une réponse d'erreur simple.
     */
    public static ErrorResponse of(HttpStatus status, String message, String path, String traceId) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Factory method pour créer une réponse d'erreur avec validation errors.
     */
    public static ErrorResponse of(HttpStatus status, String message, String path, String traceId,
                                   Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();
    }
}