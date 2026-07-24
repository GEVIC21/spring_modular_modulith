package com.monolith.modularmonolith.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * DTO de réponse standardisée pour toutes les erreurs API.
 */
@Builder
@JsonInclude(NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}