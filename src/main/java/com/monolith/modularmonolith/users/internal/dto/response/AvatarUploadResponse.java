package com.monolith.modularmonolith.users.internal.dto.response;

import java.time.Instant;

public record AvatarUploadResponse(
        String message,
        String filename,
        String avatarUrl,
        String thumbnailUrl,
        Instant uploadedAt
) {
    public AvatarUploadResponse(String message, String filename, String avatarUrl, String thumbnailUrl) {
        this(message, filename, avatarUrl, thumbnailUrl, Instant.now());
    }
}