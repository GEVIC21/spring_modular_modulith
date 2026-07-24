package com.monolith.modularmonolith.identity.internal.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * DTO de résultat d'import massif d'utilisateurs.
 */
@Builder
public record BulkImportResult(
        int totalProcessed,
        int successCount,
        int failureCount,
        List<String> errors
) {
}