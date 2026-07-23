package com.monolith.modularmonolith.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details,
        Instant timestamp
) {
    public ApiErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null, Instant.now());
    }

    public ApiErrorResponse(int status, String error, String message, String path, Map<String, String> details) {
        this(status, error, message, path, details, Instant.now());
    }
}