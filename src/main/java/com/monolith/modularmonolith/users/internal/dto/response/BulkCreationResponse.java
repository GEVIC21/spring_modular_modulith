package com.monolith.modularmonolith.users.internal.dto.response;

import java.util.List;

public record BulkCreationResponse(
        int totalProcessed,
        int successCount,
        int errorCount,
        List<String> errors,
        List<Long> createdUserIds
) {}